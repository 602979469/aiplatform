package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 密钥（K8s Secret）分页查询请求：按命名空间 + 关键字实时查询集群。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterSecretQueryRequest extends PageQueryRequest {

    /** 命名空间（缺省 tsk）。 */
    @Size(max = 64, message = "命名空间长度不能超过 64")
    private String namespace;

    /** 名称关键字（模糊匹配 secret 名）。 */
    @Size(max = 64, message = "关键字长度不能超过 64")
    private String keyword;
}
