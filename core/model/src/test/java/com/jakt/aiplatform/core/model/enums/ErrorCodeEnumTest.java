package com.jakt.aiplatform.core.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class ErrorCodeEnumTest {

    @Test
    void getCodeShouldReturnEnumName() {
        assertEquals("USERNAME_EXISTS", ErrorCodeEnum.USERNAME_EXISTS.getCode());
        assertEquals("LOGIN_FAILED", ErrorCodeEnum.LOGIN_FAILED.getCode());
        assertEquals("SYSTEM_ERROR", ErrorCodeEnum.SYSTEM_ERROR.getCode());
    }

    @Test
    void getMessageShouldReturnBusinessMessage() {
        assertEquals("用户名或密码错误", ErrorCodeEnum.LOGIN_FAILED.getMessage());
        assertEquals("用户名已存在", ErrorCodeEnum.USERNAME_EXISTS.getMessage());
    }
}
