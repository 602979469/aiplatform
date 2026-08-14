package com.jakt.aiplatform.common.integration.deepseek.model;

/**
 * DeepSeek 消息角色。
 */
public enum DeepSeekRoleEnum {

    SYSTEM("system"),
    USER("user"),
    ASSISTANT("assistant");

    private final String code;

    DeepSeekRoleEnum(String code) {
        this.code = code;
    }

    public String getCode() {
        return code;
    }
}
