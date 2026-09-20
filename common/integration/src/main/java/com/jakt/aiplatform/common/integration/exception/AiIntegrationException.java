package com.jakt.aiplatform.common.integration.exception;

import com.jakt.aiplatform.common.framework.error.CommonException;
import com.jakt.aiplatform.common.framework.error.ErrorCode;

/**
 * 外部集成异常。
 */
public class AiIntegrationException extends CommonException {

    /**
     * 静态工厂：集成代码直接 {@code throw AiIntegrationException.ofThrow(...)}，禁止 throw new。
     *
     * @param errorCode 错误码
     * @return 集成异常
     */
    public static AiIntegrationException ofThrow(ErrorCode errorCode) {
        return new AiIntegrationException(errorCode, errorCode.getMessage());
    }

    /**
     * 静态工厂（带自定义消息）。
     *
     * @param errorCode 错误码
     * @param message 错误消息
     * @return 集成异常
     */
    public static AiIntegrationException ofThrow(ErrorCode errorCode, String message) {
        return new AiIntegrationException(errorCode, message);
    }

    /**
     * 静态工厂（带自定义消息与根因）。
     *
     * @param errorCode 错误码
     * @param message 错误消息
     * @param cause 根因
     * @return 集成异常
     */
    public static AiIntegrationException ofThrow(ErrorCode errorCode, String message, Throwable cause) {
        return new AiIntegrationException(errorCode, message, cause);
    }

    public AiIntegrationException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }

    public AiIntegrationException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode.getCode(), message);
        initCause(cause);
    }
}
