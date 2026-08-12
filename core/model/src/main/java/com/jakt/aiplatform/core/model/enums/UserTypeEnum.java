package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 用户用户类型（00系统用户 01注册用户）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum UserTypeEnum implements BaseEnum<String> {

    /** 系统用户。 */
    SYSTEM_USER("00", "系统用户"),
    /** 注册用户。 */
    REGISTER_USER("01", "注册用户"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    UserTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
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
    public static UserTypeEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(UserTypeEnum.class, code);
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static UserTypeEnum fromCode(String code) {
        return BaseEnum.fromCode(UserTypeEnum.class, code);
    }
}
