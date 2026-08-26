package com.jakt.aiplatform.web.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * 实时管理业务 pod 响应。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClusterRuntimePodResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** pod 名称。 */
    private String podName;

    /** 配置版本号。 */
    private String versionNo;

    /** 命名空间。 */
    private String namespace;

    /** 状态。 */
    private String status;

    /** 实际副本数。 */
    private Integer readyReplicas;

    /** 期望副本数。 */
    private Integer desiredReplicas;

    /** 所在节点。 */
    private String nodeName;

    /** 节点架构。 */
    private String arch;

    /** 镜像。 */
    private String image;

    /** 最近部署时间。 */
    private LocalDateTime lastDeployTime;
}
