package com.jakt.aiplatform.core.service.impl;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;

import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.result.Result;
import com.jakt.aiplatform.common.framework.template.BizTemplate;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.integration.k8s.K8sClient;
import com.jakt.aiplatform.common.integration.k8s.K8sDeploymentInfo;
import com.jakt.aiplatform.common.integration.k8s.K8sEventInfo;
import com.jakt.aiplatform.common.integration.k8s.K8sNodeInfo;
import com.jakt.aiplatform.common.integration.k8s.K8sNodeMetric;
import com.jakt.aiplatform.core.model.domain.ClusterDashboard;
import com.jakt.aiplatform.core.model.domain.ClusterNodeInfo;
import com.jakt.aiplatform.core.model.domain.ClusterRuntimeEvent;
import com.jakt.aiplatform.core.model.domain.ClusterRuntimePod;
import com.jakt.aiplatform.core.service.ClusterK8sService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 集群 K8s 领域服务实现：所有远程调用包在 BizTemplate 内，统一异常处理。
 */
@Service
public class ClusterK8sServiceImpl implements ClusterK8sService {

    /** 系统管理业务 pod 标签键。 */
    private static final String MANAGED_LABEL_KEY = "aiplatform-managed";

    /** 系统管理业务 pod 标签值。 */
    private static final String MANAGED_LABEL_VALUE = "true";

    /** K8s 远程客户端。 */
    private final K8sClient k8sClient;

    public ClusterK8sServiceImpl(K8sClient k8sClient) {
        this.k8sClient = k8sClient;
    }

    @Override
    public ClusterDashboard getDashboard() {
        Result<ClusterDashboard> result = BizTemplate.execute(() -> {
            ClusterDashboard dashboard = new ClusterDashboard();
            List<K8sNodeInfo> nodeInfos = k8sClient.listNodes();
            List<K8sNodeMetric> nodeMetrics = k8sClient.listNodeMetrics();

            List<ClusterNodeInfo> nodes = new ArrayList<>();
            long cpuTotal = 0L;
            long cpuUsed = 0L;
            long memoryTotal = 0L;
            long memoryUsed = 0L;
            for (K8sNodeInfo nodeInfo : nodeInfos) {
                ClusterNodeInfo node = new ClusterNodeInfo();
                node.setNodeName(nodeInfo.getNodeName());
                node.setRole(nodeInfo.getRole());
                node.setArch("arm64".equals(nodeInfo.getArch()) ? "ARM" : "AMD");
                node.setStatus(nodeInfo.getStatus());
                node.setPodCountByNamespace(new HashMap<>());
                nodes.add(node);

                K8sNodeMetric metric = nodeMetrics.stream()
                        .filter(m -> m.getNodeName().equals(nodeInfo.getNodeName()))
                        .findFirst()
                        .orElse(null);
                if (metric != null) {
                    cpuTotal += nullToZero(metric.getCpuTotalMilli());
                    cpuUsed += nullToZero(metric.getCpuUsedMilli());
                    memoryTotal += nullToZero(metric.getMemoryTotalBytes());
                    memoryUsed += nullToZero(metric.getMemoryUsedBytes());
                }
            }
            dashboard.setNodes(nodes);
            dashboard.setCpuTotalMilli(cpuTotal);
            dashboard.setCpuUsedMilli(cpuUsed);
            dashboard.setMemoryTotalBytes(memoryTotal);
            dashboard.setMemoryUsedBytes(memoryUsed);
            return dashboard;
        });
        return checkResult(result, "查询集群大盘失败");
    }

    @Override
    public List<ClusterRuntimePod> listRuntimePods(List<String> namespaces) {
        Result<List<ClusterRuntimePod>> result = BizTemplate.execute(() -> {
            List<ClusterRuntimePod> pods = new ArrayList<>();
            for (String namespace : namespaces) {
                List<K8sDeploymentInfo> deployments =
                        k8sClient.listDeploymentsByLabel(namespace, MANAGED_LABEL_KEY, MANAGED_LABEL_VALUE);
                for (K8sDeploymentInfo deployment : deployments) {
                    pods.add(toRuntimePod(deployment));
                }
            }
            return pods;
        });
        return checkResult(result, "查询实时业务 pod 列表失败");
    }

    @Override
    public void stop(String namespace, String name) {
        Result<Void> result = BizTemplate.executeWithoutResult(
                () -> k8sClient.scaleDeployment(namespace, name, 0));
        checkResult(result, "停用业务 pod 失败");
    }

