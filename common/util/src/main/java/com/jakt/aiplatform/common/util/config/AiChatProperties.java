package com.jakt.aiplatform.common.util.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;

/**
 * AI 对话业务配置。
 */
@Data
@ConfigurationProperties(prefix = "ai.chat")
public class AiChatProperties {

    /** 模拟模式：none 正常 / fail 模拟失败 / timeout 模拟超时（测试用）。 */
    private String simulation = "none";

    /** 模拟超时时长（秒）。 */
    private int timeoutSeconds = 60;
}
