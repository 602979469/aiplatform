package com.jakt.aiplatform.common.integration.ssh;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * SSH 连接配置（master 主机）。
 */
@Data
@ConfigurationProperties(prefix = "ai.ssh")
public class SshProperties {

    /** SSH 用户。 */
    private String username = "ubuntu";

    /** 私钥路径（绝对路径，如 /root/.ssh/id_rsa）。 */
    private String privateKeyPath = "";

    /** 私钥口令（无则留空）。 */
    private String passphrase = "";

    /** 默认超时（秒）。 */
    private long timeoutSeconds = 600;
}
