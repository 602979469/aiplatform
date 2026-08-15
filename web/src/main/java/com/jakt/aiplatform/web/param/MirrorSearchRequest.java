package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 镜像搜索请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MirrorSearchRequest extends BaseRequest {

    /** 镜像名称，可带版本号（如 mysql:8），不带则默认 latest。 */
    @NotBlank(message = "镜像名称不能为空")
    private String imageName;

    /** 客户端操作系统（服务端根据 UA 解析，前端可覆盖）。 */
    private String os;

    /** 客户端架构（amd64/arm64，由前端检测传入）。 */
    private String arch;
}