    @Override
    public void start(String namespace, String name, int replicas) {
        Result<Void> result = BizTemplate.executeWithoutResult(
                () -> k8sClient.scaleDeployment(namespace, name, replicas));
        checkResult(result, "启用业务 pod 失败");
    }

    @Override
    public String getPodLogs(String namespace, String deploymentName) {
        Result<String> result = BizTemplate.execute(() -> {
            K8sDeploymentInfo deployment = k8sClient.getDeployment(namespace, deploymentName);
            if (deployment == null || deployment.getFirstPodName() == null) {
                throw AiPlatformException.ofThrow(BizErrorCodeEnum.RESOURCE_NOT_FOUND,
                        "业务 pod 不存在或尚无运行实例: " + deploymentName);
            }
            return k8sClient.getPodLogs(namespace, deployment.getFirstPodName());
        });
        return checkResult(result, "查询业务 pod 日志失败");
    }

    @Override
    public List<ClusterRuntimeEvent> getPodEvents(String namespace, String deploymentName) {
        Result<List<ClusterRuntimeEvent>> result = BizTemplate.execute(() -> {
            K8sDeploymentInfo deployment = k8sClient.getDeployment(namespace, deploymentName);
            if (deployment == null || deployment.getFirstPodName() == null) {
                throw AiPlatformException.ofThrow(BizErrorCodeEnum.RESOURCE_NOT_FOUND,
                        "业务 pod 不存在或尚无运行实例: " + deploymentName);
            }
            List<K8sEventInfo> eventInfos = k8sClient.listPodEvents(namespace, deployment.getFirstPodName());
            return eventInfos.stream().map(this::toRuntimeEvent).collect(Collectors.toList());
        });
        return checkResult(result, "查询业务 pod 事件失败");
    }

    /**
     * 集成 DTO → 领域模型（实时业务 pod）。
     *
     * @param deployment 集成层 Deployment 信息
     * @return 领域模型
     */
    private ClusterRuntimePod toRuntimePod(K8sDeploymentInfo deployment) {
        ClusterRuntimePod pod = new ClusterRuntimePod();
        pod.setPodName(deployment.getName());
        pod.setNamespace(deployment.getNamespace());
        pod.setReadyReplicas(deployment.getReadyReplicas());
        pod.setDesiredReplicas(deployment.getDesiredReplicas());
        pod.setNodeName(deployment.getNodeName());
        pod.setArch("arm64".equals(deployment.getNodeArch()) ? "ARM" : "AMD");
        pod.setImage(deployment.getImage());
        pod.setLastDeployTime(deployment.getLastDeployTime());

        int ready = nullToZero(deployment.getReadyReplicas());
        int desired = nullToZero(deployment.getDesiredReplicas());
        if (desired == 0) {
            pod.setStatus("已停止");
        } else if (ready >= desired) {
            pod.setStatus("运行中");
        } else {
            pod.setStatus("部署中");
        }
        return pod;
    }

    /**
     * 集成 DTO → 领域模型（事件）。
     *
     * @param eventInfo 集成层事件信息
     * @return 领域模型
     */
    private ClusterRuntimeEvent toRuntimeEvent(K8sEventInfo eventInfo) {
        ClusterRuntimeEvent event = new ClusterRuntimeEvent();
        event.setType(eventInfo.getType());
        event.setReason(eventInfo.getReason());
        event.setMessage(eventInfo.getMessage());
        event.setCount(eventInfo.getCount());
        event.setLastTimestamp(eventInfo.getLastTimestamp());
        return event;
    }

    /**
     * null 安全转 0。
     *
     * @param value 数值
     * @return 数值或 0
     */
    private long nullToZero(Long value) {
        return value == null ? 0L : value;
    }

    /**
     * null 安全转 0。
     *
     * @param value 数值
     * @return 数值或 0
     */
    private int nullToZero(Integer value) {
        return value == null ? 0 : value;
    }

    /**
     * 校验 BizTemplate 执行结果，失败抛业务异常（集成层已记日志）。
     *
     * @param result  执行结果
     * @param message 失败提示
     * @param <T>     返回类型
     * @return 成功时的数据
     */
    private <T> T checkResult(Result<T> result, String message) {
        if (!result.isSuccess()) {
            LoggerUtil.warn(LogFileEnum.INTEGRATION, "{} code={} msg={}",
                    message, result.getErrorCode(), result.getErrorMessage());
            throw AiPlatformException.ofThrow(result.getErrorCode(), result.getErrorMessage());
        }
        return result.getData();
    }
}
