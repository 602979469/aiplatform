package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 角色数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum DataScopeEnum implements BaseEnum<String> {

    /** 全部数据权限。 */
    ALL("1", "全部数据权限"),
    /** 自定数据权限。 */
    CUSTOM("2", "自定数据权限"),
    /** 本部门数据权限。 */
    DEPT("3", "本部门数据权限"),
    /** 本部门及以下数据权限。 */
    DEPT_AND_CHILD("4", "本部门及以下数据权限"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    DataScopeEnum(String code, String desc) {
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
    public static DataScopeEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(DataScopeEnum.class, code);
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static DataScopeEnum fromCode(String code) {
        return BaseEnum.fromCode(DataScopeEnum.class, code);
    }
}
