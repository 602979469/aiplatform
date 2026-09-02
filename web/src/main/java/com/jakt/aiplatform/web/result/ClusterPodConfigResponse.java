package com.jakt.aiplatform.web.result;

import com.jakt.aiplatform.core.model.enums.ClusterPodConfigStatusEnum;
import com.jakt.aiplatform.web.result.BaseResult;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 业务pod配置表响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ClusterPodConfigResponse extends BaseResult {
    /** 主键。 */
    private Long id;

    /** 资源名称。 */
    private String resourceName;

    /** pod名称。 */
    private String podName;

    /** 配置状态。 */
    private ClusterPodConfigStatusEnum status;

    /** 业务命名空间。 */
    private String namespace;

    /** Deployment YAML。 */
    private String deployYaml;

    /** 关联 cluster_image.id（部署用）。 */
    private Long imageId;

    /** 上次构建commit。 */
    private String lastBuiltCommit;

    /** 创建者。 */
    private String createBy;

    /** 更新者。 */
    private String updateBy;

    /** 备注。 */
    private String remark;

}
