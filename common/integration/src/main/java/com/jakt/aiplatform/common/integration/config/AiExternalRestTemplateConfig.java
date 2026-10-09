package com.jakt.aiplatform.common.integration.config;

import com.jakt.aiplatform.common.integration.deepseek.DeepSeekProperties;
import com.jakt.aiplatform.common.integration.dify.DifyProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.web.client.RestTemplate;

import java.time.Duration;

/**
 * 外部接口 RestTemplate 配置：DeepSeek / XuanYuan 共用，超时取 DeepSeekProperties。
 */
@Configuration
public class AiExternalRestTemplateConfig {

    @Bean
    public RestTemplate deepSeekRestTemplate(DeepSeekProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(properties.getConnectTimeout()).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(properties.getReadTimeout()).toMillis());
        return new RestTemplate(factory);
    }

    /**
     * Dify 工作流专用的 RestTemplate：工作流可能多步调用模型，读取超时单独放宽。
     *
     * @param properties Dify 配置
     * @return RestTemplate
     */
    @Bean
    public RestTemplate difyRestTemplate(DifyProperties properties) {
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout((int) Duration.ofSeconds(properties.getConnectTimeout()).toMillis());
        factory.setReadTimeout((int) Duration.ofSeconds(properties.getReadTimeout()).toMillis());
        return new RestTemplate(factory);
    }
}
