package com.jakt.aiplatform.common.integration.ssh;

import cn.hutool.core.io.FileUtil;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationErrorCode;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationException;
import com.jcraft.jsch.Channel;
import com.jcraft.jsch.ChannelExec;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.FileInputStream;
import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.TimeUnit;

/**
 * JSch SSH 客户端实现：远程命令执行 + 文件上传，统一异常封装 + INTEGRATION 日志。
 */
@Component
public class SshClientImpl implements SshClient {

    /** SSH 端口。 */
    private static final int SSH_PORT = 22;

    /** JSch 实例。 */
    private final JSch jsch = new JSch();

    private final SshProperties properties;

    public SshClientImpl(SshProperties properties) {
        this.properties = properties;
        // 私钥只注册一次：JSch identity 会累积，反复 addIdentity 会导致认证混乱（Auth fail）
        if (properties.getPrivateKeyPath() != null && !properties.getPrivateKeyPath().isBlank()) {
            try {
                if (properties.getPassphrase() != null && !properties.getPassphrase().isBlank()) {
                    jsch.addIdentity(properties.getPrivateKeyPath(), properties.getPassphrase());
                } else {
                    jsch.addIdentity(properties.getPrivateKeyPath());
                }
            } catch (JSchException e) {
                LoggerUtil.error(LogFileEnum.INTEGRATION, "【SSH】私钥注册失败 path={}", properties.getPrivateKeyPath());
            }
        }
    }

    @Override
    public SshResult execute(String host, String command) {
        return execute(host, command, properties.getTimeoutSeconds());
    }

    @Override
    public SshResult execute(String host, String command, long timeoutSeconds) {
        Session session = null;
        ChannelExec channel = null;
        SshResult result = new SshResult();
        try {
            session = createSession(host);
            channel = (ChannelExec) session.openChannel("exec");
            channel.setCommand(command);
            channel.setInputStream(null);

            InputStream in = channel.getInputStream();
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            ByteArrayOutputStream errOutput = new ByteArrayOutputStream();
            ChannelExec channelRef = channel;
            // stderr 必须持续读取，否则缓冲区满会阻塞 channel（JSch 经典坑）
            Thread errReader = new Thread(() -> {
                try (InputStream err = channelRef.getErrStream()) {
                    byte[] buf = new byte[8192];
                    int len;
                    while ((len = err.read(buf)) > 0) {
                        errOutput.write(buf, 0, len);
                    }
                } catch (Exception ignored) {
                    // 读取中断忽略
                }
            }, "ssh-stderr-reader");
            errReader.setDaemon(true);
            errReader.start();
            channel.connect(connectTimeoutMillis());

            byte[] buffer = new byte[8192];
            long deadline = System.currentTimeMillis() + TimeUnit.SECONDS.toMillis(timeoutSeconds);
            while (true) {
                while (in.available() > 0) {
                    int len = in.read(buffer);
                    if (len < 0) {
                        break;
                    }
                    output.write(buffer, 0, len);
                }
                if (channel.isClosed()) {
                    if (in.available() > 0) {
                        continue;
                    }
                    break;
                }
                if (System.currentTimeMillis() > deadline) {
                    result.setTimeout(true);
                    String stdout = output.toString(StandardCharsets.UTF_8);
                    String stderr = errOutput.toString(StandardCharsets.UTF_8);
                    result.setOutput(stderr.isBlank() ? stdout : stdout + stderr);
                    LoggerUtil.warn(LogFileEnum.INTEGRATION, "【SSH】命令超时 host={} cmd长度={}",
                            host, command.length());
                    return result;
                }
                TimeUnit.MILLISECONDS.sleep(100);
            }
            result.setExitCode(channel.getExitStatus());
            String stdout = output.toString(StandardCharsets.UTF_8);
            String stderr = errOutput.toString(StandardCharsets.UTF_8);
            result.setOutput(stderr.isBlank() ? stdout : stdout + stderr);
            LoggerUtil.info(LogFileEnum.INTEGRATION, "【SSH】命令完成 host={} exit={} stdoutLen={} stderrLen={}",
                    host, result.getExitCode(), stdout.length(), stderr.length());
            return result;
        } catch (JSchException e) {
            throw toIntegrationException("SSH 执行失败 host={} cmd长度={}", e, host, command.length());
        } catch (Exception e) {
            throw toIntegrationException("SSH 执行异常 host={} cmd长度={}", e, host, command.length());
        } finally {
            if (channel != null) {
                channel.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    @Override
    public void uploadFile(String host, String localPath, String remotePath) {
        Session session = null;
        ChannelSftp sftp = null;
        try {
            session = createSession(host);
            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect(connectTimeoutMillis());
            try (InputStream inputStream = new FileInputStream(localPath)) {
                sftp.put(inputStream, remotePath);
            }
            LoggerUtil.info(LogFileEnum.INTEGRATION, "【SSH】上传文件成功 host={} {} -> {}", host, localPath, remotePath);
        } catch (Exception e) {
            throw toIntegrationException("SSH 上传文件失败 host={} {} -> {}", e, host, localPath, remotePath);
        } finally {
            if (sftp != null) {
                sftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    @Override
    public void downloadFile(String host, String remotePath, String localPath) {
        Session session = null;
        ChannelSftp sftp = null;
        try {
            FileUtil.mkParentDirs(localPath);
            session = createSession(host);
            sftp = (ChannelSftp) session.openChannel("sftp");
            sftp.connect(connectTimeoutMillis());
            sftp.get(remotePath, localPath);
            LoggerUtil.info(LogFileEnum.INTEGRATION, "【SSH】下载文件成功 host={} {} -> {}", host, remotePath, localPath);
        } catch (Exception e) {
            throw toIntegrationException("SSH 下载文件失败 host={} {} -> {}", e, host, remotePath, localPath);
        } finally {
            if (sftp != null) {
                sftp.disconnect();
            }
            if (session != null) {
                session.disconnect();
            }
        }
    }

    /**
     * 创建 SSH 会话。
     *
     * @param host 目标主机（用户@IP）
     * @return 会话
     * @throws JSchException 连接失败
     */
    private Session createSession(String host) throws JSchException {
        String user = host.contains("@") ? host.substring(0, host.indexOf('@')) : properties.getUsername();
        String hostname = host.contains("@") ? host.substring(host.indexOf('@') + 1) : host;
        Session session = jsch.getSession(user, hostname, SSH_PORT);
        session.setConfig("StrictHostKeyChecking", "no");
        session.setConfig("PreferredAuthentications", "publickey,password");
        session.connect(connectTimeoutMillis());
        return session;
    }

    /**
     * 连接超时毫秒。
     *
     * @return 连接超时毫秒
     */
    private int connectTimeoutMillis() {
        return 15000;
    }

    /**
     * 统一异常封装：INTEGRATION 日志 + 集成异常。
     *
     * @param message 日志模板
     * @param e       原始异常
     * @param args    日志参数
     * @return 集成异常
     */
    private AiIntegrationException toIntegrationException(String message, Exception e, Object... args) {
        LoggerUtil.error(LogFileEnum.INTEGRATION, e, "【SSH】" + message, args);
        return new AiIntegrationException(AiIntegrationErrorCode.SSH_ERROR,
                "SSH 远程执行失败: " + e.getMessage(), e);
    }
}
