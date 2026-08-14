package com.jakt.aiplatform.common.integration.exception;

import com.jakt.aiplatform.common.util.error.ErrorCode;

/**
 * 外部集成错误码。
 */
public enum AiIntegrationErrorCode implements ErrorCode {

    DEEPSEEK_API_ERROR("DeepSeek 接口调用失败"),
    XUANYUAN_API_ERROR("镜像加速器接口调用失败"),
    AUTH_ERROR("外部服务认证失败"),
    TIMEOUT("外部服务调用超时"),
    UNKNOWN("未知外部错误");

    private final String message;

    AiIntegrationErrorCode(String message) {
        this.message = message;
    }

    @Override
    public String getCode() {
        return name();
    }

    @Override
    public String getMessage() {
        return message;
    }
}
