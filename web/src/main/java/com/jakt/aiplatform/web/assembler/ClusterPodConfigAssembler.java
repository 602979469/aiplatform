package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.constant.PageConstants;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.ClusterDashboard;
import com.jakt.aiplatform.core.model.domain.ClusterNodeInfo;
import com.jakt.aiplatform.core.model.domain.ClusterPodConfig;
import com.jakt.aiplatform.core.model.domain.ClusterRuntimeEvent;
import com.jakt.aiplatform.core.model.domain.ClusterRuntimePod;
import com.jakt.aiplatform.core.model.param.ClusterPodConfigQueryParam;
import com.jakt.aiplatform.web.param.ClusterPodConfigCreateRequest;
import com.jakt.aiplatform.web.param.ClusterPodConfigQueryRequest;
import com.jakt.aiplatform.web.param.ClusterPodConfigUpdateRequest;
import com.jakt.aiplatform.web.result.ClusterDashboardResponse;
import com.jakt.aiplatform.web.result.ClusterNodeResponse;
import com.jakt.aiplatform.web.result.ClusterPodConfigResponse;
import com.jakt.aiplatform.web.result.ClusterRuntimeEventResponse;
import com.jakt.aiplatform.web.result.ClusterRuntimePodResponse;

/**
 * 业务pod配置对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class ClusterPodConfigAssembler {

    private ClusterPodConfigAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建请求 DTO；为空返回 null
     * @return 领域模型
     */
    public static ClusterPodConfig toModel(ClusterPodConfigCreateRequest request) {
        if (request == null) {
            return null;
        }
        ClusterPodConfig clusterPodConfig = new ClusterPodConfig();
        clusterPodConfig.setResourceName(request.getResourceName());
        clusterPodConfig.setPodName(request.getPodName());
        clusterPodConfig.setNamespace(request.getNamespace());
        clusterPodConfig.setDeployYaml(request.getDeployYaml());
        clusterPodConfig.setImageId(request.getImageId());
        clusterPodConfig.setRemark(request.getRemark());
        return clusterPodConfig;
    }

    /**
     * 更新请求 DTO + 路径主键 → 领域模型。
     *
     * @param request 更新请求 DTO；为空返回 null
     * @param id      路径主键
     * @return 领域模型
     */
    public static ClusterPodConfig toModel(ClusterPodConfigUpdateRequest request, Long id) {
        if (request == null) {
            return null;
        }
        ClusterPodConfig clusterPodConfig = new ClusterPodConfig();
        clusterPodConfig.setId(id);
        clusterPodConfig.setResourceName(request.getResourceName());
        clusterPodConfig.setPodName(request.getPodName());
        clusterPodConfig.setNamespace(request.getNamespace());
        clusterPodConfig.setDeployYaml(request.getDeployYaml());
        clusterPodConfig.setImageId(request.getImageId());
        clusterPodConfig.setRemark(request.getRemark());
        return clusterPodConfig;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 查询请求 DTO；为空返回空查询参数（分页走默认值）
     * @return 查询参数
     */
    public static ClusterPodConfigQueryParam toQueryParam(ClusterPodConfigQueryRequest request) {
        if (request == null) {
            return new ClusterPodConfigQueryParam();
        }
        ClusterPodConfigQueryParam param = new ClusterPodConfigQueryParam();
        param.setResourceName(request.getResourceName());
        param.setPodName(request.getPodName());
        param.setStatus(request.getStatus());
        param.setNamespace(request.getNamespace());
        param.setCreateTimeBegin(request.getCreateTimeBegin());
        param.setCreateTimeEnd(request.getCreateTimeEnd());
        param.setUpdateTimeBegin(request.getUpdateTimeBegin());
        param.setUpdateTimeEnd(request.getUpdateTimeEnd());
        param.setPageNum(ObjectUtil.defaultIfNull(request.getPageNum(), PageConstants.DEFAULT_PAGE_NUM));
        param.setPageSize(ObjectUtil.defaultIfNull(request.getPageSize(), PageConstants.DEFAULT_PAGE_SIZE));
        return param;
    }

    /**
     * 领域模型 → 响应 VO（gitUrl 原样返回，内部工具不脱敏，复制回填需要真实地址）。
     *
     * @param clusterPodConfig 领域模型；为空返回 null
     * @return 响应 VO
     */
    public static ClusterPodConfigResponse toResponse(ClusterPodConfig clusterPodConfig) {
        if (clusterPodConfig == null) {
            return null;
        }
        ClusterPodConfigResponse response = new ClusterPodConfigResponse();
        response.setId(clusterPodConfig.getId());
        response.setResourceName(clusterPodConfig.getResourceName());
        response.setPodName(clusterPodConfig.getPodName());
        response.setStatus(clusterPodConfig.getStatus());
        response.setNamespace(clusterPodConfig.getNamespace());
        response.setDeployYaml(clusterPodConfig.getDeployYaml());
        response.setImageId(clusterPodConfig.getImageId());
        response.setLastBuiltCommit(clusterPodConfig.getLastBuiltCommit());
        response.setCreateBy(clusterPodConfig.getCreateBy());
        response.setUpdateBy(clusterPodConfig.getUpdateBy());
        response.setRemark(clusterPodConfig.getRemark());
        response.setCreateTime(clusterPodConfig.getCreateTime());
        response.setUpdateTime(clusterPodConfig.getUpdateTime());
        return response;
    }

    /**
     * 大盘领域模型 → 响应 VO。
     *
     * @param dashboard 大盘领域模型；为空返回 null
     * @return 响应 VO
     */
    public static ClusterDashboardResponse toDashboardResponse(ClusterDashboard dashboard) {
        if (dashboard == null) {
            return null;
        }
        ClusterDashboardResponse response = new ClusterDashboardResponse();
        response.setNodes(ConvertUtil.map(dashboard.getNodes(), ClusterPodConfigAssembler::toNodeResponse));
        response.setCpuTotalMilli(dashboard.getCpuTotalMilli());
        response.setCpuUsedMilli(dashboard.getCpuUsedMilli());
        response.setMemoryTotalBytes(dashboard.getMemoryTotalBytes());
        response.setMemoryUsedBytes(dashboard.getMemoryUsedBytes());
        response.setPodTotal(dashboard.getPodTotal());
        response.setPodRunning(dashboard.getPodRunning());
        response.setPodStopped(dashboard.getPodStopped());
        response.setPodDeploying(dashboard.getPodDeploying());
        response.setPodFailed(dashboard.getPodFailed());
        return response;
    }

    /**
     * 节点领域模型 → 响应 VO。
     *
     * @param node 节点领域模型；为空返回 null
     * @return 响应 VO
     */
    public static ClusterNodeResponse toNodeResponse(ClusterNodeInfo node) {
        if (node == null) {
            return null;
        }
        return ClusterNodeResponse.builder()
                .nodeName(node.getNodeName())
                .role(node.getRole())
                .arch(node.getArch())
                .status(node.getStatus())
                .podCountByNamespace(node.getPodCountByNamespace())
                .cpuTotalMilli(node.getCpuTotalMilli())
                .cpuUsedMilli(node.getCpuUsedMilli())
                .memoryTotalBytes(node.getMemoryTotalBytes())
                .memoryUsedBytes(node.getMemoryUsedBytes())
                .build();
    }

    /**
     * 实时业务 pod 领域模型 → 响应 VO。
     *
     * @param pod 实时业务 pod 领域模型；为空返回 null
     * @return 响应 VO
     */
    public static ClusterRuntimePodResponse toRuntimePodResponse(ClusterRuntimePod pod) {
        if (pod == null) {
            return null;
        }
        return ClusterRuntimePodResponse.builder()
                .podName(pod.getPodName())
                .podConfigId(pod.getPodConfigId())
                .namespace(pod.getNamespace())
                .status(pod.getStatus())
                .readyReplicas(pod.getReadyReplicas())
                .desiredReplicas(pod.getDesiredReplicas())
                .nodeName(pod.getNodeName())
                .arch(pod.getArch())
                .image(pod.getImage())
                .lastDeployTime(pod.getLastDeployTime())
                .build();
    }

    /**
     * K8s 事件领域模型 → 响应 VO。
     *
     * @param event K8s 事件领域模型；为空返回 null
     * @return 响应 VO
     */
    public static ClusterRuntimeEventResponse toRuntimeEventResponse(ClusterRuntimeEvent event) {
        if (event == null) {
            return null;
        }
        return ClusterRuntimeEventResponse.builder()
                .type(event.getType())
                .reason(event.getReason())
                .message(event.getMessage())
                .count(event.getCount())
                .lastTimestamp(event.getLastTimestamp())
                .build();
    }

}
