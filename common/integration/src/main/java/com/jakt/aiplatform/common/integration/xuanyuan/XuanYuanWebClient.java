package com.jakt.aiplatform.common.integration.xuanyuan;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONArray;
import com.alibaba.fastjson2.JSONObject;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationErrorCode;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationException;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Component;
import org.springframework.web.client.HttpStatusCodeException;
import org.springframework.web.client.RestTemplate;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.List;

/**
 * 轩辕加速器官网（xuanyuan.cloud）网页接口客户端。
 *
 * <p>搜索仓库 / 查询仓库 tags 均走官网前端接口；失败抛 {@link AiIntegrationException}。
 */
@Component
public class XuanYuanWebClient {

    /** 浏览器 UA，避免被官网 WAF 拦截（403 ACCESS DENIED）。 */
    private static final String BROWSER_UA = "Mozilla/5.0 (Macintosh; Intel Mac OS X 10_15_7) AppleWebKit/537.36 "
            + "(KHTML, like Gecko) Chrome/126.0.0.0 Safari/537.36";

    private final RestTemplate deepSeekRestTemplate;

    private final XuanYuanProperties properties;

    public XuanYuanWebClient(RestTemplate deepSeekRestTemplate, XuanYuanProperties properties) {
        this.deepSeekRestTemplate = deepSeekRestTemplate;
        this.properties = properties;
    }

    /**
     * 网页搜索仓库（docker.io），返回原始结果列表。
     *
     * @param query 搜索词
     * @param limit 数量上限
     * @return 原始结果列表
     */
    public List<JSONObject> searchRepos(String query, int limit) {
        String url = properties.getWebUrl() + "/api/docker/searchv4?q="
                + URLEncoder.encode(query, StandardCharsets.UTF_8) + "&page=1&size=" + Math.max(limit, 100);
        String body = getWithRetry(url);
        try {
            JSONObject json = JSON.parseObject(body);
            JSONArray results = json == null ? null : json.getJSONArray("results");
            if (results == null) {
                throw new AiIntegrationException(AiIntegrationErrorCode.XUANYUAN_API_ERROR,
                        "轩辕加速器官网搜索响应无结果，请稍后重试");
            }
            List<JSONObject> repos = new ArrayList<>();
            for (int i = 0; i < results.size(); i++) {
                repos.add(results.getJSONObject(i));
            }
            return repos;
        } catch (AiIntegrationException e) {
            throw e;
        } catch (Exception e) {
            AiPlatformLoggerUtil.warn(LogFileEnum.COMMON_ERROR, "【镜像加速器】【XUANYUAN-WEB】网页搜索失败, q={}, 错误={}",
                    query, e.getMessage());
            throw new AiIntegrationException(AiIntegrationErrorCode.XUANYUAN_API_ERROR,
                    "轩辕加速器官网搜索失败，请稍后重试", e);
        }
    }

    /**
     * 查询仓库 tags（tag 为版本筛选），返回原始 JSON。
     *
     * @param namespace 命名空间
     * @param name      仓库名
     * @param tag       版本筛选
     * @return tags 响应 JSON
     */
    public JSONObject fetchTags(String namespace, String name, String tag) {
        String url = properties.getWebUrl() + "/api/docker/tags?namespace="
                + URLEncoder.encode(namespace, StandardCharsets.UTF_8) + "&name="
                + URLEncoder.encode(name, StandardCharsets.UTF_8) + "&tag="
                + URLEncoder.encode(tag, StandardCharsets.UTF_8)
                + "&page=1&limit=100&sort=last_pushed&order=desc";
        String body = getWithRetry(url);
        try {
            return JSON.parseObject(body);
        } catch (Exception e) {
            AiPlatformLoggerUtil.warn(LogFileEnum.COMMON_ERROR, "【镜像加速器】【XUANYUAN-WEB】查询tags失败, repo={}/{}:{}, 错误={}",
                    namespace, name, tag, e.getMessage());
            throw new AiIntegrationException(AiIntegrationErrorCode.XUANYUAN_API_ERROR,
                    "轩辕加速器官网查询标签失败，请稍后重试", e);
        }
    }

    /**
     * 带浏览器 UA 的 GET 请求，403/5xx 时短暂退避重试；认证失败抛 AUTH_ERROR，重试后仍失败抛 XUANYUAN_API_ERROR。
     */
    private String getWithRetry(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.set(HttpHeaders.USER_AGENT, BROWSER_UA);
        headers.set(HttpHeaders.ACCEPT, "application/json");
        HttpEntity<Void> entity = new HttpEntity<>(headers);
        Exception lastError = null;
        for (int attempt = 1; attempt <= 3; attempt++) {
            try {
                return deepSeekRestTemplate.exchange(url, HttpMethod.GET, entity, String.class).getBody();
            } catch (HttpStatusCodeException e) {
                int status = e.getStatusCode().value();
                if (status == 401 || status == 403) {
                    throw new AiIntegrationException(AiIntegrationErrorCode.AUTH_ERROR,
                            "轩辕加速器官网认证失败(HTTP " + status + ")，请检查账号配置", e);
                }
                lastError = e;
                if (attempt < 3) {
                    sleepQuietly(800L * attempt);
                }
            } catch (Exception e) {
                lastError = e;
                if (attempt < 3) {
                    sleepQuietly(800L * attempt);
                }
            }
        }
        throw new AiIntegrationException(AiIntegrationErrorCode.XUANYUAN_API_ERROR,
                "轩辕加速器官网接口请求失败: " + (lastError == null ? "unknown" : lastError.getMessage()), lastError);
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
