package com.jakt.aiplatform.core.model.enums;
import com.jakt.aiplatform.common.framework.enums.BaseEnum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 启用状态枚举：用于用户/角色/菜单的 status 列。
 */
@Getter
public enum EnableStatusEnum implements BaseEnum<String> {

    /** 启用。 */
    ENABLE("0", "启用"),

    /** 停用。 */
    DISABLE("1", "停用"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    EnableStatusEnum(String code, String desc) {
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
     * @param code code
     * @return 枚举
     */
    @JsonCreator
    public static EnableStatusEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(EnableStatusEnum.class, code);
    }
}
