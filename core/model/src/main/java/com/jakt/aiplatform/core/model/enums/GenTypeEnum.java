package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 代码生成生成代码方式（0zip压缩包 1自定义路径）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GenTypeEnum implements BaseEnum<String> {

    /** zip压缩包。 */
    ZIP("0", "zip压缩包"),
    /** 自定义路径。 */
    CUSTOM_PATH("1", "自定义路径"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    GenTypeEnum(String code, String desc) {
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
    public static GenTypeEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(GenTypeEnum.class, code);
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static GenTypeEnum fromCode(String code) {
        return BaseEnum.fromCode(GenTypeEnum.class, code);
    }
}
