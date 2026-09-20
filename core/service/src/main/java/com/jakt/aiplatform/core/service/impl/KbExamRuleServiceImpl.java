package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.collection.CollUtil;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.model.constant.KbExamConstant;
import com.jakt.aiplatform.core.model.domain.KbExamTemplate;
import com.jakt.aiplatform.core.model.domain.KbExamTemplateRule;
import com.jakt.aiplatform.core.model.domain.KbQuestion;
import com.jakt.aiplatform.core.model.param.KbExamRuleParam;
import com.jakt.aiplatform.core.model.param.KbExamStartParam;
import com.jakt.aiplatform.core.model.param.KbQuestionPickParam;
import com.jakt.aiplatform.core.repository.KbExamTemplateRepository;
import com.jakt.aiplatform.core.repository.KbExamTemplateRuleRepository;
import com.jakt.aiplatform.core.repository.KbQuestionRepository;
import com.jakt.aiplatform.core.service.KbExamRuleService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 组卷规则领域服务实现：规则解析、题型配额与抽题。
 */
@Service
public class KbExamRuleServiceImpl implements KbExamRuleService {

    /** 抽题放大倍数：多取候选再在内存过滤题型组。 */
    private static final int CANDIDATE_MULTIPLIER = 5;

    /** 抽题候选数量下限。 */
    private static final int CANDIDATE_MIN = 50;

    /** 模板仓储。 */
    private final KbExamTemplateRepository kbExamTemplateRepository;

    /** 模板规则仓储。 */
    private final KbExamTemplateRuleRepository kbExamTemplateRuleRepository;

    /** 题库仓储。 */
    private final KbQuestionRepository kbQuestionRepository;

    public KbExamRuleServiceImpl(KbExamTemplateRepository kbExamTemplateRepository,
                                 KbExamTemplateRuleRepository kbExamTemplateRuleRepository,
                                 KbQuestionRepository kbQuestionRepository) {
        this.kbExamTemplateRepository = kbExamTemplateRepository;
        this.kbExamTemplateRuleRepository = kbExamTemplateRuleRepository;
        this.kbQuestionRepository = kbQuestionRepository;
    }

    @Override
    public List<KbExamRuleParam> resolveRules(KbExamStartParam param) {
        if (ObjectUtil.isNotNull(param.getTemplateId())) {
            KbExamTemplate template = kbExamTemplateRepository.findById(param.getTemplateId());
            AssertUtil.throwErrWhenNull(template, ErrorCodeEnum.PARAM_INVALID, "试卷模板不存在");
            List<KbExamTemplateRule> rules = kbExamTemplateRuleRepository.findByTemplateId(template.getId());
            AssertUtil.throwErrWhenTrue(CollUtil.isEmpty(rules), ErrorCodeEnum.PARAM_INVALID, "模板未配置知识点范围");
            return rules.stream().map(rule -> {
                KbExamRuleParam item = new KbExamRuleParam();
                item.setCategory(rule.getCategory());
                item.setSubtopic(rule.getSubtopic());
                item.setQuestionType(rule.getQuestionType());
                item.setDifficulty(rule.getDifficulty());
                item.setCount(rule.getQuestionCount());
                return item;
            }).collect(Collectors.toList());
        }
        AssertUtil.throwErrWhenTrue(ObjectUtil.isNull(param.getRules()) || CollUtil.isEmpty(param.getRules()),
                ErrorCodeEnum.PARAM_INVALID, "请选择模板或至少配置一个知识点");
        return param.getRules();
    }

    @Override
    public int resolveQuestionCount(KbExamStartParam param, List<KbExamRuleParam> rules) {
        if (ObjectUtil.isNotNull(param.getTemplateId())) {
            KbExamTemplate template = kbExamTemplateRepository.findById(param.getTemplateId());
            if (ObjectUtil.isNotNull(template) && ObjectUtil.isNotNull(template.getQuestionCount())) {
                return template.getQuestionCount();
            }
        }
        if (ObjectUtil.isNotNull(param.getQuestionCount()) && param.getQuestionCount() > 0) {
            return param.getQuestionCount();
        }
        int sum = rules.stream().mapToInt(rule -> ObjectUtil.isNull(rule.getCount()) ? 0 : rule.getCount()).sum();
        return sum > 0 ? sum : KbExamConstant.DEFAULT_QUESTION_COUNT;
    }

    @Override
    public int[] resolveTypeQuotas(KbExamStartParam param, int questionCount) {
        Map<String, Integer> mix = resolveTypeMix(param.getTemplateId());
        if (CollUtil.isNotEmpty(mix)) {
            int select = mix.getOrDefault(KbExamConstant.QUESTION_TYPE_SINGLE, 0)
                    + mix.getOrDefault(KbExamConstant.QUESTION_TYPE_MULTI, 0);
            int qa = mix.getOrDefault(KbExamConstant.QUESTION_TYPE_JUDGE, 0);
            int essay = mix.getOrDefault(KbExamConstant.QUESTION_TYPE_ESSAY, 0);
            if (select + qa + essay > 0) {
                return new int[]{Math.max(0, select), Math.max(0, qa), Math.max(0, essay)};
            }
        }
        return fixedTypeQuotas(questionCount);
    }

