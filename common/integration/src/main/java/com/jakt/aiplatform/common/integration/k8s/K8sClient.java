package com.jakt.aiplatform.common.integration.k8s;

import java.util.List;

/**
 * Kubernetes 集群基础客户端：只提供最基础的查询/操作能力，业务语义由 core-service domain 层封装。
 *
 * <p>所有方法失败抛 {@link com.jakt.aiplatform.common.integration.exception.AiIntegrationException}，
 * 集成层内部已按 {@link com.jakt.aiplatform.common.framework.enums.LogFileEnum#INTEGRATION} 记录日志。
 */
public interface K8sClient {

    /**
     * 集群节点列表。
     *
     * @return 节点列表
     */
    List<K8sNodeInfo> listNodes();

    /**
     * 节点资源用量（依赖 metrics-server，未安装时为空列表）。
     *
     * @return 节点资源用量列表
     */
    List<K8sNodeMetric> listNodeMetrics();

    /**
     * 按名称查询 Deployment。
     *
     * @param namespace 命名空间
     * @param name      Deployment 名称
     * @return Deployment 信息；不存在返回 null
     */
    K8sDeploymentInfo getDeployment(String namespace, String name);

    /**
     * 按标签查询 Deployment 列表。
     *
     * @param namespace 命名空间
     * @param labelKey   标签键
     * @param labelValue 标签值
     * @return Deployment 列表
     */
    List<K8sDeploymentInfo> listDeploymentsByLabel(String namespace, String labelKey, String labelValue);

    /**
     * 调整 Deployment 副本数。
     *
     * @param namespace  命名空间
     * @param name       Deployment 名称
     * @param replicas   目标副本数
     */
    void scaleDeployment(String namespace, String name, int replicas);

    /**
     * 应用 YAML（创建或更新）。
     *
     * @param yaml 完整 YAML 内容
     */
    void applyYaml(String yaml);

    /**
     * 按 YAML 删除资源。
     *
     * @param yaml 完整 YAML 内容
     */
    void deleteByYaml(String yaml);

    /**
     * 按名称删除 Deployment。
     *
     * @param namespace 命名空间
     * @param name      Deployment 名称
     */
    void deleteDeployment(String namespace, String name);

    /**
     * 查询 Pod 日志。
     *
     * @param namespace 命名空间
     * @param podName   Pod 名称
     * @return 日志文本
     */
    String getPodLogs(String namespace, String podName);

    /**
     * 查询命名空间下 Pod 相关事件。
     *
     * @param namespace 命名空间
     * @param podName   Pod 名称
     * @return 事件列表
     */
    List<K8sEventInfo> listPodEvents(String namespace, String podName);
}
