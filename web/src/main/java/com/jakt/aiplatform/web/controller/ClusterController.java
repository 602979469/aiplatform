package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.ClusterPodConfigManager;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.ClusterDashboard;
import com.jakt.aiplatform.core.model.domain.ClusterPodConfig;
import com.jakt.aiplatform.web.assembler.ClusterPodConfigAssembler;
import com.jakt.aiplatform.web.checker.ClusterPodConfigParamChecker;
import com.jakt.aiplatform.web.param.ClusterPodConfigCreateRequest;
import com.jakt.aiplatform.web.param.ClusterPodConfigQueryRequest;
import com.jakt.aiplatform.web.param.ClusterPodConfigUpdateRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.ClusterDashboardResponse;
import com.jakt.aiplatform.web.result.ClusterPodConfigResponse;
import com.jakt.aiplatform.web.result.ClusterRuntimeEventResponse;
import com.jakt.aiplatform.web.result.ClusterRuntimePodResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 集群管理接口：数据大盘 + 配置管理 + 实时管理。
 *
 * <p>Controller 只做收参 → ParamChecker → Manager → Assembler → ApiTemplate，不含业务规则。
 */
@RestController
@RequestMapping("/api/cluster")
@Tag(name = "集群管理")
public class ClusterController {

    /** 集群管理 Manager。 */
    private final ClusterPodConfigManager clusterPodConfigManager;

    public ClusterController(ClusterPodConfigManager clusterPodConfigManager) {
        this.clusterPodConfigManager = clusterPodConfigManager;
    }

    /**
     * 集群大盘数据。
     *
     * @return 大盘数据
     */
    @GetMapping("/dashboard")
    public ApiResult<ClusterDashboardResponse> dashboard() {
        return ApiTemplate.execute(null, new ApiTemplate.Callback<Object, ClusterDashboardResponse>() {

            @Override
            public ClusterDashboardResponse execute(Object param) {
                ClusterDashboard dashboard = clusterPodConfigManager.getDashboard();
                return ClusterPodConfigAssembler.toDashboardResponse(dashboard);
            }
        });
    }

    /**
     * 业务命名空间列表。
     *
     * @return 命名空间列表
     */
    @GetMapping("/namespaces")
    public ApiResult<List<String>> namespaces() {
        return ApiTemplate.execute(null, new ApiTemplate.Callback<Object, List<String>>() {

            @Override
            public List<String> execute(Object param) {
                return clusterPodConfigManager.listNamespaces();
            }
        });
    }

