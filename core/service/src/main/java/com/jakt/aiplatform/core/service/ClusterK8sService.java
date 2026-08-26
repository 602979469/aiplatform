package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.ClusterDashboard;
import com.jakt.aiplatform.core.model.domain.ClusterRuntimeEvent;
import com.jakt.aiplatform.core.model.domain.ClusterRuntimePod;

import java.util.List;

/**
 * 集群 K8s 领域服务：封装 K8s 远程客户端，向业务层提供独立的领域能力。
 *
 * <p>内部统一走 BizTemplate 执行模板，集成异常由 common-integration 记 INTEGRATION 日志，
 * 本层只负责领域语义与结果组装，不裸奔调用远程客户端。
 */
public interface ClusterK8sService {

    /**
     * 集群大盘数据。
     *
     * @return 集群大盘
     */
    ClusterDashboard getDashboard();

    /**
     * 实时管理列表：按系统管理标签查询各命名空间 Deployment。
     *
     * @param namespaces 业务命名空间列表
     * @return 实时业务 pod 列表
     */
    List<ClusterRuntimePod> listRuntimePods(List<String> namespaces);

    /**
     * 停用：对应 Deployment 缩容到 0。
     *
     * @param namespace 命名空间
     * @param name      Deployment 名称
     */
    void stop(String namespace, String name);

    /**
     * 启用：对应 Deployment 扩容到指定副本数。
     *
     * @param namespace 命名空间
     * @param name      Deployment 名称
     * @param replicas  目标副本数
     */
    void start(String namespace, String name, int replicas);

    /**
     * 查询 Deployment 下首个 Pod 的日志。
     *
     * @param namespace 命名空间
     * @param deploymentName Deployment 名称
     * @return 日志文本
     */
    String getPodLogs(String namespace, String deploymentName);

    /**
     * 查询 Deployment 下首个 Pod 的事件。
     *
     * @param namespace 命名空间
     * @param deploymentName Deployment 名称
     * @return 事件列表
     */
    List<ClusterRuntimeEvent> getPodEvents(String namespace, String deploymentName);
}