    @Override
    public List<Long> pickByGroup(List<String> allowedTypes, int count, List<KbExamRuleParam> rules,
                                  Long userId, boolean excludeMastered, List<Long> selected) {
        List<Long> result = new ArrayList<>();
        if (count <= 0 || CollUtil.isEmpty(rules)) {
            return result;
        }
        int totalWeight = rules.stream().mapToInt(rule -> ObjectUtil.isNull(rule.getCount()) ? 1 : rule.getCount()).sum();
        if (totalWeight <= 0) {
            totalWeight = rules.size();
        }
        int remaining = count;
        for (int i = 0; i < rules.size() && remaining > 0; i++) {
            KbExamRuleParam rule = rules.get(i);
            int weight = ObjectUtil.isNull(rule.getCount()) ? 1 : rule.getCount();
            int quota = (i == rules.size() - 1)
                    ? remaining
                    : Math.max(1, (int) Math.round(count * weight / (double) totalWeight));
            quota = Math.min(quota, remaining);
            List<Long> taken = new ArrayList<>(selected);
            taken.addAll(result);
            List<Long> got = pick(rule, allowedTypes, userId, excludeMastered, quota, taken);
            result.addAll(got);
            remaining = count - result.size();
        }
        return result;
    }

    @Override
    public List<Long> pick(KbExamRuleParam rule, Collection<String> allowedTypes, Long userId,
                           boolean excludeMastered, int need, List<Long> selected) {
        if (need <= 0) {
            return Collections.emptyList();
        }
        KbQuestionPickParam queryParam = new KbQuestionPickParam();
        queryParam.setUserId(userId);
        queryParam.setCategory(StrUtil.trimToNull(rule.getCategory()));
        queryParam.setSubtopic(StrUtil.trimToNull(rule.getSubtopic()));
        // 规则里显式指定了题型时以规则为准，否则按题型组过滤
        queryParam.setQuestionType(StrUtil.trimToNull(rule.getQuestionType()));
        queryParam.setDifficulty(StrUtil.trimToNull(rule.getDifficulty()));
        queryParam.setExcludeMastered(excludeMastered);
        queryParam.setLimit(Math.max(need * CANDIDATE_MULTIPLIER, CANDIDATE_MIN));

        List<Long> candidates = kbQuestionRepository.pickIds(queryParam);
        if (CollUtil.isEmpty(candidates)) {
            return Collections.emptyList();
        }
        Set<Long> taken = new LinkedHashSet<>(selected);
        List<Long> pool = candidates.stream()
                .filter(id -> !taken.contains(id))
                .collect(Collectors.toList());

        // 按题型组过滤（抽题 SQL 只支持单个题型，这里在内存里按分组筛）
        if (ObjectUtil.isNotNull(allowedTypes) && CollUtil.isNotEmpty(allowedTypes) && StrUtil.isBlank(rule.getQuestionType())) {
            Map<Long, KbQuestion> map = new LinkedHashMap<>();
            for (KbQuestion row : kbQuestionRepository.findByIds(pool)) {
                map.put(row.getId(), row);
            }
            pool = pool.stream()
                    .filter(id -> map.containsKey(id) && allowedTypes.contains(map.get(id).getQuestionType()))
                    .collect(Collectors.toList());
        }
        Collections.shuffle(pool);
        return pool.stream().limit(need).collect(Collectors.toList());
    }

    /**
     * 固定配比 5:2:1 的配额（历史行为，兜底用）。
     *
     * @param questionCount 总题量
     * @return 长度 3 的数组：[选择题配额, 判断题配额, 解答题配额]
     */
    private int[] fixedTypeQuotas(int questionCount) {
        int essayQuota = Math.max(1, (int) Math.round(questionCount * KbExamConstant.RATIO_ESSAY
                / (double) KbExamConstant.RATIO_TOTAL));
        int qaQuota = Math.max(1, (int) Math.round(questionCount * KbExamConstant.RATIO_QA
                / (double) KbExamConstant.RATIO_TOTAL));
        int selectQuota = Math.max(1, questionCount - essayQuota - qaQuota);
        return new int[]{selectQuota, qaQuota, essayQuota};
    }

    /**
     * 解析模板的题型配比 JSON。
     *
     * @param templateId 模板ID
     * @return 题型 → 题量；未配置或解析失败返回空 Map
     */
    private Map<String, Integer> resolveTypeMix(Long templateId) {
        if (ObjectUtil.isNull(templateId)) {
            return new LinkedHashMap<>();
        }
        KbExamTemplate template = kbExamTemplateRepository.findById(templateId);
        if (ObjectUtil.isNull(template) || StrUtil.isBlank(template.getTypeMix())) {
            return new LinkedHashMap<>();
        }
        Map<String, Integer> mix = new LinkedHashMap<>();
        try {
            JSONObject json = JSONUtil.parseObj(template.getTypeMix());
            for (String key : json.keySet()) {
                mix.put(key, json.getInt(key, 0));
            }
        } catch (Exception e) {
            LoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "【考试】模板 {} 的题型配比解析失败: {}",
                    templateId, template.getTypeMix());
            return new LinkedHashMap<>();
        }
        return mix;
    }
}
