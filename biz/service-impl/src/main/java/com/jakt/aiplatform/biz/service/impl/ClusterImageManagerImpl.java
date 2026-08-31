package com.jakt.aiplatform.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.biz.service.ClusterImageManager;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.integration.ssh.SshClient;
import com.jakt.aiplatform.common.integration.ssh.SshResult;
import com.jakt.aiplatform.core.model.domain.ClusterImage;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.ClusterImageStatusEnum;
import com.jakt.aiplatform.core.model.enums.ClusterImageTypeEnum;
import com.jakt.aiplatform.core.model.param.ClusterImageQueryParam;
import com.jakt.aiplatform.core.service.ClusterCiProperties;
import com.jakt.aiplatform.core.service.ClusterImageService;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;

/**
 * 镜像管理用例编排：CRUD 走镜像领域服务；构建/物理删除通过 SSH 调 cluster-ci 脚本。
 */
@Service
public class ClusterImageManagerImpl implements ClusterImageManager {

    /** 镜像领域服务。 */
    private final ClusterImageService clusterImageService;

    /** SSH 客户端（调 cluster-ci 脚本）。 */
    private final SshClient sshClient;

    /** cluster-ci 配置（master/worker 主机）。 */
    private final ClusterCiProperties ciProperties;

    public ClusterImageManagerImpl(ClusterImageService clusterImageService,
                                   SshClient sshClient,
                                   ClusterCiProperties ciProperties) {
        this.clusterImageService = clusterImageService;
        this.sshClient = sshClient;
        this.ciProperties = ciProperties;
    }

    @Override
    public ClusterImage createClusterImage(ClusterImage image) {
        ClusterImage created = clusterImageService.createClusterImage(image);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建镜像成功 id={} image={}:{}",
                created.getId(), created.getImageName(), created.getVersion());
        return created;
    }

    @Override
    public int updateClusterImage(ClusterImage image) {
        int affected = clusterImageService.updateClusterImage(image);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新镜像成功 id={} 影响行数={}", image.getId(), affected);
        return affected;
    }

    @Override
    public int deleteClusterImage(Long id) {
        ClusterImage image = clusterImageService.getClusterImage(id);
        AssertUtil.throwErrWhenNull(image, BizErrorCodeEnum.RESOURCE_NOT_FOUND, "镜像不存在");
        // 已发布镜像：先物理删除（Harbor artifact + 各节点 ctr + MinIO tar），再删记录
        if (image.getBuildStatus() == ClusterImageStatusEnum.PUBLISHED) {
            String script = ciProperties.getWorkDir() + "/bin/delete_image.sh";
            SshResult check = sshClient.execute(ciProperties.getMasterHost(),
                    "test -f " + script + " && echo yes || echo no", 30);
            if (check.isSuccess() && "yes".equals(check.getOutput().trim())) {
                SshResult del = sshClient.execute(ciProperties.getMasterHost(),
                        "bash " + script + " '" + image.getImageName() + "' '" + image.getVersion() + "'",
                        600);
                LoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                        "镜像物理删除脚本执行 id={} success={} output={}",
                        id, del.isSuccess(), shortOutput(del.getOutput()));
            } else {
                LoggerUtil.warn(LogFileEnum.BIZ_SERVICE,
                        "delete_image.sh 未就位，跳过 Harbor/节点/MinIO 清理（TODO） id={}", id);
            }
        }
        int affected = clusterImageService.deleteClusterImage(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除镜像 id={} 影响行数={}", id, affected);
        return affected;
    }

    @Async("asyncThreadPool")
    @Override
    public void buildClusterImage(Long id) {
        clusterImageService.buildClusterImage(id);
        doBuild(id);
    }

    /**
     * 实际构建：拉 git → 上传 Dockerfile → build_image.sh → 回写 PUBLISHED；失败自动重试≤3。
     */
    private void doBuild(Long id) {
        try {
            ClusterImage image = clusterImageService.getClusterImage(id);
            if (image == null) {
                return;
            }
            if (image.getImageType() == ClusterImageTypeEnum.EXTERNAL) {
                LoggerUtil.warn(LogFileEnum.BIZ_SERVICE,
                        "外部镜像导入脚本未接入(TODO)，按构建失败处理 id={}", id);
                failWithRetry(id);
                return;
            }
            String master = ciProperties.getMasterHost();
            String workDir = ciProperties.getWorkDir() + "/images/" + id;
            String srcDir = workDir + "/src";
            String dockerfilePath = workDir + "/Dockerfile";

            // 1. 拉 git 源码（浅克隆单分支）
            SshResult clone = sshClient.execute(master,
                    "rm -rf " + srcDir + " && mkdir -p " + srcDir
                            + " && git clone --depth 1 --branch '" + image.getGitBranch() + "' '"
                            + image.getGitUrl() + "' " + srcDir,
                    600);
            if (!clone.isSuccess()) {
                failWithRetry(id);
                return;
            }

            // 2. 上传用户 Dockerfile（覆盖仓库自带）
            Path local = Files.createTempFile("dockerfile-", ".tmp");
            Files.writeString(local, image.getDockerfile(), StandardCharsets.UTF_8);
            sshClient.uploadFile(master, local.toString(), dockerfilePath);
            Files.deleteIfExists(local);

            // 3. 调 build_image.sh（buildx 多架构 → Harbor → MinIO tar）
            SshResult build = sshClient.execute(master,
                    "bash " + ciProperties.getWorkDir() + "/bin/build_image.sh '"
                            + image.getImageName() + "' '" + image.getVersion() + "' "
                            + srcDir + " " + dockerfilePath,
                    1800);
            if (!build.isSuccess()) {
                failWithRetry(id);
                return;
            }

            // 4. 回写 PUBLISHED + harborRef + tarName（脚本最后一行输出 harbor_ref）
            String harborRef = lastLine(build.getOutput());
            if (StrUtil.isBlank(harborRef)) {
                failWithRetry(id);
                return;
            }
            clusterImageService.saveBuildResult(id, harborRef,
                    image.getImageName() + "_" + image.getVersion() + ".tar.gz");
            LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "镜像构建成功 id={} harbor={}", id, harborRef);
        } catch (Exception e) {
            LoggerUtil.error(LogFileEnum.COMMON_ERROR, e, "镜像构建失败 id={}", id);
            failWithRetry(id);
        }
    }

    /** 构建失败：重试≤3 次（markBuildResult 内部计数，重试中保持 BUILDING）。 */
    private void failWithRetry(Long id) {
        clusterImageService.markBuildResult(id, false);
        ClusterImage after = clusterImageService.getClusterImage(id);
        if (after != null && after.getBuildStatus() == ClusterImageStatusEnum.BUILDING) {
            doBuild(id);
        }
    }

    @Override
    public ClusterImage getClusterImage(Long id) {
        return clusterImageService.getClusterImage(id);
    }

    @Override
    public PageResult<ClusterImage> pageClusterImages(ClusterImageQueryParam query) {
        return clusterImageService.findPage(query);
    }

    @Override
    public java.util.List<ClusterImage> listPublishedImages() {
        return clusterImageService.listPublished();
    }

    private String lastLine(String output) {
        if (StrUtil.isBlank(output)) {
            return "";
        }
        String[] lines = output.trim().split("\\n");
        return lines[lines.length - 1].trim();
    }

    private String shortOutput(String output) {
        if (output == null || output.length() <= 300) {
            return output;
        }
        return output.substring(output.length() - 300);
    }
}
