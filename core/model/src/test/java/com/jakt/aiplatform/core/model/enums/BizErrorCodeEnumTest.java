package com.jakt.aiplatform.core.model.enums;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class BizErrorCodeEnumTest {

    @Test
    void getCodeShouldReturnEnumName() {
        assertEquals("RESOURCE_NOT_FOUND", BizErrorCodeEnum.RESOURCE_NOT_FOUND.getCode());
        assertEquals("USERNAME_EXISTS", BizErrorCodeEnum.USERNAME_EXISTS.getCode());
        assertEquals("LOGIN_FAILED", BizErrorCodeEnum.LOGIN_FAILED.getCode());
    }

    @Test
    void getMessageShouldReturnBusinessMessage() {
        assertEquals("用户名或密码错误", BizErrorCodeEnum.LOGIN_FAILED.getMessage());
        assertEquals("用户名已存在", BizErrorCodeEnum.USERNAME_EXISTS.getMessage());
        assertEquals("资源不存在", BizErrorCodeEnum.RESOURCE_NOT_FOUND.getMessage());
    }
}
