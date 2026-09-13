package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 删除域名映射请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterDomainRemoveRequest extends BaseRequest {

    /** 完整域名（xxxx.jakt.online）。 */
    @NotBlank(message = "域名不能为空")
    @Size(max = 253, message = "域名长度不能超过 253")
    private String domain;
}
