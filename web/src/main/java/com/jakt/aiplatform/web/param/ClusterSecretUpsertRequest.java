package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 密钥新增/更新请求：只提交需要覆盖或新增的键值。
 *
 * <p>安全约定：值仅允许前端 → 后端单向传输；接口/日志/响应均不回显任何 value。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class ClusterSecretUpsertRequest extends BaseRequest {

    /** 命名空间。 */
    @NotBlank(message = "命名空间不能为空")
    @Size(max = 64, message = "命名空间长度不能超过 64")
    private String namespace;

    /** K8s Secret 名称（DNS-1123）。 */
    @NotBlank(message = "Secret 名称不能为空")
    @Size(max = 253, message = "Secret 名称长度不能超过 253")
    private String name;

    /** 类型（缺省 Opaque）。 */
    @Size(max = 64, message = "类型长度不能超过 64")
    private String type;

    /** 键值列表（提交即覆盖/新增；未提交的键在集群侧原样保留）。 */
    @NotEmpty(message = "至少提交一个键值")
    private List<Item> keys;

    /** 单个键值项。 */
    @Data
    public static class Item {

        /** 键名。 */
        @NotBlank(message = "键名不能为空")
        @Size(max = 253, message = "键名长度不能超过 253")
        private String key;

        /** 值（新值顶替集群中的旧值，无需提供原值）。 */
        @NotBlank(message = "值不能为空")
        @Size(max = 1048576, message = "值长度不能超过 1MB")
        private String value;
    }
}
