package com.jakt.aiplatform.core.model.domain;

import com.jakt.aiplatform.common.framework.error.CommonException;
import org.junit.jupiter.api.Test;

import java.math.BigDecimal;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

/**
 * 预算解析自检：覆盖区间 / 精确值 / 脏字符 / 非法输入。
 */
class BudgetRangeTest {

    @Test
    void parseRange() {
        BudgetRange range = BudgetRange.parse("800~1200");
        assertEquals(0, new BigDecimal("800").compareTo(range.getMin()));
        assertEquals(0, new BigDecimal("1200").compareTo(range.getMax()));
        assertTrue(range.isRange());
    }

    @Test
    void parseSingleAmount() {
        BudgetRange range = BudgetRange.parse("999");
        assertEquals(0, new BigDecimal("999").compareTo(range.getMin()));
        assertEquals(0, new BigDecimal("999").compareTo(range.getMax()));
        assertFalse(range.isRange());
    }

    @Test
    void parseDirtyInput() {
        BudgetRange range = BudgetRange.parse(" ￥1,200 到 1,800 元 ");
        assertEquals(0, new BigDecimal("1200").compareTo(range.getMin()));
        assertEquals(0, new BigDecimal("1800").compareTo(range.getMax()));
    }

    @Test
    void parseReversedInput() {
        BudgetRange range = BudgetRange.parse("1200-800");
        assertEquals(0, new BigDecimal("800").compareTo(range.getMin()));
        assertEquals(0, new BigDecimal("1200").compareTo(range.getMax()));
    }

    @Test
    void parseInvalidInput() {
        assertThrows(CommonException.class, () -> BudgetRange.parse("大概一千块吧"));
        assertThrows(CommonException.class, () -> BudgetRange.parse("800~1200~1500"));
        assertThrows(CommonException.class, () -> BudgetRange.parse(" "));
    }
}
