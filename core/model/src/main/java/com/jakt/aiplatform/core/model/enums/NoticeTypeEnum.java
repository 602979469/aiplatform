package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 通知公告公告类型（1通知 2公告）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NoticeTypeEnum implements BaseEnum<String> {

    /** 通知。 */
    NOTICE("1", "通知"),
    /** 公告。 */
    ANNOUNCEMENT("2", "公告"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    NoticeTypeEnum(String code, String desc) {
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
    public static NoticeTypeEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(NoticeTypeEnum.class, code);
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static NoticeTypeEnum fromCode(String code) {
        return BaseEnum.fromCode(NoticeTypeEnum.class, code);
    }
}