    /**
     * 业务pod配置分页查询。
     *
     * @param request 查询条件
     * @return 分页结果
     */
    @GetMapping("/pod-config/page")
    public ApiResult<PageResult<ClusterPodConfigResponse>> pagePodConfig(ClusterPodConfigQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<ClusterPodConfigQueryRequest, PageResult<ClusterPodConfigResponse>>() {

            @Override
            public void beforeService(ClusterPodConfigQueryRequest param) {
                ClusterPodConfigParamChecker.checkClusterPodConfigQueryRequest(param);
            }

            @Override
            public PageResult<ClusterPodConfigResponse> execute(ClusterPodConfigQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new ClusterPodConfigQueryRequest());
                PageResult<ClusterPodConfig> page = clusterPodConfigManager.pageClusterPodConfigs(ClusterPodConfigAssembler.toQueryParam(param));
                return ConvertUtil.mapPage(page, ClusterPodConfigAssembler::toResponse);
            }
        });
    }

    /**
     * 新增业务pod配置版本。
     *
     * @param request 创建请求
     * @return 创建结果
     */
    @PostMapping("/pod-config")
    public ApiResult<ClusterPodConfigResponse> createPodConfig(@RequestBody ClusterPodConfigCreateRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<ClusterPodConfigCreateRequest, ClusterPodConfigResponse>() {

            @Override
            public void beforeService(ClusterPodConfigCreateRequest param) {
                ClusterPodConfigParamChecker.checkClusterPodConfigCreateRequest(param);
            }

            @Override
            public ClusterPodConfigResponse execute(ClusterPodConfigCreateRequest param) {
                ClusterPodConfig clusterPodConfig = clusterPodConfigManager.createClusterPodConfig(ClusterPodConfigAssembler.toModel(param));
                return ClusterPodConfigAssembler.toResponse(clusterPodConfig);
            }
        });
    }

    /**
     * 编辑业务pod配置版本（全量）。
     *
     * @param id      配置主键
     * @param request 更新请求
     * @return 统一返回体
     */
    @PutMapping("/pod-config/{id}")
    public ApiResult<Void> updatePodConfig(@PathVariable Long id, @RequestBody ClusterPodConfigUpdateRequest request) {
        return ApiTemplate.executeWithoutResult(request, new ApiTemplate.CallbackWithoutResult<ClusterPodConfigUpdateRequest>() {

            @Override
            public void beforeService(ClusterPodConfigUpdateRequest param) {
                ClusterPodConfigParamChecker.checkId(id);
                ClusterPodConfigParamChecker.checkClusterPodConfigUpdateRequest(param);
            }

            @Override
            public void execute(ClusterPodConfigUpdateRequest param) {
                clusterPodConfigManager.updateClusterPodConfig(ClusterPodConfigAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除业务pod配置（含 K8s 资源）。
     *
     * @param id 配置主键
     * @return 统一返回体
     */
    @DeleteMapping("/pod-config/{id}")
    public ApiResult<Void> deletePodConfig(@PathVariable Long id) {
        return ApiTemplate.executeWithoutResult(id, new ApiTemplate.CallbackWithoutResult<Long>() {

            @Override
            public void beforeService(Long param) {
                ClusterPodConfigParamChecker.checkId(param);
            }

            @Override
            public void execute(Long param) {
                clusterPodConfigManager.deleteClusterPodConfig(param);
            }
        });
    }

    /**
     * 触发部署（异步受理）。
     *
     * @param id 配置主键
     * @return 统一返回体
     */
    @PostMapping("/pod-config/{id}/deploy")
    public ApiResult<Void> deployPodConfig(@PathVariable Long id) {
        return ApiTemplate.executeWithoutResult(id, new ApiTemplate.CallbackWithoutResult<Long>() {

            @Override
            public void beforeService(Long param) {
                ClusterPodConfigParamChecker.checkId(param);
            }

            @Override
            public void execute(Long param) {
                clusterPodConfigManager.deploy(param);
            }
        });
    }

    /**
     * 停用：对应 Deployment 缩容到 0。
     *
     * @param id 配置主键
     * @return 统一返回体
     */
    @PostMapping("/pod-config/{id}/stop")
    public ApiResult<Void> stopPodConfig(@PathVariable Long id) {
        return ApiTemplate.executeWithoutResult(id, new ApiTemplate.CallbackWithoutResult<Long>() {

            @Override
            public void beforeService(Long param) {
                ClusterPodConfigParamChecker.checkId(param);
            }

            @Override
            public void execute(Long param) {
                clusterPodConfigManager.stop(param);
            }
        });
    }

    /**
     * 启用：对应 Deployment 扩容到配置副本数。
     *
     * @param id 配置主键
     * @return 统一返回体
     */
    @PostMapping("/pod-config/{id}/start")
    public ApiResult<Void> startPodConfig(@PathVariable Long id) {
        return ApiTemplate.executeWithoutResult(id, new ApiTemplate.CallbackWithoutResult<Long>() {

            @Override
            public void beforeService(Long param) {
                ClusterPodConfigParamChecker.checkId(param);
            }

            @Override
            public void execute(Long param) {
                clusterPodConfigManager.start(param);
            }
        });
    }

    /**
     * 实时管理列表。
     *
     * @return 实时业务 pod 列表
     */
    @GetMapping("/runtime/list")
    public ApiResult<List<ClusterRuntimePodResponse>> listRuntime() {
        return ApiTemplate.execute(null, new ApiTemplate.Callback<Object, List<ClusterRuntimePodResponse>>() {

            @Override
            public List<ClusterRuntimePodResponse> execute(Object param) {
                return ConvertUtil.map(clusterPodConfigManager.listRuntimePods(), ClusterPodConfigAssembler::toRuntimePodResponse);
            }
        });
    }

    /**
     * 运行 Pod 日志。
     *
     * @param podName pod 名称
     * @return 日志文本
     */
    @GetMapping("/runtime/{podName}/logs")
    public ApiResult<String> podLogs(@PathVariable String podName) {
        return ApiTemplate.execute(podName, new ApiTemplate.Callback<String, String>() {

            @Override
            public void beforeService(String param) {
                ClusterPodConfigParamChecker.checkPodName(param);
            }

            @Override
            public String execute(String param) {
                return clusterPodConfigManager.getPodLogs(param);
            }
        });
    }

    /**
     * 运行事件。
     *
     * @param podName pod 名称
     * @return K8s 事件列表
     */
    @GetMapping("/runtime/{podName}/events")
    public ApiResult<List<ClusterRuntimeEventResponse>> podEvents(@PathVariable String podName) {
        return ApiTemplate.execute(podName, new ApiTemplate.Callback<String, List<ClusterRuntimeEventResponse>>() {

            @Override
            public void beforeService(String param) {
                ClusterPodConfigParamChecker.checkPodName(param);
            }

            @Override
            public List<ClusterRuntimeEventResponse> execute(String param) {
                return ConvertUtil.map(clusterPodConfigManager.getPodEvents(param), ClusterPodConfigAssembler::toRuntimeEventResponse);
            }
        });
    }
}
