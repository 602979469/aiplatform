package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 操作日志业务类型（0其它 1新增 2修改 3删除）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum BusinessTypeEnum implements BaseEnum<Integer> {

    /** 其它。 */
    OTHER(0, "其它"),
    /** 新增。 */
    INSERT(1, "新增"),
    /** 修改。 */
    UPDATE(2, "修改"),
    /** 删除。 */
    DELETE(3, "删除"),
    /** 授权。 */
    GRANT(4, "授权"),
    /** 导出。 */
    EXPORT(5, "导出"),
    /** 导入。 */
    IMPORT(6, "导入"),
    /** 强退。 */
    FORCE(7, "强退"),
    /** 生成代码。 */
    GENCODE(8, "生成代码"),
    /** 清空。 */
    CLEAN(9, "清空"),
    ;

    /** code（数据库存储值）。 */
    private final Integer code;

    /** 描述。 */
    private final String desc;

    BusinessTypeEnum(Integer code, String desc) {
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
    public static BusinessTypeEnum fromCodeJson(String code) {
        return code == null ? null : BaseEnum.fromCode(BusinessTypeEnum.class, Integer.valueOf(code));
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static BusinessTypeEnum fromCode(Integer code) {
        return BaseEnum.fromCode(BusinessTypeEnum.class, code);
    }
}
