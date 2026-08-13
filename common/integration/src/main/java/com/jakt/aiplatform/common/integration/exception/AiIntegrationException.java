package com.jakt.aiplatform.common.integration.exception;

import com.jakt.aiplatform.core.model.exception.ErrorCodeCarrier;

/**
 * 外部集成异常：common-integration 内所有外部调用（DeepSeek / XuanYuan 等）失败统一抛出。
 *
 * <p>errorCode 取值见 {@link AiIntegrationErrorCode}，与 core-model ErrorCodeEnum 同名时
 * 可被业务模板解析为业务异常。
 */
public class AiIntegrationException extends RuntimeException implements ErrorCodeCarrier {

    private final String errorCode;

    public AiIntegrationException(String errorCode, String message) {
        super(message);
        this.errorCode = errorCode;
    }

    public AiIntegrationException(String errorCode, String message, Throwable cause) {
        super(message, cause);
        this.errorCode = errorCode;
    }

    @Override
    public String getErrorCode() {
        return errorCode;
    }
}
