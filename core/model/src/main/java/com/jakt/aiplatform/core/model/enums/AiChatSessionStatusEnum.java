package com.jakt.aiplatform.core.model.enums;

import lombok.Getter;

/**
 * 用户AI会话状态。
 */
@Getter
public enum AiChatSessionStatusEnum implements BaseEnum<String> {

    NORMAL("0", "正常"),

    DISABLED("1", "停用");

    private final String code;

    private final String desc;

    AiChatSessionStatusEnum(String code, String desc) {
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
}
