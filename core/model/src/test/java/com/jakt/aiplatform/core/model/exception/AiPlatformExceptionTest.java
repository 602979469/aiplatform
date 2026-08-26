package com.jakt.aiplatform.core.model.exception;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class AiPlatformExceptionTest {

    @Test
    void ofThrowShouldKeepEnumCodeAndMessage() {
        AiPlatformException exception = AiPlatformException.ofThrow(BizErrorCodeEnum.USERNAME_EXISTS);

        assertEquals("USERNAME_EXISTS", exception.getErrorCode());
        assertEquals("用户名已存在", exception.getErrorMessage());
    }

    @Test
    void ofThrowShouldAllowCustomMessage() {
        AiPlatformException exception = AiPlatformException.ofThrow(BizErrorCodeEnum.USERNAME_EXISTS, "该用户名已被占用");

        assertEquals("USERNAME_EXISTS", exception.getErrorCode());
        assertEquals("该用户名已被占用", exception.getErrorMessage());
    }
}
