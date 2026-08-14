package com.jakt.aiplatform.common.util.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommonExceptionTest {

    @Test
    void shouldKeepCodeAndMessage() {
        CommonException exception = new CommonException("PARAM_INVALID", "用户名不能为空");

        assertEquals("PARAM_INVALID", exception.getErrorCode());
        assertEquals("用户名不能为空", exception.getErrorMessage());
        assertEquals("用户名不能为空", exception.getMessage());
    }

    @Test
    void ofShouldConvertErrorCodeToCodeString() {
        CommonException exception = CommonException.of(CommonErrorCode.PARAM_INVALID, "字段缺失");

        assertEquals("PARAM_INVALID", exception.getErrorCode());
        assertEquals("字段缺失", exception.getErrorMessage());
    }
}
