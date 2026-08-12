package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 岗位状态（0正常 1停用）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum PostStatusEnum implements BaseEnum<String> {

    /** 正常。 */
    NORMAL("0", "正常"),
    /** 停用。 */
    DISABLED("1", "停用"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    PostStatusEnum(String code, String desc) {
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
    public static PostStatusEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(PostStatusEnum.class, code);
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static PostStatusEnum fromCode(String code) {
        return BaseEnum.fromCode(PostStatusEnum.class, code);
    }
}
