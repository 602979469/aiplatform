package com.jakt.aiplatform.core.model.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.Map;

/**
 * 集群节点信息（大盘展示用）。
 */
@Data
public class ClusterNodeInfo implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 节点名称。 */
    private String nodeName;

    /** 角色：master / worker。 */
    private String role;

    /** 架构：AMD / ARM。 */
    private String arch;

    /** 状态：Ready / NotReady。 */
    private String status;

    /** 该节点上系统管理业务 pod 数量，按命名空间分组（如 tsk -> 2）。 */
    private Map<String, Integer> podCountByNamespace;

    /** 节点 CPU 总量（毫核）。 */
    private Long cpuTotalMilli;

    /** 节点 CPU 可分配量（allocatable，毫核）。 */
    private Long cpuAllocatableMilli;

    /** 节点 CPU 已用量（毫核）。 */
    private Long cpuUsedMilli;

    /** 节点 CPU 已分配量（该节点 pod requests 之和，毫核）。 */
    private Long cpuRequestMilli;

    /** 节点内存总量（字节）。 */
    private Long memoryTotalBytes;

    /** 节点内存可分配量（allocatable，字节）。 */
    private Long memoryAllocatableBytes;

    /** 节点内存已用量（字节）。 */
    private Long memoryUsedBytes;

    /** 节点内存已分配量（该节点 pod requests 之和，字节）。 */
    private Long memoryRequestBytes;

    /** 节点磁盘总量（字节）。 */
    private Long diskTotalBytes;

    /** 节点磁盘已用量（字节）。 */
    private Long diskUsedBytes;

    /** 节点上 pod 数量（不含已终态 pod）。 */
    private Integer podCount;

    /** 节点可调度的 pod 上限（allocatable pods）。 */
    private Integer podAllocatable;
}
