package com.jakt.aiplatform.web.result;

import lombok.Data;

import java.util.List;

/**
 * 密钥响应：只包含名称/类型/纳管标签/键名，永不包含 value。
 */
@Data
public class ClusterSecretResponse {

    /** 命名空间。 */
    private String namespace;

    /** Secret 名称。 */
    private String name;

    /** 类型（Opaque 等）。 */
    private String type;

    /** 是否带 aiplatform-managed 标签。 */
    private Boolean managed;

    /** 键名列表（只读）。 */
    private List<String> keys;
}
