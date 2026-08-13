package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.integration.xuanyuan.XuanYuanProperties;
import com.jakt.aiplatform.common.util.enums.ThreadPoolEnum;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.CommandUtil;
import com.jakt.aiplatform.common.util.tools.MirrorFileUtil;
import com.jakt.aiplatform.common.util.tools.ThreadPoolUtil;
import com.jakt.aiplatform.core.model.domain.MirrorDownloadTask;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.AiMirrorDownloadService;
import org.springframework.stereotype.Service;

import java.io.File;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.time.LocalDateTime;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

/**
 * 镜像下载生成领域服务实现。
 *
 * <p>docker pull + docker save 为本地命令调用（不用 AI），任务状态内存维护。
 * 日志统一使用 【镜像加速器】【DOCKER】 关键词便于检索。
 */
@Service
public class AiMirrorDownloadServiceImpl implements AiMirrorDownloadService {

    /** 拉取/打包超时（秒）。 */
    private static final long DOCKER_TIMEOUT_SECONDS = 600;

    /** docker pull 最大重试次数（网络抖动/瞬时TLS超时容错）。 */
    private static final int PULL_MAX_RETRY = 3;

    private final Map<String, MirrorDownloadTask> tasks = new ConcurrentHashMap<>();

    private final XuanYuanProperties xuanYuanProperties;

    private final ThreadPoolUtil threadPoolUtil;

    public AiMirrorDownloadServiceImpl(XuanYuanProperties xuanYuanProperties, ThreadPoolUtil threadPoolUtil) {
        this.xuanYuanProperties = xuanYuanProperties;
        this.threadPoolUtil = threadPoolUtil;
    }

    @Override
    public MirrorDownloadTask generate(String repo, String tag) {
        ensureImageDir();
        String fileName = MirrorFileUtil.buildFileName(repo, tag);

        // 本地已有文件：直接返回已完成
        if (MirrorFileUtil.isFileExists(fileName)) {
            MirrorDownloadTask existed = new MirrorDownloadTask();
            existed.setTaskId("local-" + fileName);
            existed.setRepo(repo);
            existed.setTag(tag);
            existed.setFileName(fileName);
            existed.setStatus("ready");
            existed.setProgress(100);
            existed.setProgressMsg("本地已存在，可直接下载");
            existed.setCreateTime(LocalDateTime.now());
            existed.setFinishTime(LocalDateTime.now());
            AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "【镜像加速器】【DOCKER】本地已存在文件: {}", fileName);
            return existed;
        }

        MirrorDownloadTask task = new MirrorDownloadTask();
        task.setTaskId(UUID.randomUUID().toString().replace("-", ""));
        task.setRepo(repo);
        task.setTag(tag);
        task.setFileName(fileName);
        task.setStatus("generating");
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
        AiPlatformInvoker.throwErrWhenNull(task, ErrorCodeEnum.BIZ_ERROR,
                "下载任务不存在或已过期，请重新生成");
        return task;
    }

    @Override
    public File getFile(String fileName) {
        AiPlatformInvoker.throwErrWhenFalse(MirrorFileUtil.isValidFileName(fileName), ErrorCodeEnum.BIZ_ERROR,
                "非法的文件名");
        Path path = Paths.get(MirrorFileUtil.IMAGE_DIR).resolve(fileName).normalize();
        File file = path.toFile();
        AiPlatformInvoker.throwErrWhenTrue(!file.exists() || !file.isFile(), ErrorCodeEnum.BIZ_ERROR,
                "本地文件不存在，请重新生成下载链接");
        return file;
    }

    /**
     * 异步执行 docker pull + docker save。
     */
    private void doGenerate(MirrorDownloadTask task, String repo, String tag) {
        String registryHost = resolveRegistryHost();
        String remoteImage = registryHost + "/" + repo + ":" + tag;
        Path targetFile = Paths.get(MirrorFileUtil.IMAGE_DIR, task.getFileName());
        try {
            update(task, "generating", 10, "开始拉取镜像 " + remoteImage);
            CommandUtil.CommandResult pull = null;
            for (int attempt = 1; attempt <= PULL_MAX_RETRY; attempt++) {
                update(task, "generating", 10 + attempt * 5, "拉取镜像中（第 " + attempt + "/" + PULL_MAX_RETRY + " 次）");
                pull = CommandUtil.execute(Arrays.asList("docker", "pull", remoteImage), DOCKER_TIMEOUT_SECONDS);
                if (pull.isSuccess()) {
                    break;
                }
                if (attempt < PULL_MAX_RETRY) {
                    AiPlatformLoggerUtil.warn(LogFileEnum.BIZ_SERVICE,
                            "【镜像加速器】【DOCKER】docker pull 第{}次失败，准备重试: repo={}, tag={}, 退出码={}, 超时={}",
                            attempt, repo, tag, pull.getExitCode(), pull.isTimeout());
                    sleepQuietly(3000L * attempt);
                }
            }
            if (pull == null || !pull.isSuccess()) {
                fail(task, "docker pull 失败（已重试 " + PULL_MAX_RETRY + " 次）", pull);
                return;
            }

            update(task, "generating", 60, "拉取完成，开始打包 tar");
            CommandUtil.CommandResult save = CommandUtil.execute(
                    Arrays.asList("docker", "save", "-o", targetFile.toString(), remoteImage), DOCKER_TIMEOUT_SECONDS);
            if (!save.isSuccess()) {
                fail(task, "docker save 失败", save);
                return;
            }

            update(task, "ready", 100, "打包完成，可下载");
            task.setFinishTime(LocalDateTime.now());
            AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                    "【镜像加速器】【DOCKER】下载生成完成: {} -> {}", remoteImage, targetFile);
        } catch (Exception e) {
            AiPlatformLoggerUtil.error(LogFileEnum.COMMON_ERROR,
                    "【镜像加速器】【DOCKER】下载生成异常: repo={}, tag={}, 错误={}", repo, tag, e.getMessage());
            update(task, "failed", 0, "生成失败");
            task.setErrorCode("UNKNOWN");
            task.setErrorMsg(e.getMessage());
            task.setFinishTime(LocalDateTime.now());
        }
    }

    private void fail(MirrorDownloadTask task, String phase, CommandUtil.CommandResult result) {
        String errorCode = result.isTimeout() ? "TIMEOUT" : "UNKNOWN";
        String errorMsg = result.isTimeout() ? "命令执行超时（" + phase + "）" : extractError(result.getOutput());
        AiPlatformLoggerUtil.error(LogFileEnum.COMMON_ERROR,
                "【镜像加速器】【DOCKER】{}失败: repo={}, tag={}, 退出码={}, 超时={}, 输出={}",
                phase, task.getRepo(), task.getTag(), result.getExitCode(), result.isTimeout(), result.getOutput());
        update(task, "failed", 0, "生成失败");
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

    private void update(MirrorDownloadTask task, String status, int progress, String progressMsg) {
        task.setStatus(status);
        task.setProgress(progress);
        task.setProgressMsg(progressMsg);
    }

    private void ensureImageDir() {
        try {
            MirrorFileUtil.ensureImageDir();
        } catch (IllegalStateException e) {
            throw new com.jakt.aiplatform.core.model.exception.AiPlatformException(
                    ErrorCodeEnum.BIZ_ERROR, "创建镜像存储目录失败: " + e.getMessage(), e);
        }
    }

    private String resolveRegistryHost() {
        String url = xuanYuanProperties.getRegistryUrl();
        if (StrUtil.isBlank(url)) {
            return "docker.xuanyuan.run";
        }
        return url.replaceFirst("^https?://", "").replaceAll("/+$", "");
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
