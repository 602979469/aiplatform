package com.jakt.aiplatform.biz.service;

import lombok.Data;

import java.util.List;

/**
 * 集群密钥只读视图：只含名称/类型/纳管标签/键名，永不包含 value。
 */
@Data
public class ClusterSecretView {

    /** 命名空间。 */
    private String namespace;

    /** Secret 名称。 */
    private String name;

    /** 类型。 */
    private String type;

    /** 是否带 aiplatform-managed 标签。 */
    private boolean managed;

    /** 键名（已排序）。 */
    private List<String> keys;
}
