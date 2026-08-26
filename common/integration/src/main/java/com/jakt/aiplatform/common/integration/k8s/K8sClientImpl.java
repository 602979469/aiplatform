package com.jakt.aiplatform.common.integration.k8s;

import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationErrorCode;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationException;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetricsList;
import io.fabric8.kubernetes.client.KubernetesClient;
import io.fabric8.kubernetes.client.KubernetesClientBuilder;
import io.fabric8.kubernetes.client.KubernetesClientException;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import io.fabric8.kubernetes.client.dsl.PodResource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * fabric8 Kubernetes 客户端实现：基础查询与操作，统一异常封装 + INTEGRATION 日志。
 */
@Component
public class K8sClientImpl implements K8sClient, DisposableBean {

    /** Kubernetes 客户端（in-cluster 或 kubeconfig 自动发现）。 */
    private final KubernetesClient kubernetesClient;

    public K8sClientImpl() {
        this.kubernetesClient = new KubernetesClientBuilder().build();
    }

    @Override
    public void destroy() {
        kubernetesClient.close();
    }

    @Override
    public List<K8sNodeInfo> listNodes() {
        try {
            List<Node> nodes = kubernetesClient.nodes().list().getItems();
            List<K8sNodeInfo> result = new ArrayList<>();
            for (Node node : nodes) {
                result.add(toNodeInfo(node));
            }
            return result;
        } catch (KubernetesClientException e) {
            throw toIntegrationException("查询节点列表失败", e);
        }
    }

    @Override
    public List<K8sNodeMetric> listNodeMetrics() {
        try {
            List<Node> nodes = kubernetesClient.nodes().list().getItems();
            List<K8sNodeMetric> result = new ArrayList<>();
            for (Node node : nodes) {
                K8sNodeMetric metric = new K8sNodeMetric();
                metric.setNodeName(node.getMetadata().getName());
                metric.setCpuTotalMilli(parseCpuMilli(node.getStatus().getCapacity().get("cpu")));
                metric.setMemoryTotalBytes(parseMemoryBytes(node.getStatus().getCapacity().get("memory")));
                result.add(metric);
            }
            // metrics-server 用量：单节点失败不影响整体，失败节点用量置 null
            NodeMetricsList nodeMetricsList = kubernetesClient.top().nodes().metrics();
            if (nodeMetricsList != null && nodeMetricsList.getItems() != null) {
                for (NodeMetrics nodeMetrics : nodeMetricsList.getItems()) {
                    String nodeName = nodeMetrics.getMetadata() == null ? null : nodeMetrics.getMetadata().getName();
                    K8sNodeMetric metric = result.stream()
                            .filter(m -> m.getNodeName().equals(nodeName))
                            .findFirst()
                            .orElse(null);
                    if (metric == null || nodeMetrics.getUsage() == null) {
                        continue;
                    }
                    metric.setCpuUsedMilli(parseCpuMilli(nodeMetrics.getUsage().get("cpu")));
                    metric.setMemoryUsedBytes(parseMemoryBytes(nodeMetrics.getUsage().get("memory")));
                }
            }
            return result;
        } catch (KubernetesClientException e) {
            throw toIntegrationException("查询节点资源用量失败", e);
        }
    }

    @Override
    public K8sDeploymentInfo getDeployment(String namespace, String name) {
        try {
            Deployment deployment = kubernetesClient.apps().deployments().inNamespace(namespace)
                    .withName(name).get();
            if (deployment == null) {
                return null;
            }
            return toDeploymentInfo(deployment);
        } catch (KubernetesClientException e) {
            throw toIntegrationException("查询 Deployment 失败 namespace={} name={}", e, namespace, name);
        }
    }

    @Override
    public List<K8sDeploymentInfo> listDeploymentsByLabel(String namespace, String labelKey, String labelValue) {
        try {
            List<Deployment> deployments = kubernetesClient.apps().deployments().inNamespace(namespace)
                    .withLabel(labelKey, labelValue).list().getItems();
            List<K8sDeploymentInfo> result = new ArrayList<>();
            for (Deployment deployment : deployments) {
                result.add(toDeploymentInfo(deployment));
            }
            return result;
        } catch (KubernetesClientException e) {
            throw toIntegrationException("按标签查询 Deployment 失败 namespace={} label={}={}", e,
                    namespace, labelKey, labelValue);
        }
    }

