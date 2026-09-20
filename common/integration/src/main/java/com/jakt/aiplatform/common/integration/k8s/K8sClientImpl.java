package com.jakt.aiplatform.common.integration.k8s;

import cn.hutool.core.collection.CollUtil;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.util.StrUtil;
import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationErrorCode;
import com.jakt.aiplatform.common.integration.exception.AiIntegrationException;
import io.fabric8.kubernetes.api.model.Container;
import io.fabric8.kubernetes.api.model.ConfigMap;
import io.fabric8.kubernetes.api.model.ConfigMapBuilder;
import io.fabric8.kubernetes.api.model.Event;
import io.fabric8.kubernetes.api.model.ObjectMeta;
import io.fabric8.kubernetes.api.model.Node;
import io.fabric8.kubernetes.api.model.NodeCondition;
import io.fabric8.kubernetes.api.model.NodeStatus;
import io.fabric8.kubernetes.api.model.Pod;
import io.fabric8.kubernetes.api.model.Quantity;
import io.fabric8.kubernetes.api.model.apps.Deployment;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetrics;
import io.fabric8.kubernetes.api.model.metrics.v1beta1.NodeMetricsList;
import io.fabric8.kubernetes.client.*;
import io.fabric8.kubernetes.client.dsl.LogWatch;
import io.fabric8.kubernetes.client.dsl.PodResource;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.core.env.Environment;
import org.springframework.core.env.Profiles;
import org.springframework.stereotype.Component;

import java.io.ByteArrayOutputStream;
import java.io.ByteArrayInputStream;
import java.nio.charset.StandardCharsets;
import java.time.OffsetDateTime;
import java.time.format.DateTimeParseException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

/**
 * fabric8 Kubernetes 客户端实现：基础查询与操作，统一异常封装 + INTEGRATION 日志。
 */
@Component
public class K8sClientImpl implements K8sClient, DisposableBean {

    /** Spring 环境（profile 判断：dev 用配置 token，其余 in-cluster 自动发现）。 */
    private final Environment environment;

    /** Kubernetes 客户端（dev：配置 token 连 API Server；非 dev：in-cluster 自动发现）。 */
    private final KubernetesClient kubernetesClient;

    public K8sClientImpl(Environment environment) {
        this.environment = environment;
        this.kubernetesClient = buildClient();
    }

