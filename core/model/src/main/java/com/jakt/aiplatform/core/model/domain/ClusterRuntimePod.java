package com.jakt.aiplatform.core.model.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实时管理中的业务 pod 状态（k8s client 实时查询结果）。
 */
@Data
public class ClusterRuntimePod implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** pod 名称（配置的 podName）。 */
    private String podName;

    /** 配置 ID（cluster_pod_config 主键，用于日志/事件/启停定位）。 */
    private Long podConfigId;

    /** 命名空间。 */
    private String namespace;

    /** 状态：运行中 / 已停止 / 部署中 / 失败 / 不存在。 */
    private String status;

    /** 实际副本数。 */
    private Integer readyReplicas;

    /** 期望副本数。 */
    private Integer desiredReplicas;

    /** 所在节点。 */
    private String nodeName;

    /** 节点架构。 */
    private String arch;

    /** 镜像（podName:短哈希）。 */
    private String image;

    /** 最近部署时间。 */
    private LocalDateTime lastDeployTime;
}
