package com.jakt.aiplatform.common.integration.k8s;

import lombok.Data;

/**
 * 节点资源用量（metrics-server，集成层 DTO）。
 */
@Data
public class K8sNodeMetric {

    /** 节点名称。 */
    private String nodeName;

    /** CPU 总量（毫核）。 */
    private Long cpuTotalMilli;

    /** CPU 已用（毫核）。 */
    private Long cpuUsedMilli;

    /** 内存总量（字节）。 */
    private Long memoryTotalBytes;

    /** 内存已用（字节）。 */
    private Long memoryUsedBytes;
}
