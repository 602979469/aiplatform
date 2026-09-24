package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.param.KbExamRuleParam;
import com.jakt.aiplatform.core.model.param.KbExamStartParam;

import java.util.Collection;
import java.util.List;
import java.util.Map;

/**
 * 组卷规则领域服务：规则解析、题型配额与按规则抽题。
 */
public interface KbExamRuleService {

    /**
     * 解析组卷规则：模板优先，其次入参。
     *
     * @param param 开考参数
     * @return 知识点规则列表
     */
    List<KbExamRuleParam> resolveRules(KbExamStartParam param);

    /**
     * 解析总题量：模板优先，其次入参，最后默认值。
     *
     * @param param 开考参数
     * @param rules 知识点规则
     * @return 总题量
     */
    int resolveQuestionCount(KbExamStartParam param, List<KbExamRuleParam> rules);

    /**
     * 题型配额：优先读模板的 typeMix（如 {"解答":14,"单选":4,"判断":2}），未配置时回退固定配比 5:2:1。
     *
     * @param param 开考参数
     * @param questionCount 总题量
     * @return 长度 3 的数组：[选择题配额(单选+多选), 判断题配额, 解答题配额]
     */
    int[] resolveTypeQuotas(KbExamStartParam param, int questionCount);

    /**
     * 统计知识点范围内各题型的可用题量（与抽题同一套筛选口径）。
     *
     * <p>用于让题型配额"看得到库存"：知识点没有的题型不应该占配额。
     * 同一筛选条件在规则里出现多次时只统计一次，避免重复累加。</p>
     *
     * @param rules 知识点规则
     * @param userId 用户ID
     * @param excludeMastered 是否排除已做对
     * @return 题型 → 可用题量
     */
    Map<String, Integer> countAvailableByType(List<KbExamRuleParam> rules, Long userId, boolean excludeMastered);

    /**
     * 题型配额分配：在期望配比基础上按知识点实际可用题量收敛。
     *
     * <p>先按期望配比出题，某题型库存不够时把缺口补给仍有库存的题型组，
     * 保证"游戏（没有解答题）"不会硬留一道解答题配额。</p>
     *
     * @param desired 期望配额 [选择, 判断, 解答]
     * @param availableByType 题型 → 可用题量
     * @param questionCount 目标总题量
     * @return 实际配额 [选择, 判断, 解答]，总和 ≤ questionCount；总和不足说明知识点题量不够
     */
    int[] allocateTypeQuotas(int[] desired, Map<String, Integer> availableByType, int questionCount);

    /**
     * 在知识点范围内补题（不限题型），用于题型配额没抽满时兜底。
     *
     * <p>与历史实现的关键区别：只在所选知识点内补，绝不跨知识点抽题。</p>
     *
     * @param rules 知识点规则
     * @param userId 用户ID
     * @param excludeMastered 是否排除已做对
     * @param need 需要题量
     * @param selected 已选中的题目ID
     * @return 补到的题目ID
     */
    List<Long> pickWithinRules(List<KbExamRuleParam> rules, Long userId, boolean excludeMastered,
                               int need, List<Long> selected);

    /**
     * 按题型分组抽题：把该组配额按各知识点权重分配到规则上，再逐条抽题。
     *
     * @param allowedTypes 允许的题型（如 选择=单选+多选）
     * @param count 该组需要的题量
     * @param rules 知识点规则
     * @param userId 用户ID
     * @param excludeMastered 是否排除已做对
     * @param selected 已选中的题目ID（去重）
     * @return 抽中的题目ID
     */
    List<Long> pickByGroup(List<String> allowedTypes, int count, List<KbExamRuleParam> rules,
                           Long userId, boolean excludeMastered, List<Long> selected);

    /**
     * 按规则 + 题型集合抽题（随机 + 去重 + 排除已掌握）。
     *
     * @param rule 知识点规则
     * @param allowedTypes 允许的题型集合
     * @param userId 用户ID
     * @param excludeMastered 是否排除已做对
     * @param need 需要题量
     * @param selected 已选中的题目ID
     * @return 抽中的题目ID
     */
    List<Long> pick(KbExamRuleParam rule, Collection<String> allowedTypes, Long userId,
                    boolean excludeMastered, int need, List<Long> selected);
}
