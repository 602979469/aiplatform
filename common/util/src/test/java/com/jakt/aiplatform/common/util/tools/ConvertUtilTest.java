package com.jakt.aiplatform.common.util.tools;

import com.jakt.aiplatform.common.framework.result.PageResult;
import org.junit.jupiter.api.Test;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

class ConvertUtilTest {

    @Test
    void mapShouldReturnEmptyListForNull() {
        List<String> result = ConvertUtil.map(null, (String value) -> value.toUpperCase());

        assertTrue(result.isEmpty());
    }

    @Test
    void mapShouldTransformEveryElement() {
        List<String> result = ConvertUtil.map(List.of("a", "b"), (String value) -> value.toUpperCase());

        assertEquals(List.of("A", "B"), result);
    }

    @Test
    void mapPageShouldKeepPageMetadata() {
        PageResult<Integer> source = new PageResult<>(2L, 2, 10, List.of(1, 2));

        PageResult<String> result = ConvertUtil.mapPage(source, String::valueOf);

        assertEquals(2L, result.getTotal());
        assertEquals(2, result.getPageNum());
        assertEquals(10, result.getPageSize());
        assertEquals(List.of("1", "2"), result.getDataList());
    }
}
