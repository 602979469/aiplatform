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

    /** 配置版本号。 */
    @NotBlank(message = "配置版本号不能为空")
    @Size(max = 32, message = "配置版本号长度不能超过 32")
    private String versionNo;

    /** 业务命名空间。 */
    @NotBlank(message = "业务命名空间不能为空")
    @Size(max = 64, message = "业务命名空间长度不能超过 64")
    private String namespace;

    /** git仓库地址（可含token，敏感，仅提交时传入，不回显）。 */
    @NotBlank(message = "git仓库地址不能为空")
    @Size(max = 512, message = "git仓库地址长度不能超过 512")
    private String gitUrl;

    /** git分支。 */
    @NotBlank(message = "git分支不能为空")
    @Size(max = 128, message = "git分支长度不能超过 128")
    private String gitBranch;

    /** Dockerfile内容。 */
    @NotBlank(message = "Dockerfile内容不能为空")
    @Size(max = 65535, message = "Dockerfile内容长度不能超过 65535")
    private String dockerfile;

    /** Deployment YAML（用户编辑后的最终内容）。 */
    @NotBlank(message = "Deployment YAML不能为空")
    @Size(max = 16777215, message = "Deployment YAML长度不能超过 16777215")
    private String deployYaml;

    /** 自动刷新开关（0关 1开）。 */
    private Integer autoRefresh;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;
}
