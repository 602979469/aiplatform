package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 登录日志登录状态（0成功 1失败）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum LoginStatusEnum implements BaseEnum<String> {

    /** 成功。 */
    SUCCESS("0", "成功"),
    /** 失败。 */
    FAIL("1", "失败"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    LoginStatusEnum(String code, String desc) {
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
    public static LoginStatusEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(LoginStatusEnum.class, code);
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static LoginStatusEnum fromCode(String code) {
        return BaseEnum.fromCode(LoginStatusEnum.class, code);
    }
}
