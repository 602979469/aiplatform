package com.jakt.aiplatform.web.param;

import com.jakt.aiplatform.core.model.enums.ClusterImageTypeEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建镜像请求 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterImageCreateRequest extends BaseRequest {

    /** 标准化镜像名（小写字母/数字/下划线）。 */
    @NotBlank(message = "镜像名不能为空")
    @Pattern(regexp = "^[a-z0-9_]+$", message = "镜像名仅允许小写字母、数字、下划线")
    @Size(max = 128, message = "镜像名长度不能超过 128")
    private String imageName;

    /** 版本/tag。 */
    @NotBlank(message = "版本不能为空")
    @Pattern(regexp = "^[a-zA-Z0-9._-]+$", message = "版本仅允许字母、数字、点、横线、下划线")
    @Size(max = 128, message = "版本长度不能超过 128")
    private String version;

    /** 来源类型（BUILD 自研 / EXTERNAL 现成）。 */
    @NotNull(message = "来源类型不能为空")
    private ClusterImageTypeEnum imageType;

    /** git 地址（imageType=BUILD 必填）。 */
    @Size(max = 512, message = "git地址长度不能超过 512")
    private String gitUrl;

    /** git 分支（imageType=BUILD 必填）。 */
    @Size(max = 128, message = "git分支长度不能超过 128")
    private String gitBranch;

    /** Dockerfile 内容（imageType=BUILD 必填）。 */
    @Size(max = 65535, message = "Dockerfile内容长度不能超过 65535")
    private String dockerfile;

    /** 外部镜像地址（imageType=EXTERNAL 必填）。 */
    @Size(max = 512, message = "外部镜像地址长度不能超过 512")
    private String externalImage;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;
}
