package com.jakt.aiplatform.common.util.tools;

import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import cn.hutool.core.util.StrUtil;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * 本地命令执行工具：带超时控制，统一捕获输出。镜像下载等外部命令调用统一走本类。
 */
public final class CommandUtil {

    /** 日志检索关键词。 */
    public static final String LOG_TAG = "【CMD】";

    /** 默认超时（秒）。 */
    public static final long DEFAULT_TIMEOUT_SECONDS = 300;

    private CommandUtil() {
    }

    /**
     * 执行命令（默认超时 300 秒）。
     *
     * @param command 命令及参数
     * @return 执行结果
     */
    public static CommandResult execute(String... command) {
        return execute(Arrays.asList(command), DEFAULT_TIMEOUT_SECONDS);
    }

    /**
     * 执行命令。
     *
     * @param command        命令及参数
     * @param timeoutSeconds 超时时间（秒）
     * @return 执行结果
     */
    public static CommandResult execute(List<String> command, long timeoutSeconds) {
        CommandResult result = new CommandResult();
        Process process = null;
        try {
            ProcessBuilder builder = new ProcessBuilder(command);
            builder.redirectErrorStream(true);
            process = builder.start();
            final Process runningProcess = process;

            StringBuilder output = new StringBuilder();
            Thread reader = new Thread(() -> {
                try (BufferedReader bufferedReader = new BufferedReader(
                        new InputStreamReader(runningProcess.getInputStream(), StandardCharsets.UTF_8))) {
                    String line;
                    while ((line = bufferedReader.readLine()) != null) {
                        output.append(line).append('\n');
                    }
                } catch (IOException e) {
                    // 忽略读取中断
                }
            }, "cmd-output-reader");
            reader.setDaemon(true);
            reader.start();

            boolean finished = process.waitFor(timeoutSeconds, TimeUnit.SECONDS);
            if (!finished) {
                result.setTimeout(true);
                process.destroyForcibly();
                process.waitFor(5, TimeUnit.SECONDS);
            }
            reader.join(2000);
            result.setExitCode(process.exitValue());
            result.setOutput(output.toString().trim());
        } catch (Exception e) {
            result.setExitCode(-1);
            result.setOutput("命令执行异常: " + e.getMessage());
            AiPlatformLoggerUtil.error(LogFileEnum.COMMON_ERROR, "{}命令执行异常: {} , 错误: {}",
                    LOG_TAG, String.join(" ", command), e.getMessage());
        } finally {
            if (process != null) {
                process.destroy();
            }
        }

        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "{}执行命令: {} | 退出码: {} | 超时: {}",
                LOG_TAG, String.join(" ", command), result.getExitCode(), result.isTimeout());
        if (StrUtil.isNotBlank(result.getOutput())) {
            AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "{}命令输出:\n{}", LOG_TAG, result.getOutput());
        }
        return result;
    }

    /**
     * 命令执行结果。
     */
    public static class CommandResult {

        /** 退出码（-1 表示异常）。 */
        private int exitCode = -1;

        /** 输出内容。 */
        private String output = "";

        /** 是否超时。 */
        private boolean timeout;

        public boolean isSuccess() {
            return !timeout && exitCode == 0;
        }

        public int getExitCode() {
            return exitCode;
        }

        public void setExitCode(int exitCode) {
            this.exitCode = exitCode;
        }

        public String getOutput() {
            return output;
        }

        public void setOutput(String output) {
            this.output = output;
        }

        public boolean isTimeout() {
            return timeout;
        }

        public void setTimeout(boolean timeout) {
            this.timeout = timeout;
        }
    }
}
