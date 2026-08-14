package com.jakt.aiplatform.common.integration.exception;

import com.jakt.aiplatform.common.util.error.CommonException;
import com.jakt.aiplatform.common.util.error.ErrorCode;

/**
 * 外部集成异常。
 */
public class AiIntegrationException extends CommonException {

    public AiIntegrationException(ErrorCode errorCode, String message) {
        super(errorCode.getCode(), message);
    }

    public AiIntegrationException(ErrorCode errorCode, String message, Throwable cause) {
        super(errorCode.getCode(), message);
        initCause(cause);
    }
}
