package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.constant.KbExamConstant;
import com.jakt.aiplatform.core.model.param.KbExamStartParam;
import org.junit.jupiter.api.Test;

import java.util.LinkedHashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertArrayEquals;
import static org.junit.jupiter.api.Assertions.assertEquals;

/**
 * 组卷题型配额单元测试：验证配额不会超过知识点可用题量，也不会跨题型硬凑。
 *
 * <p>回归背景：知识点题型不均衡时（如"游戏"无解答题、"支付深挖"全是解答题），
 * 历史实现会从全库随机补客观题，导致整卷混入其它知识点。</p>
 */
class KbExamRuleQuotaTest {

    /** 只测纯逻辑，仓储不参与，直接传 null。 */
    private final KbExamRuleServiceImpl service = new KbExamRuleServiceImpl(null, null, null);

    private static Map<String, Integer> capacity(int single, int multi, int judge, int essay) {
        Map<String, Integer> map = new LinkedHashMap<>();
        if (single > 0) {
            map.put(KbExamConstant.QUESTION_TYPE_SINGLE, single);
        }
        if (multi > 0) {
            map.put(KbExamConstant.QUESTION_TYPE_MULTI, multi);
        }
        if (judge > 0) {
            map.put(KbExamConstant.QUESTION_TYPE_JUDGE, judge);
        }
        if (essay > 0) {
            map.put(KbExamConstant.QUESTION_TYPE_ESSAY, essay);
        }
        return map;
    }

    @Test
    void fixedRatioShouldMatchLegacyBehaviourForTenQuestions() {
        int[] quotas = service.resolveTypeQuotas(new KbExamStartParam(), 10);

        assertArrayEquals(new int[]{6, 3, 1}, quotas);
    }

    @Test
    void fixedRatioShouldNeverExceedQuestionCountOnTinyPapers() {
        // 历史实现固定 Math.max(1, ...)，1 道题会算出 [1,1,1]，总量比题量还多
        assertArrayEquals(new int[]{1, 0, 0}, service.resolveTypeQuotas(new KbExamStartParam(), 1));
        assertArrayEquals(new int[]{2, 1, 0}, service.resolveTypeQuotas(new KbExamStartParam(), 3));
        assertEquals(10, sum(service.resolveTypeQuotas(new KbExamStartParam(), 10)));
    }

    @Test
    void allocationShouldMoveGapToTypesWithStockWhenEssayMissing() {
        // 游戏：59 单选 + 15 判断，没有解答题 → 解答题的 1 个配额应补到选择题
        int[] quotas = service.allocateTypeQuotas(new int[]{6, 3, 1}, capacity(59, 0, 15, 0), 10);

        assertArrayEquals(new int[]{7, 3, 0}, quotas);
        assertEquals(10, sum(quotas));
    }

    @Test
    void allocationShouldHonourKnowledgePointWithEssayOnly() {
        // 支付深挖 · 技术：29 道全是解答题 → 整卷都出解答题，而不是拿 9 道别的科目的题凑数
        int[] quotas = service.allocateTypeQuotas(new int[]{6, 3, 1}, capacity(0, 0, 0, 29), 10);

        assertArrayEquals(new int[]{0, 0, 10}, quotas);
    }

    @Test
    void allocationShouldReportShortageWhenStockIsNotEnough() {
        int[] quotas = service.allocateTypeQuotas(new int[]{6, 3, 1}, capacity(3, 0, 0, 0), 10);

        assertArrayEquals(new int[]{3, 0, 0}, quotas);
        assertEquals(3, sum(quotas));
    }

    @Test
    void allocationShouldKeepDesiredMixWhenStockIsSufficient() {
        int[] quotas = service.allocateTypeQuotas(new int[]{6, 3, 1}, capacity(93, 0, 31, 324), 10);

        assertArrayEquals(new int[]{6, 3, 1}, quotas);
    }

    @Test
    void allocationShouldScaleDownTemplateMixExceedingQuestionCount() {
        // 模板 typeMix 配了 20 道（4 选择 + 2 判断 + 14 解答），但本次只考 10 道
        int[] quotas = service.allocateTypeQuotas(new int[]{4, 2, 14}, capacity(50, 0, 50, 50), 10);

        assertEquals(10, sum(quotas));
        assertEquals(2, quotas[0]);
        assertEquals(1, quotas[1]);
        assertEquals(7, quotas[2]);
    }

    private static int sum(int[] values) {
        int total = 0;
        for (int value : values) {
            total += value;
        }
        return total;
    }
}
