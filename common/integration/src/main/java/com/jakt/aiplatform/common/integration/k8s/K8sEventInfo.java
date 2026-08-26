package com.jakt.aiplatform.common.integration.k8s;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * Kubernetes 事件（集成层 DTO）。
 */
@Data
public class K8sEventInfo {

    /** 事件类型。 */
    private String type;

    /** 事件原因。 */
    private String reason;

    /** 事件消息。 */
    private String message;

    /** 出现次数。 */
    private Integer count;

    /** 最近发生时间。 */
    private LocalDateTime lastTimestamp;
}
