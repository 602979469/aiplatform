package com.jakt.aiplatform.common.integration.canal;

import cn.hutool.core.util.StrUtil;
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

/**
 * canal-adapter 管理接口实现：走 JDK HttpClient 打集群内 Service，不引额外 SDK。
 */
@Component
public class CanalAdapterClientImpl implements CanalAdapterClient {

    /** 默认地址：同集群 cdc 命名空间的 canal-adapter Service。 */
    private static final String DEFAULT_BASE_URL = "http://canal-adapter.cdc.svc.cluster.local:8081";

    /** 普通查询超时。 */
    private static final Duration QUERY_TIMEOUT = Duration.ofSeconds(20);

    /** 全量导入超时：数据量大时耗时较长。 */
    private static final Duration ETL_TIMEOUT = Duration.ofMinutes(30);

    /** canal-adapter 管理接口地址（canal.adapter.url 可覆盖）。 */
    private final String baseUrl;

    /** JDK HttpClient。 */
    private final HttpClient httpClient;

    public CanalAdapterClientImpl(Environment environment) {
        this.baseUrl = StrUtil.removeSuffix(environment.getProperty("canal.adapter.url", DEFAULT_BASE_URL), "/");
        this.httpClient = HttpClient.newBuilder().connectTimeout(Duration.ofSeconds(5)).build();
    }

    @Override
    public String destinations() {
        return send("GET", "/destinations", QUERY_TIMEOUT);
    }

    @Override
    public String triggerEtl(String adapter, String task) {
        return send("POST", "/etl/" + adapter + "/" + task, ETL_TIMEOUT);
    }

    /**
     * 发送请求并返回响应体。
     *
     * @param method  HTTP 方法
     * @param path    接口路径
     * @param timeout 超时时间
     * @return 响应体
     */
    private String send(String method, String path, Duration timeout) {
        String url = baseUrl + path;
        try {
            HttpRequest.Builder builder = HttpRequest.newBuilder()
                    .uri(URI.create(url))
                    .timeout(timeout);
            HttpRequest request = "POST".equals(method)
                    ? builder.POST(HttpRequest.BodyPublishers.noBody()).build()
                    : builder.GET().build();
            HttpResponse<String> response = httpClient.send(request,
                    HttpResponse.BodyHandlers.ofString(StandardCharsets.UTF_8));
            if (response.statusCode() < 200 || response.statusCode() >= 300) {
                LoggerUtil.error(LogFileEnum.INTEGRATION,
                        "【CANAL】调用失败 url={} status={} body={}", url, response.statusCode(), response.body());
                throw new AiIntegrationException(AiIntegrationErrorCode.UNKNOWN,
                        "canal-adapter 调用失败（HTTP " + response.statusCode() + "）");
            }
            LoggerUtil.info(LogFileEnum.INTEGRATION, "【CANAL】调用成功 url={} status={}", url, response.statusCode());
            return response.body();
        } catch (AiIntegrationException e) {
            throw e;
        } catch (Exception e) {
            LoggerUtil.error(LogFileEnum.INTEGRATION, e, "【CANAL】调用异常 url={}", url);
            throw new AiIntegrationException(AiIntegrationErrorCode.UNKNOWN,
                    "canal-adapter 调用异常: " + e.getMessage(), e);
        }
    }
}
