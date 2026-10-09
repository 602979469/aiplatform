package com.jakt.aiplatform.common.integration.dify;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * Dify 配置：Service API 地址与默认 API Key（每个应用可单独配 Key，见能力表 provider_config）。
 */
@Data
@ConfigurationProperties(prefix = "ai.dify")
public class DifyProperties {

    /** Service API 地址（注意带 /v1）。 */
    private String baseUrl = "https://dify.jakt.online/v1";

    /** 默认 API Key（app-xxx）；能力可以用自己的 provider_config.apiKey 覆盖。 */
    private String apiKey;

    /** 连接超时（秒）。 */
    private Integer connectTimeout = 10;

    /** 读取超时（秒）：工作流可能多步调用模型，给宽一点。 */
    private Integer readTimeout = 180;
}
