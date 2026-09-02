package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建业务pod配置请求 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterPodConfigCreateRequest extends BaseRequest {

    /** 资源名称（中文名）。 */
    @NotBlank(message = "资源名称不能为空")
    @Size(max = 64, message = "资源名称长度不能超过 64")
    private String resourceName;

    /** pod名称（镜像名）。 */
    @NotBlank(message = "pod名称不能为空")
    @Size(max = 64, message = "pod名称长度不能超过 64")
    private String podName;

    /** 业务命名空间。 */
    @NotBlank(message = "业务命名空间不能为空")
    @Size(max = 64, message = "业务命名空间长度不能超过 64")
    private String namespace;

    /** git仓库地址（已废弃，改用 imageId 绑定已发布镜像；保留兼容旧数据）。 */
    @Size(max = 512, message = "git仓库地址长度不能超过 512")
    private String gitUrl;

    /** git分支（已废弃，保留兼容旧数据）。 */
    @Size(max = 128, message = "git分支长度不能超过 128")
    private String gitBranch;

    /** Dockerfile内容（已废弃，保留兼容旧数据）。 */
    @Size(max = 65535, message = "Dockerfile内容长度不能超过 65535")
    private String dockerfile;

    /** Deployment YAML（用户编辑后的最终内容）。 */
    @NotBlank(message = "Deployment YAML不能为空")
    @Size(max = 16777215, message = "Deployment YAML长度不能超过 16777215")
    private String deployYaml;

    /** 关联 cluster_image.id（部署用，替代 git；可选，旧流程兼容）。 */
    private Long imageId;

    /** 自动刷新开关（0关 1开）。 */
    private Integer autoRefresh;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;
}
