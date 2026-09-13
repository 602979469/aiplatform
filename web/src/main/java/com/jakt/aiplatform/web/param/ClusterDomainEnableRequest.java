package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 开启公网映射请求（新增 Caddy 站点块）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterDomainEnableRequest extends BaseRequest {

    /** 完整域名（xxxx.jakt.online）。 */
    @NotBlank(message = "域名不能为空")
    @Size(max = 253, message = "域名长度不能超过 253")
    private String domain;

    /** 反代上游（可空，默认 127.0.0.1:8080；非集群服务可填其他 frp 上游）。 */
    @Size(max = 253, message = "上游地址长度不能超过 253")
    private String upstream;
}
