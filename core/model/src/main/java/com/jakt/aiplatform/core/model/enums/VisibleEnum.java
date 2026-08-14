package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 显示状态枚举：菜单 visible 列。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum VisibleEnum implements BaseEnum<String> {

    /** 显示。 */
    SHOW("0", "显示"),

    /** 隐藏。 */
    HIDE("1", "隐藏"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    VisibleEnum(String code, String desc) {
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
    public static VisibleEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(VisibleEnum.class, code);
    }
}
