package com.jakt.aiplatform.common.util.tools;

import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.error.CommonException;
import com.jakt.aiplatform.common.util.error.CommonErrorCode;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertDoesNotThrow;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

class AssertUtilTest {

    @Test
    void throwErrWhenTrueShouldThrowCommonException() {
        CommonException exception = assertThrows(CommonException.class,
                () -> AssertUtil.throwErrWhenTrue(true, CommonErrorCode.PARAM_INVALID));

        assertEquals("PARAM_INVALID", exception.getErrorCode());
        assertEquals("参数校验失败", exception.getErrorMessage());
    }

    @Test
    void throwErrWhenFalseShouldNotThrowWhenConditionIsTrue() {
        assertDoesNotThrow(() -> AssertUtil.throwErrWhenFalse(true, CommonErrorCode.PARAM_INVALID));
    }

    @Test
    void throwErrWhenNullShouldThrowCommonException() {
        CommonException exception = assertThrows(CommonException.class,
                () -> AssertUtil.throwErrWhenNull(null, CommonErrorCode.PARAM_INVALID, "不能为空"));

        assertEquals("PARAM_INVALID", exception.getErrorCode());
        assertEquals("不能为空", exception.getErrorMessage());
    }
}
