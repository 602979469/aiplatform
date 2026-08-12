package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 操作日志操作状态（0正常 1异常）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BusinessStatusEnum implements BaseEnum<Integer> {

    /** 成功。 */
    SUCCESS(0, "成功"),
    /** 失败。 */
    FAIL(1, "失败"),
    ;

    /** code（数据库存储值）。 */
    private final Integer code;

    /** 描述。 */
    private final String desc;

    BusinessStatusEnum(Integer code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    public Integer getCode() {
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
    public static BusinessStatusEnum fromCodeJson(String code) {
        return code == null ? null : BaseEnum.fromCode(BusinessStatusEnum.class, Integer.valueOf(code));
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static BusinessStatusEnum fromCode(Integer code) {
        return BaseEnum.fromCode(BusinessStatusEnum.class, code);
    }
}
