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

    /** 节点 CPU 已用量（毫核）。 */
    private Long cpuUsedMilli;

    /** 节点内存总量（字节）。 */
    private Long memoryTotalBytes;

    /** 节点内存已用量（字节）。 */
    private Long memoryUsedBytes;
}
