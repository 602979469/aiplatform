package com.jakt.aiplatform.core.model.param;
import com.jakt.aiplatform.common.framework.param.PageParam;
import com.jakt.aiplatform.core.model.enums.ClusterPodConfigStatusEnum;

import java.time.LocalDateTime;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务pod配置表查询参数。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterPodConfigQueryParam extends PageParam {

    /** 主键。 */
    private Long id;

    /** 资源名称。 */
    private String resourceName;

    /** pod名称。 */
    private String podName;

    /** 业务命名空间。 */
    private String namespace;

    /** Deployment YAML。 */
    private String deployYaml;

    /** 自动刷新开关。 */
    private Integer autoRefresh;

    /** 上次构建commit。 */
    private String lastBuiltCommit;

    /** 配置状态。 */
    private ClusterPodConfigStatusEnum status;

    /** 创建者。 */
    private String createBy;

    /** 更新者。 */
    private String updateBy;

    /** 备注。 */
    private String remark;

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
