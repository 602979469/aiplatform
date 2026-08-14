package com.jakt.aiplatform.common.util.result;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ResultTest {

    @Test
    void okShouldSetSuccessAndData() {
        Result<String> result = Result.ok("value");

        assertTrue(result.isSuccess());
        assertEquals("value", result.getData());
        assertNull(result.getErrorCode());
    }

    @Test
    void failShouldSetCodeAndMessage() {
        Result<Void> result = Result.fail("USER_NOT_EXIST", "用户不存在");

        assertFalse(result.isSuccess());
        assertEquals("USER_NOT_EXIST", result.getErrorCode());
        assertEquals("用户不存在", result.getErrorMessage());
    }
}
