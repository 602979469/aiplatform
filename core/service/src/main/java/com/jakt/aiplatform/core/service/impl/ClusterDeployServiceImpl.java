package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.integration.k8s.K8sClient;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationException;
import com.jakt.aiplatform.common.integration.ssh.SshClient;
import com.jakt.aiplatform.common.integration.ssh.SshResult;
import com.jakt.aiplatform.core.model.domain.ClusterPodConfig;
import com.jakt.aiplatform.core.service.ClusterCiProperties;
import com.jakt.aiplatform.core.service.ClusterDeployService;
import com.jakt.aiplatform.core.service.ClusterPodConfigService;
import org.yaml.snakeyaml.LoaderOptions;
import org.yaml.snakeyaml.Yaml;
import org.yaml.snakeyaml.error.YAMLException;
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

/**
 * 业务 pod 部署领域服务实现：远程编排脚本执行，异常统一由集成层记 INTEGRATION 日志。
 */
@Service
public class ClusterDeployServiceImpl implements ClusterDeployService {

    /** SSH 执行超时（秒），构建可能耗时较长。 */
    private static final long DEPLOY_TIMEOUT_SECONDS = 1800;

    /** K8s 客户端（同名 Deployment 校验）。 */
    private final K8sClient k8sClient;

    /** SSH 客户端。 */
    private final SshClient sshClient;

    /** cluster-ci 配置。 */
    private final ClusterCiProperties ciProperties;

    /** 配置领域服务（更新 last_built_commit）。 */
    private final ClusterPodConfigService clusterPodConfigService;

    public ClusterDeployServiceImpl(K8sClient k8sClient,
                                    SshClient sshClient,
                                    ClusterCiProperties ciProperties,
                                    ClusterPodConfigService clusterPodConfigService) {
        this.k8sClient = k8sClient;
        this.sshClient = sshClient;
        this.ciProperties = ciProperties;
        this.clusterPodConfigService = clusterPodConfigService;
    }

    @Override
    @Retryable(
            retryFor = {AiPlatformException.class, AiIntegrationException.class},
            maxAttempts = 3,
            backoff = @Backoff(delay = 3000, multiplier = 2))
    public void deploy(ClusterPodConfig config) {
        try {
            doDeploy(config);
            // 构建成功 → 发布
            clusterPodConfigService.markBuildResult(config.getId(), true);
        } catch (Exception e) {
            // 重试耗尽或一次性失败 → 构建失败（BUILD_FAILED）
            clusterPodConfigService.markBuildResult(config.getId(), false);
            throw e;
        }
    }

