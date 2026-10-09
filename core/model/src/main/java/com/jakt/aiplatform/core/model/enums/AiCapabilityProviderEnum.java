package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonValue;
import com.jakt.aiplatform.common.framework.enums.BaseEnum;
import lombok.Getter;

/**
 * AI 能力提供方：决定 {@code AiCapabilityService.invoke} 走哪条链路。
 */
@Getter
public enum AiCapabilityProviderEnum implements BaseEnum<String> {

    /** DeepSeek 直连（原路径：system 提示词 + 用户输入 → 一次性对话）。 */
    DEEPSEEK("DEEPSEEK", "DeepSeek 直连"),

    /** Dify 工作流（Service API blocking 模式，可挂知识库/多步编排）。 */
    DIFY("DIFY", "Dify 工作流"),
    ;

    /** code（数据库存储值）。 */
    private final String code;

    /** 描述。 */
    private final String desc;

    AiCapabilityProviderEnum(String code, String desc) {
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
    public static AiCapabilityProviderEnum fromCodeJson(String code) {
        return BaseEnum.fromCode(AiCapabilityProviderEnum.class, code);
    }
}
