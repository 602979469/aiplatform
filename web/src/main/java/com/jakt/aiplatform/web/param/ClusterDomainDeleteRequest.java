package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 彻底删除公网映射请求（移除 Caddy 站点块，含已关闭记录）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterDomainDeleteRequest extends BaseRequest {

    /** 完整域名（*.jakt.online）。 */
    @NotBlank(message = "域名不能为空")
    @Size(max = 253, message = "域名长度不能超过 253")
    private String domain;
}
