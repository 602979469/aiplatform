package com.jakt.aiplatform.common.dal.es;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.Base64;

/**
 * Elasticsearch 检索客户端（只读查询，题库搜索用）。
 *
 * <p>走 ES REST API（_search），避免引入额外 SDK 依赖。
 */
@Component
public class EsSearchClient {

    /** ES 配置。 */
    private final EsProperties properties;

    /** JDK HttpClient。 */
    private final HttpClient httpClient;

    public EsSearchClient(EsProperties properties) {
        this.properties = properties;
        this.httpClient = HttpClient.newBuilder()
                .connectTimeout(Duration.ofSeconds(5))
                .build();
    }

    /**
     * 执行检索。
     *
     * @param index 索引名
     * @param body  查询 DSL
     * @return ES 原始响应
     */
    public JSONObject search(String index, JSONObject body) {
        String url = StrUtil.removeSuffix(properties.getUrl(), "/") + "/" + index + "/_search";
        HttpRequest.Builder builder = HttpRequest.newBuilder()
                .uri(URI.create(url))
                .timeout(Duration.ofSeconds(20))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(JSONUtil.toJsonStr(body), StandardCharsets.UTF_8));
        if (StrUtil.isNotBlank(properties.getUsername())) {
            String auth = properties.getUsername() + ":" + StrUtil.nullToEmpty(properties.getPassword());
            builder.header("Authorization", "Basic "
                    + Base64.getEncoder().encodeToString(auth.getBytes(StandardCharsets.UTF_8)));
        }
        try {
            HttpResponse<String> response = httpClient.send(builder.build(),
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() >= 300) {
                throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR,
                        "ES 检索失败: HTTP " + response.statusCode() + " " + StrUtil.maxLength(response.body(), 200));
            }
            return JSONUtil.parseObj(response.body());
        } catch (AiPlatformException e) {
            throw e;
        } catch (Exception e) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.SYSTEM_ERROR, "ES 检索异常: " + e.getMessage());
        }
    }
}
