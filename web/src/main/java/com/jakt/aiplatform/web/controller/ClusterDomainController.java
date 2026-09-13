package com.jakt.aiplatform.web.controller;

import com.jakt.aiplatform.biz.service.ClusterDomainManager;
import com.jakt.aiplatform.biz.service.ClusterDomainView;
import com.jakt.aiplatform.web.checker.ClusterDomainParamChecker;
import com.jakt.aiplatform.web.param.ClusterDomainDisableRequest;
import com.jakt.aiplatform.web.param.ClusterDomainEnableRequest;
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

    /** 开启公网映射（新增 Caddy 站点块；已存在则保持不变）。 */
    @PostMapping("/enable")
    public ApiResult<Void> enable(@RequestBody ClusterDomainEnableRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<ClusterDomainEnableRequest, Void>() {

            @Override
            public void beforeService(ClusterDomainEnableRequest param) {
                ClusterDomainParamChecker.checkEnableRequest(param);
            }

            @Override
            public Void execute(ClusterDomainEnableRequest param) {
                clusterDomainManager.enable(param.getDomain(), param.getUpstream());
                return null;
            }
        });
    }

    /** 关闭公网映射（删除 Caddy 站点块，不影响集群 Ingress）。 */
    @PostMapping("/disable")
    public ApiResult<Void> disable(@RequestBody ClusterDomainDisableRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<ClusterDomainDisableRequest, Void>() {

            @Override
            public void beforeService(ClusterDomainDisableRequest param) {
                ClusterDomainParamChecker.checkDisableRequest(param);
            }

            @Override
            public Void execute(ClusterDomainDisableRequest param) {
                clusterDomainManager.disable(param.getDomain());
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
        response.setPrimary(view.getPrimary());
        response.setType(view.getType());
        response.setUpstream(view.getUpstream());
        response.setNamespace(view.getNamespace());
        response.setService(view.getService());
        response.setPort(view.getPort());
        return response;
    }
}
