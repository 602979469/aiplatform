package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 镜像下载生成请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class MirrorDownloadRequest extends BaseRequest {

    /** 仓库路径（如 library/mysql）。 */
    @NotBlank(message = "镜像仓库不能为空")
    private String repo;

    /** 版本号/tag。 */
    @NotBlank(message = "镜像版本不能为空")
    private String tag;

    /** 客户端架构（amd64/arm64）。 */
    private String arch;
}
