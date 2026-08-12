package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 通知公告公告状态（0正常 1关闭）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum NoticeStatusEnum implements BaseEnum<String> {

    /** 正常。 */
    NORMAL("0", "正常"),
    /** 关闭。 */
    CLOSED("1", "关闭"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    NoticeStatusEnum(String code, String desc) {
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
    public static NoticeStatusEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(NoticeStatusEnum.class, code);
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static NoticeStatusEnum fromCode(String code) {
        return BaseEnum.fromCode(NoticeStatusEnum.class, code);
    }
}
