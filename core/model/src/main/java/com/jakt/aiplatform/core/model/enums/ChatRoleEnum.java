package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * AI 对话角色枚举（code 与 DeepSeek 消息角色一致，可直接传给外部接口）。
 */
@Getter
public enum ChatRoleEnum implements BaseEnum<String> {

    /** 用户。 */
    USER("user", "用户"),
    /** 助手。 */
    ASSISTANT("assistant", "助手"),
    /** 系统。 */
    SYSTEM("system", "系统"),
    ;

    /** code（数据库存储值/外部接口角色值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    ChatRoleEnum(String code, String desc) {
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
    public static ChatRoleEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(ChatRoleEnum.class, code);
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static ChatRoleEnum fromCode(String code) {
        return BaseEnum.fromCode(ChatRoleEnum.class, code);
    }
}
