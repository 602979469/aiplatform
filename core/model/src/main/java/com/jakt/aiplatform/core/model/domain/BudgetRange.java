package com.jakt.aiplatform.core.model.domain;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;
import lombok.Getter;

import java.math.BigDecimal;

/**
 * 预算值对象：把用户填写的预算原文解析成金额上下限（单件）。
 *
 * <p>支持 {@code 800~1200}、{@code 800-1200}、{@code 800～1200}、{@code 800到1200}、
 * {@code 999}、{@code 999.5}、{@code ￥1,200}、{@code 1000左右}；单一金额时上下限相同。
 */
@Getter
public final class BudgetRange {

    /** 区间分隔符：全角/半角/中文写法，顺序即匹配优先级。 */
    private static final String[] SEPARATORS = {"~", "～", "≈", "—", "–", "...", "-", "到", "至"};

    /**
     * 需要剔除的货币与计量单位（装修报价常写「150 元/㎡」「800-1200 每平」）：
     * 长词在前，避免先删掉短词后残留半个单位。
     */
    private static final String[] NOISE_TOKENS = {
            "人民币", "RMB", "rmb", "左右", "平米", "平方", "㎡", "m²", "m2", "延米",
            "￥", "¥", "$", ",", "/", "元", "块", "米", "平", "每", "约", "个", "套", "件", "台", "张"};

    /** 金额（单件）下限。 */
    private final BigDecimal min;

    /** 金额（单件）上限；单一金额时与 {@link #min} 相同。 */
    private final BigDecimal max;

    private BudgetRange(BigDecimal min, BigDecimal max) {
        this.min = min;
        this.max = max;
    }

    /**
     * 解析预算原文；格式非法时抛业务异常。
     *
     * @param text 预算原文，如 800~1200 / 999
     * @return 预算区间
     */
    public static BudgetRange parse(String text) {
        AssertUtil.throwErrWhenBlank(text, BizErrorCodeEnum.PURCHASE_BUDGET_INVALID, "预算不能为空");
        String normalized = normalize(text);
        String[] parts = splitRange(normalized);
        AssertUtil.throwErrWhenTrue(parts.length > 2, BizErrorCodeEnum.PURCHASE_BUDGET_INVALID,
                "预算最多只能有一个区间：" + text);
        BigDecimal first = toAmount(parts[0], text);
        BigDecimal second = parts.length == 2 ? toAmount(parts[1], text) : first;
        boolean reversed = first.compareTo(second) > 0;
        return new BudgetRange(reversed ? second : first, reversed ? first : second);
    }

    /**
     * 是否为一个区间（上下限不同）。
     *
     * @return true=区间
     */
    public boolean isRange() {
        return min.compareTo(max) != 0;
    }

    /**
     * 去掉空格、货币符号、千分位与口语单位。
     */
    private static String normalize(String text) {
        String normalized = StrUtil.replace(StrUtil.trim(text), " ", StrUtil.EMPTY);
        normalized = StrUtil.replace(normalized, "，", ",");
        for (String token : NOISE_TOKENS) {
            normalized = StrUtil.replace(normalized, token, StrUtil.EMPTY);
        }
        return normalized;
    }

    /**
     * 按分隔符切成 1~2 段。
     */
    private static String[] splitRange(String normalized) {
        for (String separator : SEPARATORS) {
            int index = normalized.indexOf(separator);
            if (index >= 0) {
                return new String[]{
                        normalized.substring(0, index),
                        normalized.substring(index + separator.length())};
            }
        }
        return new String[]{normalized};
    }

    /**
     * 单段金额转数值；非法格式抛业务异常。
     */
    private static BigDecimal toAmount(String value, String origin) {
        BigDecimal amount;
        try {
            amount = new BigDecimal(value);
        } catch (NumberFormatException e) {
            throw AiPlatformException.ofThrow(BizErrorCodeEnum.PURCHASE_BUDGET_INVALID, "预算格式不正确：" + origin);
        }
        AssertUtil.throwErrWhenTrue(amount.signum() < 0, BizErrorCodeEnum.PURCHASE_BUDGET_INVALID,
                "预算不能为负数：" + origin);
        return amount;
    }
}
