package com.jakt.aiplatform.web.controller;

import com.jakt.aiplatform.biz.service.ClusterSecretManager;
import com.jakt.aiplatform.biz.service.ClusterSecretView;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.web.checker.ClusterSecretParamChecker;
import com.jakt.aiplatform.web.param.ClusterSecretQueryRequest;
import com.jakt.aiplatform.web.param.ClusterSecretUpsertRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.ClusterSecretResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 集群密钥管理接口：实时读集群，值单向写入，响应只含键名。
 */
@RestController
@RequestMapping("/api/cluster/secret")
public class ClusterSecretController {

    private final ClusterSecretManager clusterSecretManager;

    public ClusterSecretController(ClusterSecretManager clusterSecretManager) {
        this.clusterSecretManager = clusterSecretManager;
    }

    /** 分页查询（命名空间 + 关键字）。 */
    @GetMapping("/page")
    public ApiResult<PageResult<ClusterSecretResponse>> page(ClusterSecretQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<ClusterSecretQueryRequest,
                PageResult<ClusterSecretResponse>>() {

            @Override
            public void beforeService(ClusterSecretQueryRequest param) {
                ClusterSecretParamChecker.checkPageRequest(param);
            }

            @Override
            public PageResult<ClusterSecretResponse> execute(ClusterSecretQueryRequest param) {
                int pageNum = param == null || param.getPageNum() == null ? 1 : param.getPageNum();
                int pageSize = param == null || param.getPageSize() == null ? 10 : param.getPageSize();
                String namespace = param == null ? null : param.getNamespace();
                String keyword = param == null ? null : param.getKeyword();
                PageResult<ClusterSecretView> page = clusterSecretManager.page(namespace, keyword, pageNum, pageSize);
                List<ClusterSecretResponse> dataList = page.getDataList().stream()
                        .map(ClusterSecretController.this::toResponse).toList();
                return new PageResult<>(page.getTotal(), pageNum, pageSize, dataList);
            }
        });
    }

    /** 下拉选项：命名空间下全部 aiplatform-managed 密钥（含键名，供配置环境变量勾选）。 */
    @GetMapping("/options")
    public ApiResult<List<ClusterSecretResponse>> options(String namespace) {
        return ApiTemplate.execute(namespace, new ApiTemplate.Callback<String,
                List<ClusterSecretResponse>>() {

            @Override
            public List<ClusterSecretResponse> execute(String ns) {
                return clusterSecretManager.options(ns).stream()
                        .map(ClusterSecretController.this::toResponse).toList();
            }
        });
    }

    /** 密钥详情（键名列表，值不可见）。 */
    @GetMapping("/{namespace}/{name}")
    public ApiResult<ClusterSecretResponse> detail(@PathVariable String namespace,
                                                   @PathVariable String name) {
        return ApiTemplate.execute(namespace, new ApiTemplate.Callback<String,
                ClusterSecretResponse>() {

            @Override
            public void beforeService(String ns) {
                ClusterSecretParamChecker.checkDetail(ns, name);
            }

            @Override
            public ClusterSecretResponse execute(String ns) {
                return toResponse(clusterSecretManager.detail(ns, name));
            }
        });
    }

    /** 新增/覆盖键值并同步集群（不删除；未提交的键原样保留）。 */
    @PostMapping("/upsert")
    public ApiResult<Void> upsert(@RequestBody ClusterSecretUpsertRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<ClusterSecretUpsertRequest, Void>() {

            @Override
            public void beforeService(ClusterSecretUpsertRequest param) {
                ClusterSecretParamChecker.checkUpsertRequest(param);
            }

            @Override
            public Void execute(ClusterSecretUpsertRequest param) {
                Map<String, String> keyValues = new LinkedHashMap<>();
                for (ClusterSecretUpsertRequest.Item item : param.getKeys()) {
                    keyValues.put(item.getKey(), item.getValue());
                }
                clusterSecretManager.upsert(param.getNamespace(), param.getName(), param.getType(), keyValues);
                return null;
            }
        });
    }

    private ClusterSecretResponse toResponse(ClusterSecretView view) {
        ClusterSecretResponse response = new ClusterSecretResponse();
        response.setNamespace(view.getNamespace());
        response.setName(view.getName());
        response.setType(view.getType());
        response.setManaged(view.isManaged());
        response.setKeys(view.getKeys());
        return response;
    }
}
