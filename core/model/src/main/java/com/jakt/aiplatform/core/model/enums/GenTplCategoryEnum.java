package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 代码生成使用的模板（crud单表操作 tree树表操作 sub主子表操作）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum GenTplCategoryEnum implements BaseEnum<String> {

    /** 单表操作。 */
    CRUD("crud", "单表操作"),
    /** 树表操作。 */
    TREE("tree", "树表操作"),
    /** 主子表操作。 */
    SUB("sub", "主子表操作"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    GenTplCategoryEnum(String code, String desc) {
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
    public static GenTplCategoryEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(GenTplCategoryEnum.class, code);
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static GenTplCategoryEnum fromCode(String code) {
        return BaseEnum.fromCode(GenTplCategoryEnum.class, code);
    }
}
