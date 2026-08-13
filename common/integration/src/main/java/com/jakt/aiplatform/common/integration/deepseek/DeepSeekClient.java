package com.jakt.aiplatform.common.integration.deepseek;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.jakt.aiplatform.common.integration.deepseek.model.DeepSeekChatMessage;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationErrorCode;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationException;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import cn.hutool.core.util.StrUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

/**
 * DeepSeek 对话接口客户端（非流式，一次返回完整答案）。
 *
 * <p>成功返回回答内容；任何失败抛出 {@link AiIntegrationException}（错误码见 {@link AiIntegrationErrorCode}）。
 */
@Component
public class DeepSeekClient {

    private static final String COMPLETIONS_PATH = "/chat/completions";

    private final RestTemplate restTemplate;

    private final DeepSeekProperties properties;

    public DeepSeekClient(RestTemplate deepSeekRestTemplate, DeepSeekProperties properties) {
        this.restTemplate = deepSeekRestTemplate;
        this.properties = properties;
    }

    /**
     * 调用 DeepSeek chat/completions 接口，返回完整回答。
     *
     * @param messages 对话上下文
     * @return AI 回答内容
     */
    public String chat(List<DeepSeekChatMessage> messages) {
        if (StrUtil.isBlank(properties.getApiKey())) {
            throw new AiIntegrationException(AiIntegrationErrorCode.AUTH_ERROR,
                    "DeepSeek API Key 未配置，请联系管理员");
        }

        String url = properties.getBaseUrl() + COMPLETIONS_PATH;
        JSONObject body = new JSONObject();
        body.put("model", properties.getModel());
        body.put("messages", messages);
        body.put("stream", false);
        if (properties.getTemperature() != null) {
            body.put("temperature", properties.getTemperature());
        }
        if (properties.getMaxTokens() != null) {
            body.put("max_tokens", properties.getMaxTokens());
        }

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.setBearerAuth(properties.getApiKey());

        try {
            long start = System.currentTimeMillis();
            String response = restTemplate.postForObject(
                    url, new HttpEntity<>(body.toJSONString(), headers), String.class);
            long cost = System.currentTimeMillis() - start;
            AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "DeepSeek 调用成功, 模型={}, 耗时={}ms",
                    properties.getModel(), cost);
            return parseContent(response);
        } catch (RestClientException e) {
            AiPlatformLoggerUtil.error(LogFileEnum.COMMON_ERROR, "DeepSeek 接口调用失败: {}", e.getMessage());
            throw new AiIntegrationException(AiIntegrationErrorCode.DEEPSEEK_API_ERROR,
                    "AI 服务暂时不可用，请稍后重试", e);
        }
    }

    /**
     * 解析 DeepSeek 响应，取 choices[0].message.content。
     *
     * @param response 响应原文
     * @return 回答内容
     */
    private String parseContent(String response) {
        if (StrUtil.isBlank(response)) {
            throw new AiIntegrationException(AiIntegrationErrorCode.DEEPSEEK_API_ERROR, "AI 服务返回为空");
        }
        JSONObject result = JSON.parseObject(response);
        JSONArray choices = result == null ? null : result.getJSONArray("choices");
        if (choices == null || choices.isEmpty()) {
            AiPlatformLoggerUtil.error(LogFileEnum.COMMON_ERROR, "DeepSeek 响应异常: {}", response);
            throw new AiIntegrationException(AiIntegrationErrorCode.DEEPSEEK_API_ERROR, "AI 服务返回异常，请稍后重试");
        }
        return choices.getJSONObject(0).getJSONObject("message").getString("content");
    }
}
