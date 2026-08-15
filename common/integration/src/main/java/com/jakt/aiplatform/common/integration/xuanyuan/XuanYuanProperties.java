package com.jakt.aiplatform.common.integration.xuanyuan;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * 轩辕加速器（docker.xuanyuan.run）配置。
 */
@Data
@ConfigurationProperties(prefix = "ai.xuanyuan")
public class XuanYuanProperties {

    /** 加速器地址。 */
    private String registryUrl = "https://docker.xuanyuan.run";

    /** 加速器官网地址（搜索/标签网页接口）。 */
    private String webUrl = "https://xuanyuan.cloud";

    /** 登录账号。 */
    private String username = "";

    /** 登录密码。 */
    private String password = "";

    /** 接口超时（秒）。 */
    private int apiTimeoutSeconds = 30;
}
