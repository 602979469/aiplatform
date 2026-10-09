package com.jakt.aiplatform.common.integration.dify;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationErrorCode;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationException;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * Dify 工作流客户端：把 Dify 工作流当"一个函数"调用（blocking 模式，一次拿到输出）。
 *
 * <p>调用的是 Dify Service API：{@code POST {baseUrl}/workflows/run}，
 * 入参 {@code inputs} 是工作流开始节点的变量，出参取 {@code data.outputs}。
 * 任何失败抛 {@link AiIntegrationException}，日志统一打在 integration 日志里（不打印 API Key）。
 */
@Component
public class DifyWorkflowClient {

    /** 工作流运行路径。 */
    private static final String WORKFLOW_RUN_PATH = "/workflows/run";

    /** 同步等待模式：跑完一次性返回结果。 */
    private static final String RESPONSE_MODE_BLOCKING = "blocking";

    /** Dify 工作流成功状态。 */
    private static final String STATUS_SUCCEEDED = "succeeded";

    private final RestTemplate restTemplate;

    private final DifyProperties properties;

    public DifyWorkflowClient(RestTemplate difyRestTemplate, DifyProperties properties) {
        this.restTemplate = difyRestTemplate;
        this.properties = properties;
    }

    /**
     * 运行工作流并返回输出变量。
     *
     * @param apiKey 应用 API Key；为空用配置里的默认 Key
     * @param inputs 工作流输入变量
     * @param user 调用方标识（Dify 用于区分调用者，写死业务标记即可）
     * @return 工作流输出变量；无输出返回空 Map
     */
    public Map<String, Object> runBlocking(String apiKey, Map<String, Object> inputs, String user) {
        String key = StrUtil.blankToDefault(apiKey, properties.getApiKey());
        if (StrUtil.isBlank(key)) {
            throw AiIntegrationException.ofThrow(AiIntegrationErrorCode.AUTH_ERROR,
                    "Dify API Key 未配置（能力 provider_config.apiKey 或 ai.dify.api-key）");
        }
        String url = StrUtil.removeSuffix(properties.getBaseUrl(), "/") + WORKFLOW_RUN_PATH;
        JSONObject body = new JSONObject();
        body.put("inputs", ObjectUtil.defaultIfNull(inputs, new HashMap<>()));
        body.put("response_mode", RESPONSE_MODE_BLOCKING);
        body.put("user", StrUtil.blankToDefault(user, "aiplatform"));

        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        headers.set(HttpHeaders.AUTHORIZATION, "Bearer " + key);

        String response;
        try {
            response = restTemplate.postForObject(url, new HttpEntity<>(body.toJSONString(), headers), String.class);
        } catch (RestClientException e) {
            LoggerUtil.error(LogFileEnum.INTEGRATION, e, "【Dify】工作流调用失败 url={}", url);
            throw AiIntegrationException.ofThrow(AiIntegrationErrorCode.DIFY_API_ERROR,
                    "Dify 工作流调用失败：" + e.getMessage());
        }
        return parseOutputs(url, response);
    }

    /**
     * 解析 Dify 返回：{@code {"data":{"status":"succeeded","outputs":{...}}}}。
     */
    private Map<String, Object> parseOutputs(String url, String response) {
        if (StrUtil.isBlank(response)) {
            LoggerUtil.warn(LogFileEnum.INTEGRATION, "【Dify】工作流返回为空 url={}", url);
            throw AiIntegrationException.ofThrow(AiIntegrationErrorCode.DIFY_API_ERROR, "Dify 工作流返回为空");
        }
        JSONObject json = JSON.parseObject(response);
        if (ObjectUtil.isNull(json)) {
            throw AiIntegrationException.ofThrow(AiIntegrationErrorCode.DIFY_API_ERROR,
                    "Dify 返回无法解析：" + StrUtil.maxLength(response, 200));
        }
        // 认证/参数类错误：Dify 直接返回 code + message
        if (StrUtil.isNotBlank(json.getString("code")) && json.getJSONObject("data") == null) {
            LoggerUtil.warn(LogFileEnum.INTEGRATION, "【Dify】工作流报错 code={} message={}",
                    json.getString("code"), json.getString("message"));
            throw AiIntegrationException.ofThrow(AiIntegrationErrorCode.DIFY_API_ERROR,
                    "Dify 返回错误：" + json.getString("code") + " " + json.getString("message"));
        }
        JSONObject data = json.getJSONObject("data");
        if (ObjectUtil.isNull(data)) {
            throw AiIntegrationException.ofThrow(AiIntegrationErrorCode.DIFY_API_ERROR,
                    "Dify 返回缺少 data：" + StrUtil.maxLength(response, 200));
        }
        String status = data.getString("status");
        if (!StrUtil.equals(status, STATUS_SUCCEEDED)) {
            LoggerUtil.warn(LogFileEnum.INTEGRATION, "【Dify】工作流未成功 status={} error={}",
                    status, data.getString("error"));
            throw AiIntegrationException.ofThrow(AiIntegrationErrorCode.DIFY_API_ERROR,
                    "Dify 工作流未成功（status=" + status + "）：" + StrUtil.maxLength(data.getString("error"), 200));
        }
        JSONObject outputs = data.getJSONObject("outputs");
        if (ObjectUtil.isNull(outputs) || CollUtil.isEmpty(outputs)) {
            return new LinkedHashMap<>();
        }
        return new LinkedHashMap<>(outputs);
    }
}
