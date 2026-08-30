package com.jakt.aiplatform.common.integration.ssh;

/**
 * SSH 远程执行客户端：驱动集群节点上的构建/部署脚本。
 *
 * <p>所有方法失败抛 {@link com.jakt.aiplatform.common.integration.exception.AiIntegrationException}，
 * 集成层内部已按 {@link com.jakt.aiplatform.common.framework.enums.LogFileEnum#INTEGRATION} 记录日志。
 */
public interface SshClient {

    /**
     * 远程执行命令（默认超时）。
     *
     * @param host    目标主机（用户@IP，如 ubuntu@192.168.3.131）
     * @param command 命令
     * @return 执行结果（退出码 + 输出）
     */
    SshResult execute(String host, String command);

    /**
     * 远程执行命令（自定义超时，秒）。
     *
     * @param host          目标主机（用户@IP）
     * @param command       命令
     * @param timeoutSeconds 超时秒数
     * @return 执行结果
     */
    SshResult execute(String host, String command, long timeoutSeconds);

    /**
     * 上传文件到远程主机。
     *
     * @param host       目标主机（用户@IP）
     * @param localPath  本地文件路径
     * @param remotePath 远程目标路径
     */
    void uploadFile(String host, String localPath, String remotePath);

    /**
     * 从远程主机下载文件。
     *
     * @param host       目标主机（用户@IP）
     * @param remotePath 远程文件路径
     * @param localPath  本地目标路径
     */
    void downloadFile(String host, String remotePath, String localPath);
}