    /**
     * 执行一次部署（构建 + 导入 + apply），失败抛异常由 {@link #deploy} 统一置状态。
     *
     * @param config 业务 pod 配置
     */
    private void doDeploy(ClusterPodConfig config) {
        String deploymentName = resolveDeploymentName(config);
        String appDir = ciProperties.getWorkDir() + "/apps/" + config.getId();

        // 1. 校验：集群中已有同名 Deployment 则拒绝（PRD：只判断集群里面有没有了）
        AssertUtil.throwErrWhenTrue(
                k8sClient.getDeployment(config.getNamespace(), deploymentName) != null,
                ErrorCodeEnum.PARAM_INVALID,
                "集群中已存在同名 Deployment，请先删除或停用后再部署: " + deploymentName);

        // 2. 准备文件（本地临时目录 → 上传到 master 挂载目录）
        String remoteDockerfile = appDir + "/Dockerfile";
        String remoteDeployYaml = appDir + "/deployment.yaml";
        String remoteSrcDir = appDir + "/src";
        try {
            Path localDir = Files.createTempDirectory("cluster-deploy-");
            Path localDockerfile = localDir.resolve("Dockerfile");
            Path localDeployYaml = localDir.resolve("deployment.yaml");
            Files.writeString(localDockerfile, config.getDockerfile(), StandardCharsets.UTF_8);
            Files.writeString(localDeployYaml, config.getDeployYaml(), StandardCharsets.UTF_8);

            sshClient.execute(ciProperties.getMasterHost(), "mkdir -p " + appDir + " " + remoteSrcDir, 60);
            sshClient.uploadFile(ciProperties.getMasterHost(), localDockerfile.toString(), remoteDockerfile);
            sshClient.uploadFile(ciProperties.getMasterHost(), localDeployYaml.toString(), remoteDeployYaml);
        } catch (IOException e) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR, "部署文件准备失败: " + e.getMessage());
        }

        // 3. 拉取源码（git 浅克隆，直接用用户填写的 git 地址，私有仓库地址自带 token；输出 tee 到构建日志）
        //    log_dir 传 appDir，fetch 输出写 apps/{configId}/fetch.log
        SshResult fetchResult = sshClient.execute(ciProperties.getMasterHost(),
                "bash " + ciProperties.getWorkDir() + "/bin/fetch_source.sh "
                        + "'" + config.getGitUrl() + "' " + config.getGitBranch() + " " + remoteSrcDir
                        + " " + remoteDockerfile + " " + appDir,
                600);
        if (!fetchResult.isSuccess()) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR,
                    "源码拉取失败: " + shortOutput(fetchResult.getOutput()));
        }
        // 4. 镜像 tag = commit 短哈希（脚本最后一行输出，用户不接触镜像版本号）
        String imageTag = fetchResult.getOutput().trim();
        AssertUtil.throwErrWhenBlank(imageTag, ErrorCodeEnum.SYSTEM_ERROR, "镜像 tag 为空");

        // 5. 构建 + 部署一体（build_deploy.sh：双架构构建导入 + apply + set image + rollout，全程 tee 日志）
        SshResult deployResult = sshClient.execute(ciProperties.getMasterHost(),
                "bash " + ciProperties.getWorkDir() + "/bin/build_deploy.sh "
                        + config.getPodName() + " " + imageTag + " " + remoteSrcDir + " "
                        + appDir + " " + config.getNamespace() + " " + remoteDeployYaml
                        + " " + ciProperties.getWorkerHost(),
                DEPLOY_TIMEOUT_SECONDS);
        if (!deployResult.isSuccess()) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR,
                    "构建部署失败: " + shortOutput(deployResult.getOutput()));
        }

        // 7. 记录本次构建 commit（自动刷新比对用）
        ClusterPodConfig update = new ClusterPodConfig();
        update.setId(config.getId());
        update.setLastBuiltCommit(imageTag);
        clusterPodConfigService.updateByCondition(update);

        LoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                "业务pod部署完成 id={} deployment={} image={}:{}",
                config.getId(), deploymentName, config.getPodName(), imageTag);
    }

    @Override
    public String resolveDeploymentName(ClusterPodConfig config) {
        String fromYaml = parseDeploymentName(config.getDeployYaml());
        return StrUtil.isNotBlank(fromYaml) ? fromYaml : config.getPodName();
    }

    @Override
    public void deleteInstance(ClusterPodConfig config) {
        AssertUtil.throwErrWhenBlank(config.getDeployYaml(), ErrorCodeEnum.PARAM_INVALID,
                "Deployment YAML 为空，无法删除实例");
        // 按配置 deployYaml 删除全部资源（Deployment/Service/Ingress），不删配置行
        k8sClient.deleteByYaml(config.getDeployYaml());
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "业务pod实例已删除 id={} podName={}",
                config.getId(), config.getPodName());
    }

    @Override
    public String getBuildLog(Long configId) {
        String appDir = ciProperties.getWorkDir() + "/apps/" + configId;
        SshResult result = sshClient.execute(ciProperties.getMasterHost(),
                "ls -t " + appDir + "/fetch.log " + appDir + "/deploy-*.log 2>/dev/null | head -1",
                60);
        if (!result.isSuccess() || StrUtil.isBlank(result.getOutput())) {
            return "";
        }
        String latestLog = result.getOutput().trim().split("\\n")[0];
        SshResult logResult = sshClient.execute(ciProperties.getMasterHost(),
                "cat " + latestLog, 60);
        return logResult.isSuccess() ? logResult.getOutput() : "";
    }

    /**
     * 从 deployYaml 解析第一个 Deployment 的 metadata.name（用户 YAML 为准）。
     *
     * @param deployYaml Deployment YAML
     * @return Deployment 名称；解析失败返回 null
     */
    @SuppressWarnings("unchecked")
    private String parseDeploymentName(String deployYaml) {
        if (StrUtil.isBlank(deployYaml)) {
            return null;
        }
        try {
            LoaderOptions loaderOptions = new LoaderOptions();
            loaderOptions.setAllowDuplicateKeys(false);
            for (Object document : new Yaml(loaderOptions).loadAll(deployYaml)) {
                if (!(document instanceof Map)) {
                    continue;
                }
                Map<String, Object> root = (Map<String, Object>) document;
                if (!"Deployment".equals(root.get("kind"))) {
                    continue;
                }
                Object metadata = root.get("metadata");
                if (metadata instanceof Map) {
                    Object name = ((Map<String, Object>) metadata).get("name");
                    if (name != null) {
                        return String.valueOf(name);
                    }
                }
            }
        } catch (YAMLException e) {
            LoggerUtil.warn(LogFileEnum.INTEGRATION, "【部署】deployYaml 解析失败，回退 podName: {}", e.getMessage());
        }
        return null;
    }

    /**
     * 输出截断（避免日志/异常过长）。
     *
     * @param output 原始输出
     * @return 截断后的输出
     */
    private String shortOutput(String output) {
        if (StrUtil.isBlank(output)) {
            return "";
        }
        return output.length() > 500 ? output.substring(0, 500) + "..." : output;
    }
}
