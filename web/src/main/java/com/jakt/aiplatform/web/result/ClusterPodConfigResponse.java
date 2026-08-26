package com.jakt.aiplatform.web.result;


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

    /** 配置版本号。 */
    private String versionNo;

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

    /** 自动刷新开关。 */
    private Integer autoRefresh;

    /** 上次构建commit。 */
    private String lastBuiltCommit;

    /** 创建者。 */
    private String createBy;

    /** 更新者。 */
    private String updateBy;

    /** 备注。 */
    private String remark;

}
