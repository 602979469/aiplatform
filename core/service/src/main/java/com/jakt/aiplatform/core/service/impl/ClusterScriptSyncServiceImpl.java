package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.DigestUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.integration.ssh.SshClient;
import com.jakt.aiplatform.common.integration.ssh.SshResult;
import com.jakt.aiplatform.core.service.ClusterCiProperties;
import com.jakt.aiplatform.core.service.ClusterScriptSyncService;
import org.springframework.core.io.Resource;
import org.springframework.core.io.support.PathMatchingResourcePatternResolver;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.Comparator;

/**
 * cluster-ci 脚本同步服务实现：比对远端 bin 脚本与 Java 资源的 SHA-256，
 * 缺失或内容变更（bugfix 等）则上传覆盖，新增脚本自动写入。
 */
@Service
public class ClusterScriptSyncServiceImpl implements ClusterScriptSyncService {

    /** 脚本资源路径（classpath），新增 .sh 放这里会在下次调度时自动同步。 */
    private static final String SCRIPT_RESOURCE_PATTERN = "classpath*:cluster-ci/bin/*.sh";

    /** SSH 客户端。 */
    private final SshClient sshClient;

    /** cluster-ci 配置。 */
    private final ClusterCiProperties ciProperties;

    public ClusterScriptSyncServiceImpl(SshClient sshClient, ClusterCiProperties ciProperties) {
        this.sshClient = sshClient;
        this.ciProperties = ciProperties;
    }

    @Override
    public void syncScripts() {
        try {
            Resource[] resources = new PathMatchingResourcePatternResolver().getResources(SCRIPT_RESOURCE_PATTERN);
            Arrays.sort(resources, Comparator.comparing(Resource::getFilename));
            for (Resource resource : resources) {
                syncOne(resource);
            }
        } catch (IOException e) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR, "读取脚本资源失败: " + e.getMessage());
        }
    }

    /**
     * 同步单个脚本：远端不存在或 hash 不一致则上传。
     *
     * @param resource 脚本资源
     */
    private void syncOne(Resource resource) {
        String name = resource.getFilename();
        if (StrUtil.isBlank(name)) {
            return;
        }
        byte[] content = readContent(resource, name);
        String localHash = DigestUtil.sha256Hex(content);
        String binDir = ciProperties.getWorkDir() + "/bin";
        String remotePath = binDir + "/" + name;

        SshResult check = sshClient.execute(ciProperties.getMasterHost(), "sha256sum " + remotePath, 30);
        if (check.isSuccess() && StrUtil.startWith(check.getOutput().trim(), localHash)) {
            LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "脚本已是最新 name={} remote={}", name, remotePath);
            return;
        }

        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "同步脚本（缺失或内容变更） name={} remote={}", name, remotePath);
        sshClient.execute(ciProperties.getMasterHost(), "mkdir -p " + binDir, 30);
        Path tmp = null;
        try {
            tmp = Files.createTempFile("cluster-script-", ".sh");
            Files.write(tmp, content);
            // 先传临时文件再 mv 原子替换：运行中的脚本进程继续读旧 inode，不受影响
            String tmpRemote = remotePath + ".tmp";
            sshClient.uploadFile(ciProperties.getMasterHost(), tmp.toString(), tmpRemote);
            SshResult mvResult = sshClient.execute(ciProperties.getMasterHost(), "mv -f " + tmpRemote + " " + remotePath, 30);
            if (!mvResult.isSuccess()) {
                throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR,
                        "脚本原子替换失败: " + name + " " + shortOutput(mvResult.getOutput()));
            }
        } catch (IOException e) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR, "脚本同步失败: " + name + " " + e.getMessage());
        } finally {
            if (tmp != null) {
                try {
                    Files.deleteIfExists(tmp);
                } catch (IOException e) {
                    LoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "临时脚本文件清理失败 path={}", tmp);
                }
            }
        }
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "脚本同步完成 name={}", name);
    }

    /**
     * 截断 SSH 输出用于异常信息。
     *
     * @param output SSH 输出
     * @return 截断后的输出
     */
    private String shortOutput(String output) {
        return StrUtil.maxLength(output, 200);
    }

    /**
     * 读取脚本资源内容。
     *
     * @param resource 脚本资源
     * @param name     脚本文件名（日志用）
     * @return 脚本字节内容
     */
    private byte[] readContent(Resource resource, String name) {
        try (InputStream in = resource.getInputStream()) {
            return in.readAllBytes();
        } catch (IOException e) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR, "读取脚本资源失败: " + name);
        }
    }
}
