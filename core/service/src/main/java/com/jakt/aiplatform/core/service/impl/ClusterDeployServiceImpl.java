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
import org.springframework.retry.annotation.Backoff;
import org.springframework.retry.annotation.Retryable;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

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
        String deploymentName = deploymentName(config);
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

        // 3. 拉取源码（API 查 commit + codeload 下载，带 GitHub token 防限流；脚本输出 commit 短哈希）
        //    传入用户上传的 Dockerfile，脚本拉完源码后用其覆盖仓库 Dockerfile（保证构建用系统配置）
        SshResult fetchResult = sshClient.execute(ciProperties.getMasterHost(),
                "bash " + ciProperties.getWorkDir() + "/bin/fetch_source.sh "
                        + config.getGitUrl() + " " + config.getGitBranch() + " " + remoteSrcDir
                        + " " + remoteDockerfile,
                300);
        if (!fetchResult.isSuccess()) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR,
                    "源码拉取失败: " + shortOutput(fetchResult.getOutput()));
        }
        // 4. 镜像 tag = commit 短哈希（脚本最后一行输出，用户不接触镜像版本号）
        String imageTag = fetchResult.getOutput().trim();
        AssertUtil.throwErrWhenBlank(imageTag, ErrorCodeEnum.SYSTEM_ERROR, "镜像 tag 为空");

        // 5. 触发双架构构建导入（image_tools.sh：master + worker 各自构建）
        SshResult buildResult = sshClient.execute(ciProperties.getMasterHost(),
                "bash " + ciProperties.getWorkDir() + "/bin/image_tools.sh "
                        + config.getPodName() + " " + imageTag + " " + remoteSrcDir,
                DEPLOY_TIMEOUT_SECONDS);
        if (!buildResult.isSuccess()) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR,
                    "镜像构建导入失败: " + shortOutput(buildResult.getOutput()));
        }

        // 6. apply + set image + rollout（deploy.sh）
        SshResult applyResult = sshClient.execute(ciProperties.getMasterHost(),
                "bash " + ciProperties.getWorkDir() + "/bin/deploy.sh "
                        + config.getNamespace() + " " + remoteDeployYaml + " "
                        + config.getPodName() + " " + imageTag,
                DEPLOY_TIMEOUT_SECONDS);
        if (!applyResult.isSuccess()) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR,
                    "部署 apply 失败: " + shortOutput(applyResult.getOutput()));
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

    /**
     * Deployment 命名规则：podName-versionNo（小写）。
     *
     * @param config 配置
     * @return Deployment 名称
     */
    private String deploymentName(ClusterPodConfig config) {
        return (config.getPodName() + "-" + config.getVersionNo()).toLowerCase();
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
