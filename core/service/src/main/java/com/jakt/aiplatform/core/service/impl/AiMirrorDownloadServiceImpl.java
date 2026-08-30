package com.jakt.aiplatform.core.service.impl;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;

import cn.hutool.core.util.StrUtil;
import cn.hutool.core.io.FileUtil;
import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.integration.xuanyuan.XuanYuanProperties;
import com.jakt.aiplatform.common.integration.ssh.SshClient;
import com.jakt.aiplatform.common.integration.ssh.SshResult;
import com.jakt.aiplatform.common.util.enums.ThreadPoolEnum;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.MirrorFileUtil;
import com.jakt.aiplatform.common.util.tools.ThreadPoolUtil;
import com.jakt.aiplatform.core.model.domain.FileInfo;
import com.jakt.aiplatform.core.model.domain.MirrorDownloadTask;
import com.jakt.aiplatform.core.model.enums.FileNamespaceEnum;
import com.jakt.aiplatform.core.model.enums.MirrorDownloadStatusEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.repository.FileInfoRepository;
import com.jakt.aiplatform.core.service.AiMirrorDownloadService;
import com.jakt.aiplatform.core.service.FileInfoService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 镜像下载生成领域服务实现。
 *
 * <p>docker pull + docker save 通过 SSH 在远程 docker 主机执行，产物拉回本地，任务状态内存维护。
 * 日志统一使用 【镜像加速器】【DOCKER】 关键词便于检索。
 */
@Service
public class AiMirrorDownloadServiceImpl implements AiMirrorDownloadService {

    /** 远程 docker 主机上的镜像打包目录。 */
    private static final String REMOTE_DIR = "/tmp/aiplatform-mirror";

    /** 拉取/打包超时（秒）。 */
    private static final long DOCKER_TIMEOUT_SECONDS = 600;

    /** docker pull 最大重试次数（网络抖动/瞬时TLS超时容错）。 */
    private static final int PULL_MAX_RETRY = 3;

    private final Map<String, MirrorDownloadTask> tasks = new ConcurrentHashMap<>();

    private final XuanYuanProperties xuanYuanProperties;

    /** SSH 客户端（远程 docker 主机执行）。 */
    private final SshClient sshClient;

    /** 远程 docker 主机地址。 */
    private final String sshHost;

    /** 文件信息表仓储（docker_image 命名空间存量判断）。 */
    private final FileInfoRepository fileInfoRepository;

    /** 文件领域服务（镜像 tar 存入数据库）。 */
    private final FileInfoService fileInfoService;

    private final ThreadPoolUtil threadPoolUtil;

    public AiMirrorDownloadServiceImpl(XuanYuanProperties xuanYuanProperties,
                                       SshClient sshClient,
                                       @Value("${ai.mirror.ssh-host}") String sshHost,
                                       FileInfoRepository fileInfoRepository,
                                       FileInfoService fileInfoService,
                                       ThreadPoolUtil threadPoolUtil) {
        this.xuanYuanProperties = xuanYuanProperties;
        this.sshClient = sshClient;
        this.sshHost = sshHost;
        this.fileInfoRepository = fileInfoRepository;
        this.fileInfoService = fileInfoService;
        this.threadPoolUtil = threadPoolUtil;
    }

