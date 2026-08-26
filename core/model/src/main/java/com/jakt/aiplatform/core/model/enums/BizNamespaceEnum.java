package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jakt.aiplatform.common.framework.enums.BaseEnum;
import lombok.Getter;

/**
 * 业务命名空间枚举：系统允许业务 pod 部署的目标命名空间。
 *
 * <p>优先读环境变量 AIPLATFORM_BIZ_NAMESPACES（逗号分隔），未配置时用本枚举默认值。
 */
@Getter
public enum BizNamespaceEnum implements BaseEnum<String> {

    /** tsk 命名空间（当前集群主命名空间）。 */
    TSK("tsk", "tsk 命名空间"),

    /** test 命名空间。 */
    TEST("test", "test 命名空间"),
    ;

    /** code（命名空间名）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    BizNamespaceEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    @JsonValue
    public String getCode() {
        return code;
    }

    @Override
    public String getName() {
        return name();
    }

    /**
     * 按 code 反查枚举；Jackson 反序列化入口。
     *
     * @param code 命名空间名
     * @return 枚举；未匹配抛 ENUM_NOT_MATCHED
     */
    @JsonCreator
    public static BizNamespaceEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(BizNamespaceEnum.class, code);
    }
}
