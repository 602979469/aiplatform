package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 操作日志操作类别（0其它 1后台用户 2手机端用户）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum OperatorTypeEnum implements BaseEnum<Integer> {

    /** 其它。 */
    OTHER(0, "其它"),
    /** 后台用户。 */
    MANAGE(1, "后台用户"),
    /** 手机端用户。 */
    MOBILE(2, "手机端用户"),
    ;

    /** code（数据库存储值）。 */
    private final Integer code;

    /** 描述。 */
    private final String desc;

    OperatorTypeEnum(Integer code, String desc) {
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
    public static OperatorTypeEnum fromCodeJson(String code) {
        return code == null ? null : BaseEnum.fromCode(OperatorTypeEnum.class, Integer.valueOf(code));
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static OperatorTypeEnum fromCode(Integer code) {
        return BaseEnum.fromCode(OperatorTypeEnum.class, code);
    }
}
