package com.jakt.aiplatform.common.util.tools;

import cn.hutool.core.collection.CollUtil;
import com.jakt.aiplatform.common.framework.result.PageResult;

import java.util.ArrayList;
import java.util.List;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * common-util 层集合/分页转换工具。
 */
public final class ConvertUtil {

    private ConvertUtil() {
    }

    public static <T, R> List<R> map(List<T> list, Function<T, R> mapper) {
        if (CollUtil.isEmpty(list)) {
            return new ArrayList<>();
        }
        return list.stream().map(mapper).collect(Collectors.toCollection(ArrayList::new));
    }

    public static <T, R> PageResult<R> mapPage(PageResult<T> page, Function<T, R> mapper) {
        return new PageResult<>(page.getTotal(), page.getPageNum(), page.getPageSize(),
                map(page.getDataList(), mapper));
    }
}
