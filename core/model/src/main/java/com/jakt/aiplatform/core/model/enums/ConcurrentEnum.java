package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 定时任务是否并发执行（0允许 1禁止）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum ConcurrentEnum implements BaseEnum<String> {

    /** 允许。 */
    ALLOW("0", "允许"),
    /** 禁止。 */
    FORBID("1", "禁止"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    ConcurrentEnum(String code, String desc) {
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
    public static ConcurrentEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(ConcurrentEnum.class, code);
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static ConcurrentEnum fromCode(String code) {
        return BaseEnum.fromCode(ConcurrentEnum.class, code);
    }
}
