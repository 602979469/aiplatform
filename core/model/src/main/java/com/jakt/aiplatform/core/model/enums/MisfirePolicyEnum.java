package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Getter;

/**
 * 定时任务计划执行错误策略（1立即执行 2执行一次 3放弃执行）枚举。
 */
@Getter
@JsonFormat(shape = JsonFormat.Shape.OBJECT)
public enum MisfirePolicyEnum implements BaseEnum<String> {

    /** 立即执行。 */
    EXECUTE_IMMEDIATELY("1", "立即执行"),
    /** 执行一次。 */
    EXECUTE_ONCE("2", "执行一次"),
    /** 放弃执行。 */
    GIVE_UP("3", "放弃执行"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    MisfirePolicyEnum(String code, String desc) {
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
    public static MisfirePolicyEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(MisfirePolicyEnum.class, code);
    }

    /**
     * 按 code 反查枚举（业务代码/Convertor 使用）。
     *
     * @param code code
     * @return 枚举
     */
    public static MisfirePolicyEnum fromCode(String code) {
        return BaseEnum.fromCode(MisfirePolicyEnum.class, code);
    }
}
