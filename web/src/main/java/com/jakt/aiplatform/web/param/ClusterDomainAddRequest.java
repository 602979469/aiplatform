package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 新增域名映射请求：域名必填；命名空间/Service 同时填写时才创建 Ingress。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterDomainAddRequest extends BaseRequest {

    /** 完整域名（xxxx.jakt.online）。 */
    @NotBlank(message = "域名不能为空")
    @Size(max = 253, message = "域名长度不能超过 253")
    private String domain;

    /** 目标命名空间（可空）。 */
    @Size(max = 64, message = "命名空间长度不能超过 64")
    private String namespace;

    /** 目标 Service 名称（可空）。 */
    @Size(max = 253, message = "Service 名称长度不能超过 253")
    private String service;

    /** 目标端口（可空，默认 80）。 */
    private Integer port;
}