    @Override
    public MirrorDownloadTask generate(String repo, String tag) {
        String fileName = MirrorFileUtil.buildFileName(repo, tag);

        // 库中已有该镜像文件（docker_image 命名空间）：直接返回已完成
        FileInfo existing = fileInfoRepository.findOne(FileNamespaceEnum.DOCKER_IMAGE.getCode(), fileName);
        if (ObjectUtil.isNotNull(existing)) {
            MirrorDownloadTask existed = new MirrorDownloadTask();
            existed.setTaskId("local-" + fileName);
            existed.setRepo(repo);
            existed.setTag(tag);
            existed.setFileName(fileName);
            existed.setFileId(existing.getId());
            existed.setStatus(MirrorDownloadStatusEnum.READY);
            existed.setProgress(100);
            existed.setProgressMsg("库中已存在，可直接下载");
            existed.setCreateTime(LocalDateTime.now());
            existed.setFinishTime(LocalDateTime.now());
            LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【DOCKER】库中已存在文件: {}", fileName);
            return existed;
        }

        MirrorDownloadTask task = new MirrorDownloadTask();
        task.setTaskId(UUID.randomUUID().toString().replace("-", ""));
        task.setRepo(repo);
        task.setTag(tag);
        task.setFileName(fileName);
        task.setStatus(MirrorDownloadStatusEnum.GENERATING);
        task.setProgress(5);
        task.setProgressMsg("任务已创建");
        task.setCreateTime(LocalDateTime.now());
        tasks.put(task.getTaskId(), task);

        threadPoolUtil.execute(ThreadPoolEnum.MIRROR_DOWNLOAD, () -> doGenerate(task, repo, tag));
        return task;
    }

    @Override
    public MirrorDownloadTask getStatus(String taskId) {
        MirrorDownloadTask task = tasks.get(taskId);
        AssertUtil.throwErrWhenNull(task, BizErrorCodeEnum.MIRROR_TASK_NOT_FOUND,
                "下载任务不存在或已过期，请重新生成");
        return task;
    }

