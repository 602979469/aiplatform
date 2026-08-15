package com.jakt.aiplatform.core.model.enums;

import com.fasterxml.jackson.annotation.JsonValue;
import lombok.Getter;

/**
 * 用户AI会话消息状态。
 */
@Getter
public enum AiChatMessageStatusEnum implements BaseEnum<String> {

    NORMAL("0", "正常"),

    FAILED("1", "失败");

    private final String code;

    private final String desc;

    AiChatMessageStatusEnum(String code, String desc) {
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
}
