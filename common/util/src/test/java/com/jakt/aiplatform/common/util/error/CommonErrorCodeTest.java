package com.jakt.aiplatform.common.util.error;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class CommonErrorCodeTest {

    @Test
    void getCodeShouldReturnEnumName() {
        assertEquals("SYSTEM_ERROR", CommonErrorCode.SYSTEM_ERROR.getCode());
        assertEquals("AUTH_ERROR", CommonErrorCode.AUTH_ERROR.getCode());
        assertEquals("PARAM_INVALID", CommonErrorCode.PARAM_INVALID.getCode());
    }

    @Test
    void getMessageShouldReturnChineseMessage() {
        assertEquals("系统内部错误", CommonErrorCode.SYSTEM_ERROR.getMessage());
        assertEquals("认证失败", CommonErrorCode.AUTH_ERROR.getMessage());
        assertEquals("参数校验失败", CommonErrorCode.PARAM_INVALID.getMessage());
    }
}