    @Override
    public void scaleDeployment(String namespace, String name, int replicas) {
        try {
            kubernetesClient.apps().deployments().inNamespace(namespace).withName(name)
                    .scale(replicas);
            LoggerUtil.info(LogFileEnum.INTEGRATION, "【K8S】Deployment {}/{} 副本数调整为 {}",
                    namespace, name, replicas);
        } catch (KubernetesClientException e) {
            throw toIntegrationException("调整 Deployment 副本失败 namespace={} name={} replicas={}", e,
                    namespace, name, replicas);
        }
    }

    @Override
    public void applyYaml(String yaml) {
        try {
            kubernetesClient.load(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8)))
                    .createOrReplace();
            LoggerUtil.info(LogFileEnum.INTEGRATION, "【K8S】apply YAML 成功，长度={}", yaml.length());
        } catch (KubernetesClientException e) {
            throw toIntegrationException("apply YAML 失败", e);
        }
    }

    @Override
    public void deleteByYaml(String yaml) {
        try {
            kubernetesClient.load(new ByteArrayInputStream(yaml.getBytes(StandardCharsets.UTF_8))).delete();
            LoggerUtil.info(LogFileEnum.INTEGRATION, "【K8S】delete YAML 成功，长度={}", yaml.length());
        } catch (KubernetesClientException e) {
            throw toIntegrationException("delete YAML 失败", e);
        }
    }

    @Override
    public String getPodLogs(String namespace, String podName) {
        try {
            PodResource podResource = kubernetesClient.pods().inNamespace(namespace).withName(podName);
            ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
            try (LogWatch logWatch = podResource.watchLog(outputStream)) {
                Thread.sleep(300L);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            return outputStream.toString();
        } catch (KubernetesClientException e) {
            throw toIntegrationException("查询 Pod 日志失败 namespace={} pod={}", e, namespace, podName);
        }
    }

    @Override
    public List<K8sEventInfo> listPodEvents(String namespace, String podName) {
        try {
            List<Event> events = kubernetesClient.v1().events().inNamespace(namespace).list().getItems();
            List<K8sEventInfo> result = new ArrayList<>();
            for (Event event : events) {
                if (event.getInvolvedObject() == null
                        || !Objects.equals(podName, event.getInvolvedObject().getName())) {
                    continue;
                }
                K8sEventInfo info = new K8sEventInfo();
                info.setType(event.getType());
                info.setReason(event.getReason());
                info.setMessage(event.getMessage());
                info.setCount(event.getCount());
                info.setLastTimestamp(parseTimestamp(event.getLastTimestamp()));
                result.add(info);
            }
            return result;
        } catch (KubernetesClientException e) {
            throw toIntegrationException("查询 Pod 事件失败 namespace={} pod={}", e, namespace, podName);
        }
    }

    /**
     * 节点 → 集成 DTO。
     *
     * @param node fabric8 Node
     * @return 集成 DTO
     */
    private K8sNodeInfo toNodeInfo(Node node) {
        K8sNodeInfo info = new K8sNodeInfo();
        info.setNodeName(node.getMetadata().getName());
        Map<String, String> labels = node.getMetadata().getLabels();
        String role = "worker";
        if (labels != null) {
            for (Map.Entry<String, String> entry : labels.entrySet()) {
                if (entry.getKey().startsWith("node-role.kubernetes.io/")) {
                    role = entry.getKey().substring(entry.getKey().lastIndexOf('/') + 1);
                    break;
                }
            }
            info.setArch(labels.getOrDefault("kubernetes.io/arch", ""));
        }
        info.setRole(role);
        info.setStatus(isReady(node.getStatus()) ? "Ready" : "NotReady");
        return info;
    }

    /**
     * 判断节点是否 Ready。
     *
     * @param status 节点状态
     * @return 是否 Ready
     */
    private boolean isReady(NodeStatus status) {
        if (status == null || status.getConditions() == null) {
            return false;
        }
        for (NodeCondition condition : status.getConditions()) {
            if ("Ready".equals(condition.getType())) {
                return "True".equals(condition.getStatus());
            }
        }
        return false;
    }

    /**
     * Deployment → 集成 DTO（补首个 Pod 的节点/架构信息）。
     *
     * @param deployment fabric8 Deployment
     * @return 集成 DTO
     */
    private K8sDeploymentInfo toDeploymentInfo(Deployment deployment) {
        K8sDeploymentInfo info = new K8sDeploymentInfo();
        info.setName(deployment.getMetadata().getName());
        info.setNamespace(deployment.getMetadata().getNamespace());
        info.setDesiredReplicas(deployment.getSpec().getReplicas());
        info.setReadyReplicas(deployment.getStatus().getReadyReplicas());
        List<Container> containers = deployment.getSpec().getTemplate().getSpec().getContainers();
        if (containers != null && !containers.isEmpty()) {
            info.setImage(containers.get(0).getImage());
        }
        info.setLastDeployTime(parseTimestamp(deployment.getMetadata().getCreationTimestamp()));

        List<Pod> pods = kubernetesClient.pods().inNamespace(info.getNamespace())
                .withLabel("app", info.getName()).list().getItems();
        if (pods != null && !pods.isEmpty()) {
            Pod firstPod = pods.get(0);
            info.setFirstPodName(firstPod.getMetadata().getName());
            info.setNodeName(firstPod.getSpec().getNodeName());
            Map<String, String> nodeLabels = firstPod.getSpec().getNodeName() == null ? null
                    : kubernetesClient.nodes().withName(firstPod.getSpec().getNodeName()).get().getMetadata().getLabels();
            if (nodeLabels != null) {
                info.setNodeArch(nodeLabels.getOrDefault("kubernetes.io/arch", ""));
            }
        }
        return info;
    }

    /**
     * 解析 CPU 毫核（支持 4 / 500m 两种格式）。
     *
     * @param quantity K8s Quantity
     * @return 毫核；解析失败返回 null
     */
    private Long parseCpuMilli(Quantity quantity) {
        if (quantity == null) {
            return null;
        }
        String value = quantity.getAmount();
        if (value.endsWith("m")) {
            return Long.valueOf(value.substring(0, value.length() - 1));
        }
        return Double.valueOf(value).longValue() * 1000L;
    }

    /**
     * 解析内存字节（支持 Ki/Mi/Gi/B 等格式）。
     *
     * @param quantity K8s Quantity
     * @return 字节；解析失败返回 null
     */
    private Long parseMemoryBytes(Quantity quantity) {
        if (quantity == null) {
            return null;
        }
        try {
            return Quantity.getAmountInBytes(quantity).longValue();
        } catch (Exception e) {
            LoggerUtil.warn(LogFileEnum.INTEGRATION, "【K8S】内存用量解析失败 value={}", quantity.getAmount());
            return null;
        }
    }

    /**
     * K8s 时间 → 本地时间。
     *
     * @param time K8s 时间
     * @return 本地时间；为空返回 null
     */
    private LocalDateTime toLocalDateTime(OffsetDateTime time) {
        return time == null ? null : time.toLocalDateTime();
    }

    /**
     * 解析 K8s 时间字符串（RFC3339）。
     *
     * @param value 时间字符串
     * @return 本地时间；解析失败返回 null
     */
    private LocalDateTime parseTimestamp(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value).toLocalDateTime();
        } catch (DateTimeParseException e) {
            LoggerUtil.warn(LogFileEnum.INTEGRATION, "【K8S】时间解析失败 value={}", value);
            return null;
        }
    }

    /**
     * 统一异常封装：INTEGRATION 日志 + 集成异常。
     *
     * @param message 日志模板
     * @param e       原始异常
     * @param args    日志参数
     * @return 集成异常
     */
    private AiIntegrationException toIntegrationException(String message, KubernetesClientException e, Object... args) {
        LoggerUtil.error(LogFileEnum.INTEGRATION, e, "【K8S】" + message, args);
        return new AiIntegrationException(AiIntegrationErrorCode.K8S_API_ERROR,
                "Kubernetes 集群操作失败: " + e.getMessage(), e);
    }
}
