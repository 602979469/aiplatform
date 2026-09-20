package com.jakt.aiplatform.common.integration.elastic;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationErrorCode;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationException;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.nio.charset.StandardCharsets;
import java.time.Duration;
import java.util.ArrayList;
import java.util.Base64;
import java.util.List;

/**
 * Elasticsearch 管理查询实现：走 ES REST（HEAD /{index}、GET /_cat/indices），不引额外 SDK。
 */
@Component
public class EsAdminClientImpl implements EsAdminClient {

    /** ES 地址（与检索客户端共用 es.url 配置）。 */
    private final String url;

    /** 认证头（未配置账号时为 null）。 */
    private final String authorization;

    /** JDK HttpClient。 */
    private final HttpClient httpClient;

    public EsAdminClientImpl(Environment environment) {
        this.url = StrUtil.removeSuffix(environment.getProperty("es.url", ""), "/");
        String username = environment.getProperty("es.username", "");
        String password = environment.getProperty("es.password", "");
        this.authorization = StrUtil.isBlank(username) ? null
                : Base64.getEncoder().encodeToString((username + ":" + StrUtil.nullToEmpty(password))
                        .getBytes(StandardCharsets.UTF_8));
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    @Override
    public boolean indexExists(String index) {
        int status = send("HEAD", "/" + index, Duration.ofSeconds(10)).statusCode();
        return status == 200;
    }

    @Override
    public List<String> listIndices() {
        HttpResponse<String> response = send("GET", "/_cat/indices?h=index&format=json", Duration.ofSeconds(15));
        List<String> indices = new ArrayList<>();
        if (response.statusCode() / 100 != 2) {
            LoggerUtil.error(LogFileEnum.INTEGRATION, "【ES】查询索引列表失败 status={} body={}",
                    response.statusCode(), StrUtil.maxLength(response.body(), 200));
            throw AiIntegrationException.ofThrow(AiIntegrationErrorCode.UNKNOWN,
                    "Elasticsearch 索引列表查询失败（HTTP " + response.statusCode() + "）");
        }
        JSONArray array = JSONUtil.parseArray(response.body());
        for (Object item : array) {
            String name = ((JSONObject) item).getStr("index");
            if (StrUtil.isNotBlank(name) && !StrUtil.startWith(name, ".")) {
                indices.add(name);
            }
        }
        indices.sort(String::compareTo);
        return indices;
    }

    /**
     * 发送 ES 请求。
     *
     * @param method  HTTP 方法
     * @param path    接口路径
     * @param timeout 超时时间
     * @return HTTP 响应
     */
    private HttpResponse<String> send(String method, String path, Duration timeout) {
        String requestUrl = url + path;
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(requestUrl))
                    .timeout(timeout);
            if (StrUtil.isNotBlank(authorization)) {
                builder.header("Authorization", "Basic " + authorization);
            }
            HttpRequest request = "HEAD".equals(method)
                    ? builder.method("HEAD", HttpRequest.BodyPublishers.noBody()).build()
                    : builder.GET().build();
            return httpClient.send(request, HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
        } catch (Exception e) {
            LoggerUtil.error(LogFileEnum.INTEGRATION, e, "【ES】请求异常 url={}", requestUrl);
            throw AiIntegrationException.ofThrow(AiIntegrationErrorCode.UNKNOWN,
                    "Elasticsearch 请求异常: " + e.getMessage(), e);
        }
    }
}
