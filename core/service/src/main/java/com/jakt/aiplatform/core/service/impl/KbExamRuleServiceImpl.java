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
import com.jakt.aiplatform.core.model.dto.KbQuestionTypeStat;
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
     * 固定配比 5:2:1 的配额，按最大余数法取整，保证各题量下总和都等于总题量。
     *
     * @param questionCount 总题量
     * @return 长度 3 的数组：[选择题配额, 判断题配额, 解答题配额]
     */
    private int[] fixedTypeQuotas(int questionCount) {
        return apportion(new int[]{KbExamConstant.RATIO_SELECT, KbExamConstant.RATIO_QA,
                KbExamConstant.RATIO_ESSAY}, questionCount, false);
    }

    @Override
    public Map<String, Integer> countAvailableByType(List<KbExamRuleParam> rules, Long userId,
                                                     boolean excludeMastered) {
        Map<String, Integer> result = new LinkedHashMap<>();
        if (CollUtil.isEmpty(rules)) {
            return result;
        }
        // 同一筛选条件可能被多条规则引用（如模板里同一分类配了多行），只统计一次，避免重复累加
        Set<String> countedScopes = new LinkedHashSet<>();
        for (KbExamRuleParam rule : rules) {
            String scopeKey = StrUtil.join("|",
                    StrUtil.nullToEmpty(StrUtil.trimToNull(rule.getCategory())),
                    StrUtil.nullToEmpty(StrUtil.trimToNull(rule.getSubtopic())),
                    StrUtil.nullToEmpty(StrUtil.trimToNull(rule.getQuestionType())),
                    StrUtil.nullToEmpty(StrUtil.trimToNull(rule.getDifficulty())));
            if (!countedScopes.add(scopeKey)) {
                continue;
            }
            KbQuestionPickParam queryParam = new KbQuestionPickParam();
            queryParam.setUserId(userId);
            queryParam.setCategory(StrUtil.trimToNull(rule.getCategory()));
            queryParam.setSubtopic(StrUtil.trimToNull(rule.getSubtopic()));
            queryParam.setQuestionType(StrUtil.trimToNull(rule.getQuestionType()));
            queryParam.setDifficulty(StrUtil.trimToNull(rule.getDifficulty()));
            queryParam.setExcludeMastered(excludeMastered);
            for (KbQuestionTypeStat stat : kbQuestionRepository.countPickByType(queryParam)) {
                result.merge(stat.getQuestionType(), (int) stat.getTotal(), Integer::sum);
            }
        }
        return result;
    }

    @Override
    public int[] allocateTypeQuotas(int[] desired, Map<String, Integer> availableByType, int questionCount) {
        int[] allocation = new int[3];
        if (questionCount <= 0) {
            return allocation;
        }
        int[] capacity = capacityOf(availableByType);
        int[] target = normalizeDesired(desired, questionCount);

        // 第一轮：按期望配比出题，但不超过该题型的可用题量
        int remaining = questionCount;
        for (int i = 0; i < allocation.length; i++) {
            allocation[i] = Math.max(0, Math.min(Math.min(target[i], capacity[i]), remaining));
            remaining -= allocation[i];
        }

        // 第二轮：缺口按“剩余库存”比例补给还能出题的题型组
        if (remaining > 0) {
            int[] spare = new int[allocation.length];
            int spareTotal = 0;
            for (int i = 0; i < allocation.length; i++) {
                spare[i] = Math.max(0, capacity[i] - allocation[i]);
                spareTotal += spare[i];
            }
            if (spareTotal > 0) {
                int[] extra = apportion(spare, Math.min(remaining, spareTotal), true);
                for (int i = 0; i < allocation.length; i++) {
                    allocation[i] += extra[i];
                }
            }
        }
        return allocation;
    }

    @Override
    public List<Long> pickWithinRules(List<KbExamRuleParam> rules, Long userId, boolean excludeMastered,
                                      int need, List<Long> selected) {
        List<Long> result = new ArrayList<>();
        if (need <= 0 || CollUtil.isEmpty(rules)) {
            return result;
        }
        // 逐条规则轮转补题：同一知识点范围内不限题型，抽不出题就停，不会跨知识点取题
        while (result.size() < need) {
            boolean progressed = false;
            for (KbExamRuleParam rule : rules) {
                if (result.size() >= need) {
                    break;
                }
                List<Long> taken = new ArrayList<>(selected);
                taken.addAll(result);
                List<Long> got = pick(rule, null, userId, excludeMastered, 1, taken);
                if (CollUtil.isNotEmpty(got)) {
                    result.addAll(got);
                    progressed = true;
                }
            }
            if (!progressed) {
                break;
            }
        }
        return result;
    }

    /**
     * 把期望配额收敛到目标总题量：超出时按比例缩小，不足时原样返回（缺口由库存补）。
     *
     * @param desired 期望配额
     * @param questionCount 目标总题量
     * @return 长度 3 的配额
     */
    private int[] normalizeDesired(int[] desired, int questionCount) {
        int[] source = new int[3];
        int sum = 0;
        for (int i = 0; i < source.length; i++) {
            source[i] = ObjectUtil.isNull(desired) || desired.length <= i ? 0 : Math.max(0, desired[i]);
            sum += source[i];
        }
        if (sum <= questionCount) {
            return source;
        }
        return apportion(source, questionCount, false);
    }

    /**
     * 把题型可用题量折算成三个题型组的容量。
     *
     * @param availableByType 题型 → 可用题量
     * @return 长度 3 的数组：[选择题容量, 判断题容量, 解答题容量]
     */
    private int[] capacityOf(Map<String, Integer> availableByType) {
        return new int[]{
                typeCount(availableByType, KbExamConstant.QUESTION_TYPE_SINGLE)
                        + typeCount(availableByType, KbExamConstant.QUESTION_TYPE_MULTI),
                typeCount(availableByType, KbExamConstant.QUESTION_TYPE_JUDGE),
                typeCount(availableByType, KbExamConstant.QUESTION_TYPE_ESSAY)};
    }

    /**
     * 读取某题型的可用题量。
     *
     * @param availableByType 题型 → 可用题量
     * @param questionType 题型
     * @return 可用题量；无库存返回 0
     */
    private int typeCount(Map<String, Integer> availableByType, String questionType) {
        if (CollUtil.isEmpty(availableByType)) {
            return 0;
        }
        Integer value = availableByType.get(questionType);
        return ObjectUtil.isNull(value) || value < 0 ? 0 : value;
    }

    /**
     * 最大余数法按权重分配总数。
     *
     * @param weights 各组权重
     * @param total 待分配总数
     * @param capByWeight 是否限制每组不超过其权重值（用于在剩余库存内分配缺口）
     * @return 各组分到的数量，总和 ≤ total
     */
    private int[] apportion(int[] weights, int total, boolean capByWeight) {
        int[] result = new int[weights.length];
        if (total <= 0) {
            return result;
        }
        int weightSum = 0;
        for (int weight : weights) {
            weightSum += Math.max(0, weight);
        }
        if (weightSum <= 0) {
            return result;
        }
        double[] fraction = new double[weights.length];
        int assigned = 0;
        for (int i = 0; i < weights.length; i++) {
            double exact = total * Math.max(0, weights[i]) / (double) weightSum;
            int floor = (int) Math.floor(exact);
            result[i] = capByWeight ? Math.min(floor, Math.max(0, weights[i])) : floor;
            fraction[i] = exact - floor;
            assigned += result[i];
        }
        int left = total - assigned;
        while (left > 0) {
            int best = -1;
            for (int i = 0; i < weights.length; i++) {
                if (capByWeight && result[i] >= weights[i]) {
                    continue;
                }
                if (best < 0 || fraction[i] > fraction[best]) {
                    best = i;
                }
            }
            if (best < 0) {
                break;
            }
            result[best]++;
            // 每组每轮最多补一次，保证先照顾余数大的组
            fraction[best] = -1D;
            left--;
        }
        return result;
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
