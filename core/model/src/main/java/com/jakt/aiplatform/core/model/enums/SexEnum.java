package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 用户用户性别（0男 1女 2未知）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum SexEnum implements BaseEnum<String> {

    /** 男。 */
    MALE("0", "男"),
    /** 女。 */
    FEMALE("1", "女"),
    /** 未知。 */
    UNKNOWN("2", "未知"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    SexEnum(String code, String desc) {
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
    public static SexEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(SexEnum.class, code);
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static SexEnum fromCode(String code) {
        return BaseEnum.fromCode(SexEnum.class, code);
    }
}
