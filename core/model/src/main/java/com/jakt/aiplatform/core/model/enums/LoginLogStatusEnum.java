package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 登录记录结果枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum LoginLogStatusEnum implements BaseEnum<String> {

    /** 成功。 */
    SUCCESS("0", "成功"),

    /** 失败。 */
    FAIL("1", "失败"),

    /** 被踢下线。 */
    KICKOUT("2", "被踢"),

    /** 被顶下线。 */
    REPLACED("3", "被顶"),

    /** 注销。 */
    LOGOUT("4", "注销"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    LoginLogStatusEnum(String code, String desc) {
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
    public static LoginLogStatusEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(LoginLogStatusEnum.class, code);
    }
}
