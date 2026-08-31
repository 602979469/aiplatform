package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 业务pod配置表 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterPodConfigDO extends BaseDO {
    /** 主键。 */
    private Long id;

    /** 资源名称。 */
    private String resourceName;

    /** pod名称。 */
    private String podName;

    /** 业务命名空间。 */
    private String namespace;

    /** git仓库地址（可含token，敏感）。 */
    private String gitUrl;

    /** git分支。 */
    private String gitBranch;

    /** Dockerfile内容。 */
    private String dockerfile;

    /** Deployment YAML。 */
    private String deployYaml;

    /** 关联 cluster_image.id（部署用，替代 git）。 */
    private Long imageId;

    /** 自动刷新开关。 */
    private Integer autoRefresh;

    /** 上次构建commit。 */
    private String lastBuiltCommit;

    /** 配置状态。 */
    private String status;

    /** 创建者。 */
    private String createBy;

    /** 更新者。 */
    private String updateBy;

    /** 备注。 */
    private String remark;

}
