package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 参数配置系统内置（Y是 N否）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ConfigTypeEnum implements BaseEnum<String> {

    /** 是。 */
    YES("Y", "是"),
    /** 否。 */
    NO("N", "否"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    ConfigTypeEnum(String code, String desc) {
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
    public static ConfigTypeEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(ConfigTypeEnum.class, code);
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static ConfigTypeEnum fromCode(String code) {
        return BaseEnum.fromCode(ConfigTypeEnum.class, code);
    }
}