    /**
     * 构建 Kubernetes 客户端：dev profile 且配置了 master-url/token 时直连外部 API Server，
     * 否则使用 in-cluster/kubeconfig 自动发现（生产 pod 内默认）。
     *
     * @return Kubernetes 客户端
     */
    private KubernetesClient buildClient() {
        String masterUrl = environment.getProperty("k8s.master-url", "");
        String token = environment.getProperty("k8s.token", "");
        if (environment.acceptsProfiles(Profiles.of("dev"))
                && StrUtil.isNotBlank(masterUrl) && StrUtil.isNotBlank(token)) {
            Config config = new ConfigBuilder()
                    .withMasterUrl(masterUrl)
                    .withOauthToken(token)
                    .withTrustCerts(true)
                    .build();
            LoggerUtil.info(LogFileEnum.INTEGRATION, "【K8S】dev profile：使用配置 token 连接 masterUrl={}", masterUrl);
            return new KubernetesClientBuilder().withConfig(config).build();
        }
        LoggerUtil.info(LogFileEnum.INTEGRATION, "【K8S】非 dev 或未配置 token：使用 in-cluster/kubeconfig 自动发现");
        return new KubernetesClientBuilder().build();
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
            Map<String, NodeAllocation> allocations = collectNodeAllocations();
            List<K8sNodeMetric> result = new ArrayList<>();
            for (Node node : nodes) {
                NodeStatus status = node.getStatus();
                K8sNodeMetric metric = new K8sNodeMetric();
                metric.setNodeName(node.getMetadata().getName());
                metric.setCpuTotalMilli(parseCpuMilli(nodeQuantity(status, false, "cpu")));
                metric.setMemoryTotalBytes(parseMemoryBytes(nodeQuantity(status, false, "memory")));
                metric.setCpuAllocatableMilli(parseCpuMilli(nodeQuantity(status, true, "cpu")));
                metric.setMemoryAllocatableBytes(parseMemoryBytes(nodeQuantity(status, true, "memory")));
                metric.setPodAllocatable(parseCount(nodeQuantity(status, true, "pods")));

                NodeAllocation allocation = allocations.get(metric.getNodeName());
                metric.setPodCount(ObjectUtil.isNull(allocation) ? 0 : allocation.podCount());
                metric.setCpuRequestMilli(ObjectUtil.isNull(allocation) ? 0L : allocation.cpuRequestMilli());
                metric.setMemoryRequestBytes(ObjectUtil.isNull(allocation) ? 0L : allocation.memoryRequestBytes());
                fillDiskUsage(metric);
                result.add(metric);
            }
            // metrics-server 用量：metrics API 不可用（未安装 metrics-server）时降级，仅返回节点容量，不抛异常
            try {
                NodeMetricsList nodeMetricsList = kubernetesClient.top().nodes().metrics();
                if (ObjectUtil.isNotNull(nodeMetricsList) && ObjectUtil.isNotNull(nodeMetricsList.getItems())) {
                    for (NodeMetrics nodeMetrics : nodeMetricsList.getItems()) {
                        String nodeName = ObjectUtil.isNull(nodeMetrics.getMetadata()) ? null : nodeMetrics.getMetadata().getName();
                        K8sNodeMetric metric = result.stream()
                                .filter(m -> m.getNodeName().equals(nodeName))
                                .findFirst()
                                .orElse(null);
                        if (ObjectUtil.isNull(metric) || ObjectUtil.isNull(nodeMetrics.getUsage())) {
                            continue;
                        }
                        metric.setCpuUsedMilli(parseCpuMilli(nodeMetrics.getUsage().get("cpu")));
                        metric.setMemoryUsedBytes(parseMemoryBytes(nodeMetrics.getUsage().get("memory")));
                    }
                }
            } catch (KubernetesClientException e) {
                LoggerUtil.warn(LogFileEnum.INTEGRATION, "【K8S】metrics-server 不可用，资源用量置空: {}", e.getMessage());
            }
            return result;
        } catch (KubernetesClientException e) {
            throw toIntegrationException("查询节点资源用量失败", e);
        }
    }

    /**
     * 汇总每个节点上 pod 的数量与 requests 分配量（已终态 pod 不计入）。
     *
     * @return 节点名称 → 分配量
     */
    private Map<String, NodeAllocation> collectNodeAllocations() {
        Map<String, NodeAllocation> allocations = new HashMap<>();
        try {
            List<Pod> pods = kubernetesClient.pods().inAnyNamespace().list().getItems();
            if (ObjectUtil.isNull(pods)) {
                return allocations;
            }
            for (Pod pod : pods) {
                if (ObjectUtil.isNull(pod.getSpec()) || ObjectUtil.isNull(pod.getSpec().getNodeName()) || isTerminal(pod)) {
                    continue;
                }
                PodRequests requests = resolvePodRequests(pod);
                allocations.merge(pod.getSpec().getNodeName(),
                        new NodeAllocation(1, requests.cpuMilli(), requests.memoryBytes()),
                        NodeAllocation::plus);
            }
        } catch (KubernetesClientException e) {
            LoggerUtil.warn(LogFileEnum.INTEGRATION, "【K8S】统计节点 pod 分配量失败，分配量置空: {}", e.getMessage());
        }
        return allocations;
    }

    /**
     * 计算单个 pod 的资源 requests：取「容器之和」与「init 容器最大值」的较大者，再加 pod overhead。
     *
     * @param pod pod
     * @return CPU（毫核）与内存（字节）requests
     */
    private PodRequests resolvePodRequests(Pod pod) {
        long cpu = 0L;
        long memory = 0L;
        List<Container> containers = pod.getSpec().getContainers();
        if (ObjectUtil.isNotNull(containers)) {
            for (Container container : containers) {
                cpu += nullToZero(requestCpuMilli(container));
                memory += nullToZero(requestMemoryBytes(container));
            }
        }
        long initCpu = 0L;
        long initMemory = 0L;
        List<Container> initContainers = pod.getSpec().getInitContainers();
        if (ObjectUtil.isNotNull(initContainers)) {
            for (Container container : initContainers) {
                initCpu = Math.max(initCpu, nullToZero(requestCpuMilli(container)));
                initMemory = Math.max(initMemory, nullToZero(requestMemoryBytes(container)));
            }
        }
        cpu = Math.max(cpu, initCpu);
        memory = Math.max(memory, initMemory);
        Map<String, Quantity> overhead = pod.getSpec().getOverhead();
        if (ObjectUtil.isNotNull(overhead)) {
            cpu += nullToZero(parseCpuMilli(overhead.get("cpu")));
            memory += nullToZero(parseMemoryBytes(overhead.get("memory")));
        }
        return new PodRequests(cpu, memory);
    }

    /**
     * 容器 CPU requests（毫核）。
     *
     * @param container 容器
     * @return 毫核；未设置返回 null
     */
    private Long requestCpuMilli(Container container) {
        return ObjectUtil.isNull(container.getResources()) || ObjectUtil.isNull(container.getResources().getRequests())
                ? null : parseCpuMilli(container.getResources().getRequests().get("cpu"));
    }

    /**
     * 容器内存 requests（字节）。
     *
     * @param container 容器
     * @return 字节；未设置返回 null
     */
    private Long requestMemoryBytes(Container container) {
        return ObjectUtil.isNull(container.getResources()) || ObjectUtil.isNull(container.getResources().getRequests())
                ? null : parseMemoryBytes(container.getResources().getRequests().get("memory"));
    }

    /**
     * 节点磁盘用量：走 API Server 代理 kubelet summary 接口（不依赖 metrics-server）。
     *
     * @param metric 待填充的节点指标
     */
    private void fillDiskUsage(K8sNodeMetric metric) {
        try {
            String summary = kubernetesClient.raw("/api/v1/nodes/" + metric.getNodeName() + "/proxy/stats/summary");
            if (StrUtil.isBlank(summary)) {
                return;
            }
            JSONObject node = JSON.parseObject(summary).getJSONObject("node");
            if (ObjectUtil.isNull(node)) {
                return;
            }
            JSONObject fs = node.getJSONObject("fs");
            if (ObjectUtil.isNull(fs)) {
                JSONObject runtime = node.getJSONObject("runtime");
            fs = ObjectUtil.isNull(runtime) ? null : runtime.getJSONObject("imageFs");
            }
            if (ObjectUtil.isNull(fs)) {
                return;
            }
            metric.setDiskTotalBytes(fs.getLong("capacityBytes"));
            metric.setDiskUsedBytes(fs.getLong("usedBytes"));
        } catch (Exception e) {
            LoggerUtil.warn(LogFileEnum.INTEGRATION, "【K8S】节点磁盘用量查询失败 node={}: {}",
                    metric.getNodeName(), e.getMessage());
        }
    }

    /**
     * 节点 capacity/allocatable 中的资源量。
     *
     * @param status     节点状态
     * @param allocatable true 取 allocatable，false 取 capacity
     * @param key        资源键（cpu/memory/pods）
     * @return Quantity；不存在返回 null
     */
    private Quantity nodeQuantity(NodeStatus status, boolean allocatable, String key) {
        if (ObjectUtil.isNull(status)) {
            return null;
        }
        Map<String, Quantity> quantities = allocatable ? status.getAllocatable() : status.getCapacity();
        return ObjectUtil.isNull(quantities) ? null : quantities.get(key);
    }

    /**
     * pod 是否已终态（Succeeded/Failed 不再占用调度资源）。
     *
     * @param pod pod
     * @return 是否终态
     */
    private boolean isTerminal(Pod pod) {
        String phase = ObjectUtil.isNull(pod.getStatus()) ? null : pod.getStatus().getPhase();
        return "Succeeded".equals(phase) || "Failed".equals(phase);
    }

    /**
     * 解析整型数量（如 pods 上限）。
     *
     * @param quantity K8s Quantity
     * @return 数量；解析失败返回 null
     */
    private Integer parseCount(Quantity quantity) {
        if (ObjectUtil.isNull(quantity) || ObjectUtil.isNull(quantity.getAmount())) {
            return null;
        }
        try {
            return Integer.valueOf(quantity.getAmount().trim());
        } catch (NumberFormatException e) {
            LoggerUtil.warn(LogFileEnum.INTEGRATION, "【K8S】数量解析失败 value={}", quantity.getAmount());
            return null;
        }
    }

    /**
     * null 安全转 0。
     *
     * @param value 值
     * @return 非 null 值
     */
    private long nullToZero(Long value) {
        return ObjectUtil.defaultIfNull(value, 0L);
    }

    /**
     * 节点分配量（pod 数 + requests 之和）。
     */
    private record NodeAllocation(int podCount, long cpuRequestMilli, long memoryRequestBytes) {

        /**
         * 累加另一个分配量。
         *
         * @param other 另一个分配量
         * @return 累加结果
         */
        private NodeAllocation plus(NodeAllocation other) {
            return new NodeAllocation(podCount + other.podCount,
                    cpuRequestMilli + other.cpuRequestMilli,
                    memoryRequestBytes + other.memoryRequestBytes);
        }
    }

    /**
     * 单个 pod 的 requests。
     */
    private record PodRequests(long cpuMilli, long memoryBytes) {
    }

    @Override
    public List<String> listNamespaces() {
        try {
            List<io.fabric8.kubernetes.api.model.Namespace> namespaces =
                    kubernetesClient.namespaces().list().getItems();
            List<String> result = new ArrayList<>();
            if (ObjectUtil.isNull(namespaces)) {
                return result;
            }
            for (io.fabric8.kubernetes.api.model.Namespace namespace : namespaces) {
                if (ObjectUtil.isNotNull(namespace.getMetadata()) && ObjectUtil.isNotNull(namespace.getMetadata().getName())) {
                    result.add(namespace.getMetadata().getName());
                }
            }
            return result;
        } catch (KubernetesClientException e) {
            throw toIntegrationException("查询命名空间列表失败", e);
        }
    }

    @Override
    public List<K8sPodInfo> listPodsBySelector(String namespace, Map<String, String> selectorLabels) {
        try {
            List<Pod> pods = kubernetesClient.pods().inNamespace(namespace)
                    .withLabels(selectorLabels).list().getItems();
            List<K8sPodInfo> result = new ArrayList<>();
            if (ObjectUtil.isNull(pods)) {
                return result;
            }
            for (Pod pod : pods) {
                K8sPodInfo info = new K8sPodInfo();
                info.setNamespace(namespace);
                info.setPodName(ObjectUtil.isNull(pod.getMetadata()) ? null : pod.getMetadata().getName());
                info.setNodeName(ObjectUtil.isNull(pod.getSpec()) ? null : pod.getSpec().getNodeName());
                result.add(info);
            }
            return result;
        } catch (KubernetesClientException e) {
            throw toIntegrationException("按 selector 查询 Pod 失败 namespace={} selector={}", e,
                    namespace, selectorLabels);
        }
    }

    @Override
    public K8sDeploymentInfo getDeployment(String namespace, String name) {
        try {
            Deployment deployment = kubernetesClient.apps().deployments().inNamespace(namespace)
                    .withName(name).get();
            if (ObjectUtil.isNull(deployment)) {
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
    public void deleteDeployment(String namespace, String name) {
        try {
            kubernetesClient.apps().deployments().inNamespace(namespace).withName(name).delete();
            LoggerUtil.info(LogFileEnum.INTEGRATION, "【K8S】delete Deployment 成功 {}/{}", namespace, name);
        } catch (KubernetesClientException e) {
            throw toIntegrationException("delete Deployment 失败 namespace={} name={}", e, namespace, name);
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
                if (ObjectUtil.isNull(event.getInvolvedObject())
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
        if (ObjectUtil.isNotNull(labels)) {
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
        if (ObjectUtil.isNull(status) || ObjectUtil.isNull(status.getConditions())) {
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
        if (ObjectUtil.isNotNull(containers) && CollUtil.isNotEmpty(containers)) {
            info.setImage(containers.get(0).getImage());
        }
        info.setLastDeployTime(parseTimestamp(deployment.getMetadata().getCreationTimestamp()));
        if (ObjectUtil.isNotNull(deployment.getSpec()) && ObjectUtil.isNotNull(deployment.getSpec().getSelector())) {
            info.setSelectorLabels(deployment.getSpec().getSelector().getMatchLabels());
        }

        List<Pod> pods = kubernetesClient.pods().inNamespace(info.getNamespace())
                .withLabel("app", info.getName()).list().getItems();
        if (ObjectUtil.isNotNull(pods) && CollUtil.isNotEmpty(pods)) {
            Pod firstPod = pods.get(0);
            info.setFirstPodName(firstPod.getMetadata().getName());
            info.setNodeName(firstPod.getSpec().getNodeName());
            Map<String, String> nodeLabels = ObjectUtil.isNull(firstPod.getSpec().getNodeName()) ? null
                    : kubernetesClient.nodes().withName(firstPod.getSpec().getNodeName()).get().getMetadata().getLabels();
            if (ObjectUtil.isNotNull(nodeLabels)) {
                info.setNodeArch(nodeLabels.getOrDefault("kubernetes.io/arch", ""));
            }
        }
        return info;
    }

    /**
     * 解析 CPU 毫核（支持核数 4、毫核 500m、纳核 153646246n 三种格式）。
     * 节点 capacity 用核数，metrics-server 用量用纳核（fabric8 拆为 amount + format=n）。
     *
     * @param quantity K8s Quantity
     * @return 毫核；解析失败返回 null
     */
    private Long parseCpuMilli(Quantity quantity) {
        if (ObjectUtil.isNull(quantity)) {
            return null;
        }
        try {
            String value = quantity.getAmount();
            String format = quantity.getFormat();
            if ("n".equals(format)) {
                return Long.valueOf(value) / 1_000_000L;
            }
            if ("m".equals(format) || value.endsWith("m")) {
                String milli = value.endsWith("m") ? value.substring(0, value.length() - 1) : value;
                return Long.valueOf(milli);
            }
            return Double.valueOf(value).longValue() * 1000L;
        } catch (NumberFormatException e) {
            LoggerUtil.warn(LogFileEnum.INTEGRATION, "【K8S】CPU 用量解析失败 value={}", quantity.getAmount());
            return null;
        }
    }

    /**
     * 解析内存字节（支持 Ki/Mi/Gi/B 等格式）。
     *
     * @param quantity K8s Quantity
     * @return 字节；解析失败返回 null
     */
    private Long parseMemoryBytes(Quantity quantity) {
        if (ObjectUtil.isNull(quantity)) {
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
        return ObjectUtil.isNull(time) ? null : time.toLocalDateTime();
    }

    /**
     * 解析 K8s 时间字符串（RFC3339）。
     *
     * @param value 时间字符串
     * @return 本地时间；解析失败返回 null
     */
    private LocalDateTime parseTimestamp(String value) {
        if (ObjectUtil.isNull(value) || value.isBlank()) {
            return null;
        }
        try {
            return OffsetDateTime.parse(value).toLocalDateTime();
        } catch (DateTimeParseException e) {
            LoggerUtil.warn(LogFileEnum.INTEGRATION, "【K8S】时间解析失败 value={}", value);
            return null;
        }
    }

    @Override
    public Map<String, String> getConfigMapData(String namespace, String name) {
        try {
            ConfigMap configMap = kubernetesClient.configMaps().inNamespace(namespace).withName(name).get();
            if (ObjectUtil.isNull(configMap) || ObjectUtil.isNull(configMap.getData())) {
                return new HashMap<>();
            }
            return new HashMap<>(configMap.getData());
        } catch (KubernetesClientException e) {
            throw toIntegrationException("查询 ConfigMap 失败 namespace={} name={}", e, namespace, name);
        }
    }

    @Override
    public void applyConfigMap(String namespace, String name, Map<String, String> data) {
        try {
            ConfigMap existing = kubernetesClient.configMaps().inNamespace(namespace).withName(name).get();
            if (ObjectUtil.isNull(existing)) {
                ConfigMap configMap = new ConfigMapBuilder()
                        .withNewMetadata().withName(name).withNamespace(namespace).endMetadata()
                        .withData(data)
                        .build();
                kubernetesClient.configMaps().inNamespace(namespace).resource(configMap).create();
            } else {
                // fabric8 6.13.4 + Jackson 3：managedFields 序列化会 NPE，更新前必须清掉
                existing.getMetadata().setManagedFields(null);
                ConfigMap configMap = new ConfigMapBuilder(existing).withData(data).build();
                kubernetesClient.configMaps().inNamespace(namespace).resource(configMap).update();
            }
            LoggerUtil.info(LogFileEnum.INTEGRATION, "【K8S】ConfigMap {}/{} 已更新，条目数={}",
                    namespace, name, data.size());
        } catch (KubernetesClientException e) {
            throw toIntegrationException("写入 ConfigMap 失败 namespace={} name={}", e, namespace, name);
        }
    }

    @Override
    public void restartDeployment(String namespace, String name) {
        try {
            Deployment deployment = kubernetesClient.apps().deployments().inNamespace(namespace).withName(name).get();
            if (ObjectUtil.isNull(deployment)) {
                LoggerUtil.error(LogFileEnum.INTEGRATION, "【K8S】Deployment 不存在 {}/{}", namespace, name);
                throw AiIntegrationException.ofThrow(AiIntegrationErrorCode.K8S_API_ERROR,
                        "Deployment 不存在: " + namespace + "/" + name);
            }
            ObjectMeta meta = deployment.getSpec().getTemplate().getMetadata();
            Map<String, String> annotations = ObjectUtil.isNull(meta.getAnnotations())
                    ? new HashMap<>() : new HashMap<>(meta.getAnnotations());
            annotations.put("kubectl.kubernetes.io/restartedAt", String.valueOf(System.currentTimeMillis()));
            meta.setAnnotations(annotations);
            // fabric8 6.13.4 + Jackson 3：managedFields 序列化会 NPE，更新前必须清掉
            deployment.getMetadata().setManagedFields(null);
            kubernetesClient.apps().deployments().inNamespace(namespace).resource(deployment).update();
            LoggerUtil.info(LogFileEnum.INTEGRATION, "【K8S】Deployment {}/{} 已触发滚动重启", namespace, name);
        } catch (KubernetesClientException e) {
            throw toIntegrationException("重启 Deployment 失败 namespace={} name={}", e, namespace, name);
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
