package com.jakt.aiplatform.common.framework.exception;

import com.jakt.aiplatform.common.framework.error.CommonException;
import com.jakt.aiplatform.common.framework.error.ErrorCode;

/**
 * 业务异常
 */
public class AiPlatformException extends CommonException {

    /**
     * 静态工厂：业务代码直接 {@code throw AiPlatformException.ofThrow(...)}，禁止 throw new。
     *
     * @param errorCode 错误码
     * @return 业务异常
     */
    public static AiPlatformException ofThrow(ErrorCode errorCode) {
        return new AiPlatformException(errorCode.getCode(), errorCode.getMessage());
    }

    /**
     * 静态工厂（带自定义消息）。
     *
     * @param errorCode 错误码
     * @param message   错误消息
     * @return 业务异常
     */
    public static AiPlatformException ofThrow(ErrorCode errorCode, String message) {
        return new AiPlatformException(errorCode.getCode(), message);
    }

    /**
     * 静态工厂（带自定义消息与根因）。
     *
     * @param errorCode 错误码
     * @param message 错误消息
     * @param cause 根因
     * @return 业务异常
     */
    public static AiPlatformException ofThrow(ErrorCode errorCode, String message, Throwable cause) {
        return new AiPlatformException(errorCode.getCode(), message, cause);
    }

    public static AiPlatformException ofThrow(String errorCode, String message) {
        return new AiPlatformException(errorCode, message);
    }

    public AiPlatformException(String errorCode, String message) {
        super(errorCode, message);
    }

    public AiPlatformException(String errorCode, String message, Throwable cause) {
        super(errorCode, message);
        initCause(cause);
    }
}
