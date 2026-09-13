package com.jakt.aiplatform.web.controller;

import com.jakt.aiplatform.biz.service.ClusterDomainManager;
import com.jakt.aiplatform.biz.service.ClusterDomainView;
import com.jakt.aiplatform.web.checker.ClusterDomainParamChecker;
import com.jakt.aiplatform.web.param.ClusterDomainAddRequest;
import com.jakt.aiplatform.web.param.ClusterDomainRemoveRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.ClusterDomainResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 集群域名映射接口：公网 Caddy 入口 + 集群内 Ingress 联动增删查。
 */
@RestController
@RequestMapping("/api/cluster/domain")
public class ClusterDomainController {

    private final ClusterDomainManager clusterDomainManager;

    public ClusterDomainController(ClusterDomainManager clusterDomainManager) {
        this.clusterDomainManager = clusterDomainManager;
    }

    /** 全部域名映射（Caddy 站点 ∪ Ingress 域名）。 */
    @GetMapping("/list")
    public ApiResult<List<ClusterDomainResponse>> list() {
        return ApiTemplate.execute("domain", new ApiTemplate.Callback<String,
                List<ClusterDomainResponse>>() {

            @Override
            public List<ClusterDomainResponse> execute(String param) {
                return clusterDomainManager.list().stream()
                        .map(ClusterDomainController.this::toResponse).toList();
            }
        });
    }

    /** 新增域名映射（可选同时创建 Ingress）。 */
    @PostMapping("/add")
    public ApiResult<Void> add(@RequestBody ClusterDomainAddRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<ClusterDomainAddRequest, Void>() {

            @Override
            public void beforeService(ClusterDomainAddRequest param) {
                ClusterDomainParamChecker.checkAddRequest(param);
            }

            @Override
            public Void execute(ClusterDomainAddRequest param) {
                clusterDomainManager.add(param.getDomain(), param.getNamespace(),
                        param.getService(), param.getPort());
                return null;
            }
        });
    }

    /** 删除域名映射（同时删除 dm- 前缀的 Ingress）。 */
    @PostMapping("/remove")
    public ApiResult<Void> remove(@RequestBody ClusterDomainRemoveRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<ClusterDomainRemoveRequest, Void>() {

            @Override
            public void beforeService(ClusterDomainRemoveRequest param) {
                ClusterDomainParamChecker.checkRemoveRequest(param);
            }

            @Override
            public Void execute(ClusterDomainRemoveRequest param) {
                clusterDomainManager.remove(param.getDomain());
                return null;
            }
        });
    }

    /**
     * 视图转响应。
     *
     * @param view 域名映射视图
     * @return 响应对象
     */
    private ClusterDomainResponse toResponse(ClusterDomainView view) {
        ClusterDomainResponse response = new ClusterDomainResponse();
        response.setDomain(view.getDomain());
        response.setCaddy(view.getCaddy());
        response.setIngress(view.getIngress());
        response.setIngressName(view.getIngressName());
        response.setNamespace(view.getNamespace());
        response.setService(view.getService());
        response.setPort(view.getPort());
        response.setManaged(view.getManaged());
        return response;
    }
}
