package com.jakt.aiplatform.core.model.domain;

import lombok.Data;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 集群大盘数据（节点信息 + 资源概览 + 业务维度）。
 */
@Data
public class ClusterDashboard implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 节点列表。 */
    private List<ClusterNodeInfo> nodes;

    /** CPU 总量（毫核）。 */
    private Long cpuTotalMilli;

    /** CPU 已用（毫核）。 */
    private Long cpuUsedMilli;

    /** 内存总量（字节）。 */
    private Long memoryTotalBytes;

    /** 内存已用（字节）。 */
    private Long memoryUsedBytes;

    /** 系统管理业务 pod 总数。 */
    private Integer podTotal;

    /** 运行中数量。 */
    private Integer podRunning;

    /** 已停止数量。 */
    private Integer podStopped;

    /** 部署中数量。 */
    private Integer podDeploying;

    /** 失败数量。 */
    private Integer podFailed;
}
