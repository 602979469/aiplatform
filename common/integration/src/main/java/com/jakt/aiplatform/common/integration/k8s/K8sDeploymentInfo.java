package com.jakt.aiplatform.common.integration.k8s;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * Deployment 基础信息（集成层 DTO）。
 */
@Data
public class K8sDeploymentInfo {

    /** Deployment 名称。 */
    private String name;

    /** 命名空间。 */
    private String namespace;

    /** 期望副本数。 */
    private Integer desiredReplicas;

    /** 就绪副本数。 */
    private Integer readyReplicas;

    /** 镜像（首个容器）。 */
    private String image;

    /** 首个 Pod 名称。 */
    private String firstPodName;

    /** 首个 Pod 所在节点。 */
    private String nodeName;

    /** 节点架构。 */
    private String nodeArch;

    /** 最近部署时间。 */
    private LocalDateTime lastDeployTime;

    /** Deployment 的 Pod 选择器标签（用于关联业务 Pod）。 */
    private Map<String, String> selectorLabels;
}
