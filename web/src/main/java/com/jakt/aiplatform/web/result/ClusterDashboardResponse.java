package com.jakt.aiplatform.web.result;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serial;
import java.io.Serializable;
import java.util.List;

/**
 * 集群大盘响应。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClusterDashboardResponse implements Serializable {

    @Serial
    private static final long serialVersionUID = 1L;

    /** 节点列表。 */
    private List<ClusterNodeResponse> nodes;

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
