package com.jakt.aiplatform.core.model.enums;
import com.jakt.aiplatform.common.framework.enums.BaseEnum;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 菜单类型枚举：M目录 / C菜单 / F按钮。
 */
@Getter
public enum MenuTypeEnum implements BaseEnum<String> {

    /** 目录。 */
    DIRECTORY("M", "目录"),

    /** 菜单。 */
    MENU("C", "菜单"),

    /** 按钮。 */
    BUTTON("F", "按钮"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    MenuTypeEnum(String code, String desc) {
        this.code = code;
        this.desc = desc;
    }

    @Override
    @JsonValue
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
    public static MenuTypeEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(MenuTypeEnum.class, code);
    }
}