    /**
     * 异步执行：远程 docker pull + docker save，产物存入库中（docker_image 命名空间）。
     */
    private void doGenerate(MirrorDownloadTask task, String repo, String tag) {
        String registryHost = resolveRegistryHost();
        String remoteImage = registryHost + "/" + repo + ":" + tag;
        String remoteTar = REMOTE_DIR + "/" + task.getFileName();
        Path tempFile = null;
        try {
            // 1. 准备远程目录
            update(task, MirrorDownloadStatusEnum.GENERATING, 5, "准备远程环境");
            SshResult mkdir = sshClient.execute(sshHost, "mkdir -p " + REMOTE_DIR, 60);
            if (!mkdir.isSuccess()) {
                fail(task, "创建远程目录失败", mkdir);
                return;
            }

            // 2. 远程 docker pull（网络抖动/瞬时TLS超时容错，重试 3 次）
            update(task, MirrorDownloadStatusEnum.GENERATING, 10, "开始远程拉取镜像 " + remoteImage);
            SshResult pull = null;
            for (int attempt = 1; attempt <= PULL_MAX_RETRY; attempt++) {
                update(task, MirrorDownloadStatusEnum.GENERATING, 10 + attempt * 5,
                        "远程拉取镜像中（第 " + attempt + "/" + PULL_MAX_RETRY + " 次）");
                pull = sshClient.execute(sshHost, "docker pull " + remoteImage, DOCKER_TIMEOUT_SECONDS);
                if (pull.isSuccess()) {
                    break;
                }
                if (attempt < PULL_MAX_RETRY) {
                    LoggerUtil.warn(LogFileEnum.BIZ_SERVICE,
                            "【镜像加速器】【DOCKER】远程 docker pull 第{}次失败，准备重试: repo={}, tag={}, 退出码={}, 超时={}",
                            attempt, repo, tag, pull.getExitCode(), pull.isTimeout());
                    sleepQuietly(3000L * attempt);
                }
            }
            if (!pull.isSuccess()) {
                fail(task, "远程 docker pull 失败（已重试 " + PULL_MAX_RETRY + " 次）", pull);
                return;
            }

            // 3. 远程打包 tar
            update(task, MirrorDownloadStatusEnum.GENERATING, 60, "拉取完成，远程打包 tar");
            SshResult save = sshClient.execute(sshHost,
                    "docker save -o " + remoteTar + " " + remoteImage, DOCKER_TIMEOUT_SECONDS);
            if (!save.isSuccess()) {
                fail(task, "远程 docker save 失败", save);
                return;
            }

            // 4. 下载到临时文件
            update(task, MirrorDownloadStatusEnum.GENERATING, 80, "下载文件中");
            tempFile = Files.createTempFile("aiplatform-mirror-", ".tar");
            sshClient.downloadFile(sshHost, remoteTar, tempFile.toString());

            // 5. 清理远程临时 tar（失败不影响主流程）
            SshResult clean = sshClient.execute(sshHost, "rm -f " + remoteTar, 60);
            if (!clean.isSuccess()) {
                LoggerUtil.warn(LogFileEnum.BIZ_SERVICE,
                        "【镜像加速器】【DOCKER】远程临时文件清理失败: {}", remoteTar);
            }

            // 6. 流式推入文件管理（docker_image 命名空间，MinIO 对象存储），前端走 /api/file/{id}/download 流式下载
            update(task, MirrorDownloadStatusEnum.GENERATING, 90, "存入 MinIO");
            FileInfo fileInfo;
            try (InputStream contentStream = Files.newInputStream(tempFile)) {
                fileInfo = fileInfoService.uploadStream(FileNamespaceEnum.DOCKER_IMAGE.getCode(),
                        contentStream, Files.size(tempFile), task.getFileName(), repo + ":" + tag);
            }
            task.setFileId(fileInfo.getId());

            update(task, MirrorDownloadStatusEnum.READY, 100, "打包完成，可下载");
            task.setFinishTime(LocalDateTime.now());
            LoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                    "【镜像加速器】【DOCKER】下载生成完成并入库: {} fileId={}", remoteImage, fileInfo.getId());
        } catch (Exception e) {
            LoggerUtil.error(LogFileEnum.COMMON_ERROR,
                    "【镜像加速器】【DOCKER】下载生成异常: repo={}, tag={}, 错误={}", repo, tag, e.getMessage());
            update(task, MirrorDownloadStatusEnum.FAILED, 0, "生成失败");
            task.setErrorCode("UNKNOWN");
            task.setErrorMsg(e.getMessage());
            task.setFinishTime(LocalDateTime.now());
        } finally {
            if (ObjectUtil.isNotNull(tempFile)) {
                FileUtil.del(tempFile.toFile());
            }
        }
    }

    /**
     * 标记任务失败（SSH 执行结果）。
     */
    private void fail(MirrorDownloadTask task, String phase, SshResult result) {
        String errorCode = result.isTimeout() ? "TIMEOUT" : "UNKNOWN";
        String errorMsg = result.isTimeout() ? "命令执行超时（" + phase + "）" : extractError(result.getOutput());
        LoggerUtil.error(LogFileEnum.COMMON_ERROR,
                "【镜像加速器】【DOCKER】{}失败: repo={}, tag={}, 退出码={}, 超时={}, 输出={}",
                phase, task.getRepo(), task.getTag(), result.getExitCode(), result.isTimeout(), result.getOutput());
        update(task, MirrorDownloadStatusEnum.FAILED, 0, "生成失败");
        task.setErrorCode(errorCode);
        task.setErrorMsg(errorMsg);
        task.setFinishTime(LocalDateTime.now());
    }

    /**
     * 从 docker 输出中提取有用错误信息（透传给前端）。
     */
    private String extractError(String output) {
        if (StrUtil.isBlank(output)) {
            return "未知错误，请查看服务端日志";
        }
        String[] lines = output.split("\\n");
        StringBuilder builder = new StringBuilder();
        for (String line : lines) {
            if (StrUtil.isNotBlank(line)) {
                builder.append(line.trim()).append("; ");
            }
        }
        String message = builder.toString().trim();
        return message.length() > 500 ? message.substring(0, 500) : message;
    }

    /**
 * 更新任务进度。
     */
    private void update(MirrorDownloadTask task, MirrorDownloadStatusEnum status, int progress, String progressMsg) {
        task.setStatus(status);
        task.setProgress(progress);
        task.setProgressMsg(progressMsg);
    }

    /**
 * 解析注册表主机。
     */
    private String resolveRegistryHost() {
        String url = xuanYuanProperties.getRegistryUrl();
        if (StrUtil.isBlank(url)) {
            return "docker.xuanyuan.run";
        }
        return url.replaceFirst("^https?://", "").replaceAll("/+$", "");
    }

    /**
 * 静默休眠（忽略中断异常）。
     */
    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
