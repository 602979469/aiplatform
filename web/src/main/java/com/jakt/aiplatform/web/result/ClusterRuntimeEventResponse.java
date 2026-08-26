package com.jakt.aiplatform.web.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.time.LocalDateTime;

/**
 * K8s 事件响应。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClusterRuntimeEventResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

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
