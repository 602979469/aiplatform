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

    /** CPU 可分配量（allocatable，毫核）。 */
    private Long cpuAllocatableMilli;

    /** CPU 已用（毫核）。 */
    private Long cpuUsedMilli;

    /** CPU 已分配（该节点所有 pod 的 requests 之和，毫核）。 */
    private Long cpuRequestMilli;

    /** 内存总量（字节）。 */
    private Long memoryTotalBytes;

    /** 内存可分配量（allocatable，字节）。 */
    private Long memoryAllocatableBytes;

    /** 内存已用（字节）。 */
    private Long memoryUsedBytes;

    /** 内存已分配（该节点所有 pod 的 requests 之和，字节）。 */
    private Long memoryRequestBytes;

    /** 该节点上 pod 数量（不含已终态 pod）。 */
    private Integer podCount;

    /** 该节点可调度的 pod 上限（allocatable pods）。 */
    private Integer podAllocatable;

    /** 节点磁盘总量（字节，kubelet summary node.fs）。 */
    private Long diskTotalBytes;

    /** 节点磁盘已用（字节，kubelet summary node.fs）。 */
    private Long diskUsedBytes;
}
