package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 业务pod配置分页查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterPodConfigQueryRequest extends PageQueryRequest {

    /** 资源名称（中文名）。 */
    private String resourceName;

    /** pod名称。 */
    private String podName;

    /** 配置版本号。 */
    private String versionNo;

    /** 业务命名空间。 */
    private String namespace;

    /** git分支。 */
    private String gitBranch;

    /** 自动刷新开关（0关 1开）。 */
    private Integer autoRefresh;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;
}
