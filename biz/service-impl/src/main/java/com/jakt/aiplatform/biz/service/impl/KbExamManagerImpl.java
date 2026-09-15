package com.jakt.aiplatform.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.biz.service.KbExamManager;
import com.jakt.aiplatform.biz.service.KbExamPaperView;
import com.jakt.aiplatform.biz.service.KbExamQuestionView;
import com.jakt.aiplatform.biz.service.KbExamResultView;
import com.jakt.aiplatform.biz.service.KbExamRuleParam;
import com.jakt.aiplatform.biz.service.KbExamStartParam;
import com.jakt.aiplatform.biz.service.KbExamWrongView;
import com.jakt.aiplatform.common.dal.dataobject.KbExamPaperDO;
import com.jakt.aiplatform.common.dal.dataobject.KbExamPaperQuestionDO;
import com.jakt.aiplatform.common.dal.dataobject.KbExamTemplateDO;
import com.jakt.aiplatform.common.dal.dataobject.KbExamTemplateRuleDO;
import com.jakt.aiplatform.common.dal.dataobject.KbQuestionDO;
import com.jakt.aiplatform.common.dal.mapper.KbExamPaperMapper;
import com.jakt.aiplatform.common.dal.mapper.KbExamPaperQuestionMapper;
import com.jakt.aiplatform.common.dal.mapper.KbExamTemplateMapper;
import com.jakt.aiplatform.common.dal.mapper.KbExamTemplateRuleMapper;
import com.jakt.aiplatform.common.dal.mapper.KbQuestionMapper;
import com.jakt.aiplatform.common.dal.mapper.KbUserQuestionStatMapper;
import com.jakt.aiplatform.common.dal.query.KbExamPaperDalQuery;
import com.jakt.aiplatform.common.dal.query.KbExamPaperQuestionDalQuery;
import com.jakt.aiplatform.common.dal.query.KbExamTemplateRuleDalQuery;
import com.jakt.aiplatform.common.dal.query.KbQuestionPickQuery;
import com.jakt.aiplatform.common.dal.query.KbQuestionStatDelta;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.template.BizTemplate;
import com.jakt.aiplatform.common.framework.template.TransactionTemplate;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.domain.KbExamPaper;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;

/**
 * 考题系统实现：组卷（排除已掌握题目）→ 答题落库 → 交卷判分 → 回写错题集与掌握度。
 */
@Service
public class KbExamManagerImpl implements KbExamManager {

    /** 默认每题秒数。 */
    private static final int DEFAULT_PER_QUESTION_SECONDS = 60;

    /** 默认题量。 */
    private static final int DEFAULT_QUESTION_COUNT = 10;

    /** 组卷总分。 */
    private static final int TOTAL_SCORE = 100;

    /** 客观题类型。 */
    private static final Set<String> OBJECTIVE_TYPES = Set.of("单选", "多选", "判断");

    private final KbQuestionMapper kbQuestionMapper;

    private final KbExamPaperMapper kbExamPaperMapper;

    private final KbExamPaperQuestionMapper kbExamPaperQuestionMapper;

    private final KbExamTemplateMapper kbExamTemplateMapper;

    private final KbExamTemplateRuleMapper kbExamTemplateRuleMapper;

    private final KbUserQuestionStatMapper kbUserQuestionStatMapper;

    private final TransactionTemplate transactionTemplate;

    public KbExamManagerImpl(KbQuestionMapper kbQuestionMapper,
                             KbExamPaperMapper kbExamPaperMapper,
                             KbExamPaperQuestionMapper kbExamPaperQuestionMapper,
                             KbExamTemplateMapper kbExamTemplateMapper,
                             KbExamTemplateRuleMapper kbExamTemplateRuleMapper,
                             KbUserQuestionStatMapper kbUserQuestionStatMapper,
                             TransactionTemplate transactionTemplate) {
        this.kbQuestionMapper = kbQuestionMapper;
        this.kbExamPaperMapper = kbExamPaperMapper;
        this.kbExamPaperQuestionMapper = kbExamPaperQuestionMapper;
        this.kbExamTemplateMapper = kbExamTemplateMapper;
        this.kbExamTemplateRuleMapper = kbExamTemplateRuleMapper;
        this.kbUserQuestionStatMapper = kbUserQuestionStatMapper;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public KbExamPaperView start(KbExamStartParam param) {
        Long userId = param.getUserId();
        if (userId == null) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "缺少答题用户");
        }
        // 模板开考：未显式指定的参数从模板取默认值
        KbExamTemplateDO template = null;
        if (param.getTemplateId() != null) {
            template = kbExamTemplateMapper.selectById(param.getTemplateId());
            if (template == null) {
                throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "试卷模板不存在");
            }
        }
        List<KbExamRuleParam> rules = resolveRules(param);
        String mode = StrUtil.blankToDefault(param.getMode(),
                template == null ? "NORMAL" : StrUtil.blankToDefault(template.getMode(), "NORMAL"));
        int perQuestionSeconds = firstNonNull(param.getPerQuestionSeconds(),
                template != null && template.getPerQuestionSeconds() != null
                        ? template.getPerQuestionSeconds() : DEFAULT_PER_QUESTION_SECONDS);
        boolean excludeMastered = param.getExcludeMastered() != null
                ? param.getExcludeMastered() == 1
                : template == null || template.getExcludeMastered() == null || template.getExcludeMastered() == 1;
        boolean objectiveOnly = param.getObjectiveOnly() != null
                ? param.getObjectiveOnly() == 1
                : template == null || template.getObjectiveOnly() == null || template.getObjectiveOnly() == 1;
        int questionCount = resolveQuestionCount(param, rules);

        List<Long> picked = new ArrayList<>();
        for (KbExamRuleParam rule : rules) {
            int need = rule.getCount() == null ? 0 : rule.getCount();
            if (need <= 0) {
                continue;
            }
            picked.addAll(pick(rule, userId, excludeMastered, objectiveOnly, need, picked));
        }
        if (picked.size() < questionCount) {
            KbExamRuleParam fill = new KbExamRuleParam();
            picked.addAll(pick(fill, userId, excludeMastered, objectiveOnly,
                    questionCount - picked.size(), picked));
        }
        if (picked.isEmpty()) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID,
                    "没有可用题目：所选知识点下的题目可能都已做对（可切换复习模式）");
        }

        Map<Long, KbQuestionDO> questionMap = new LinkedHashMap<>();
        for (KbQuestionDO row : kbQuestionMapper.selectByIds(picked)) {
            questionMap.put(row.getId(), row);
        }
        List<KbQuestionDO> questions = picked.stream()
                .map(questionMap::get)
                .filter(java.util.Objects::nonNull)
                .collect(Collectors.toList());

        int count = questions.size();
        int baseScore = TOTAL_SCORE / count;
        int remainder = TOTAL_SCORE % count;
        LocalDateTime now = LocalDateTime.now();

        KbExamPaperDO paper = new KbExamPaperDO();
        paper.setUserId(userId);
        paper.setTitle(StrUtil.blankToDefault(param.getTitle(), buildTitle(rules)));
        paper.setMode(mode);
        paper.setStatus("IN_PROGRESS");
        paper.setPerQuestionSeconds(perQuestionSeconds);
        paper.setTimeLimitSeconds(count * perQuestionSeconds);
        paper.setQuestionCount(count);
        paper.setTotalScore(TOTAL_SCORE);
        paper.setStartTime(now);
        paper.setDeadline(now.plusSeconds((long) count * perQuestionSeconds));

        List<KbExamPaperQuestionDO> paperQuestions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            KbQuestionDO question = questions.get(i);
            KbExamPaperQuestionDO row = new KbExamPaperQuestionDO();
            row.setQuestionId(question.getId());
            row.setSeq(i + 1);
            row.setQuestionType(question.getQuestionType());
            row.setCategory(question.getCategory());
            row.setSubtopic(question.getSubtopic());
            row.setTitle(question.getTitle());
            row.setContent(question.getContent());
            row.setOptions(question.getOptions());
            row.setAnswer(question.getAnswer());
            row.setExplanation(question.getExplanation());
            row.setDifficulty(question.getDifficulty());
            row.setScore(baseScore + (i < remainder ? 1 : 0));
            paperQuestions.add(row);
        }

        BizTemplate.executeWithoutResult(transactionTemplate, () -> {
            kbExamPaperMapper.insert(paper);
            for (KbExamPaperQuestionDO row : paperQuestions) {
                row.setPaperId(paper.getId());
                kbExamPaperQuestionMapper.insert(row);
            }
        });
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "组卷成功 paperId={} userId={} 题量={} 模式={}",
                paper.getId(), userId, count, mode);
        if (template != null) {
            // 记录模板使用次数
            KbExamTemplateDO templateUpdate = new KbExamTemplateDO();
            templateUpdate.setId(template.getId());
            templateUpdate.setUseCount((template.getUseCount() == null ? 0 : template.getUseCount()) + 1);
            kbExamTemplateMapper.updateByCondition(templateUpdate);
        }

        KbExamPaperView view = new KbExamPaperView();
        view.setPaperId(paper.getId());
        view.setTitle(paper.getTitle());
        view.setMode(paper.getMode());
        view.setStatus(paper.getStatus());
        view.setQuestionCount(paper.getQuestionCount());
        view.setPerQuestionSeconds(paper.getPerQuestionSeconds());
        view.setTimeLimitSeconds(paper.getTimeLimitSeconds());
        view.setStartTime(paper.getStartTime());
        view.setDeadline(paper.getDeadline());
        view.setRemainingSeconds(remainingSeconds(paper.getDeadline()));
        view.setQuestions(paperQuestions.stream().map(this::toQuestionView).collect(Collectors.toList()));
        return view;
    }

    @Override
    public KbExamPaperView getPaper(Long paperId, Long userId) {
        KbExamPaperDO paper = requirePaper(paperId, userId);
        List<KbExamPaperQuestionDO> rows = listQuestions(paperId);
        KbExamPaperView view = new KbExamPaperView();
        view.setPaperId(paper.getId());
        view.setTitle(paper.getTitle());
        view.setMode(paper.getMode());
        view.setStatus(paper.getStatus());
        view.setQuestionCount(paper.getQuestionCount());
        view.setPerQuestionSeconds(paper.getPerQuestionSeconds());
        view.setTimeLimitSeconds(paper.getTimeLimitSeconds());
        view.setStartTime(paper.getStartTime());
        view.setDeadline(paper.getDeadline());
        view.setRemainingSeconds("IN_PROGRESS".equals(paper.getStatus())
                ? remainingSeconds(paper.getDeadline()) : 0L);
        view.setQuestions(rows.stream().map(this::toQuestionView).collect(Collectors.toList()));
        return view;
    }

    @Override
    public void answer(Long paperId, Long userId, Integer seq, String userAnswer, Integer costSeconds) {
        KbExamPaperDO paper = requirePaper(paperId, userId);
        if (!"IN_PROGRESS".equals(paper.getStatus())) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "试卷已交卷，不能继续作答");
        }
        KbExamPaperQuestionDalQuery query = new KbExamPaperQuestionDalQuery();
        query.setPaperId(paperId);
        query.setSeq(seq);
        KbExamPaperQuestionDO row = kbExamPaperQuestionMapper.selectOne(query);
        if (row == null) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "题号不存在: " + seq);
        }
        KbExamPaperQuestionDO update = new KbExamPaperQuestionDO();
        update.setId(row.getId());
        update.setUserAnswer(userAnswer);
        update.setAnswerCostSeconds(costSeconds);
        update.setAnswerTime(LocalDateTime.now());
        kbExamPaperQuestionMapper.updateByCondition(update);
    }

    @Override
    public KbExamResultView submit(Long paperId, Long userId) {
        KbExamPaperDO paper = requirePaper(paperId, userId);
        if (!"IN_PROGRESS".equals(paper.getStatus())) {
            return result(paperId, userId);
        }
        List<KbExamPaperQuestionDO> rows = listQuestions(paperId);
        int correct = 0;
        int wrong = 0;
        int unanswered = 0;
        int score = 0;
        for (KbExamPaperQuestionDO row : rows) {
            boolean objective = OBJECTIVE_TYPES.contains(row.getQuestionType());
            Integer isCorrect = null;
            if (!objective) {
                // 解答题不自动判分（练习模式看参考答案自评）
            } else if (StrUtil.isBlank(row.getUserAnswer())) {
                isCorrect = 0;
                unanswered++;
            } else if (normalizeAnswer(row.getUserAnswer()).equals(normalizeAnswer(row.getAnswer()))) {
                isCorrect = 1;
                correct++;
                score += row.getScore() == null ? 0 : row.getScore();
            } else {
                isCorrect = 0;
                wrong++;
            }
            KbExamPaperQuestionDO update = new KbExamPaperQuestionDO();
            update.setId(row.getId());
            update.setIsCorrect(isCorrect);
            kbExamPaperQuestionMapper.updateByCondition(update);
            row.setIsCorrect(isCorrect);
            if (objective) {
                writeStat(userId, row.getQuestionId(), isCorrect != null && isCorrect == 1);
            }
        }
        LocalDateTime now = LocalDateTime.now();
        int cost = (int) Duration.between(paper.getStartTime(), now).getSeconds();
        KbExamPaperDO updatePaper = new KbExamPaperDO();
        updatePaper.setId(paperId);
        updatePaper.setStatus("GRADED");
        updatePaper.setScore(score);
        updatePaper.setCorrectCount(correct);
        updatePaper.setWrongCount(wrong);
        updatePaper.setUnansweredCount(unanswered);
        updatePaper.setSubmitTime(now);
        updatePaper.setCostSeconds(cost);
        kbExamPaperMapper.updateByCondition(updatePaper);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "交卷成功 paperId={} userId={} 得分={} 对/错/未答={}/{}/{}",
                paperId, userId, score, correct, wrong, unanswered);
        return result(paperId, userId);
    }

    @Override
    public KbExamResultView result(Long paperId, Long userId) {
        KbExamPaperDO paper = requirePaper(paperId, userId);
        List<KbExamPaperQuestionDO> rows = listQuestions(paperId);
        KbExamResultView view = new KbExamResultView();
        view.setPaperId(paper.getId());
        view.setTitle(paper.getTitle());
        view.setScore(paper.getScore());
        view.setTotalScore(paper.getTotalScore());
        view.setCorrectCount(paper.getCorrectCount());
        view.setWrongCount(paper.getWrongCount());
        view.setUnansweredCount(paper.getUnansweredCount());
        view.setCostSeconds(paper.getCostSeconds());
        view.setSubmitTime(paper.getSubmitTime());
        view.setQuestions(rows.stream().map(row -> {
            KbExamResultView.Item item = new KbExamResultView.Item();
            item.setSeq(row.getSeq());
            item.setQuestionId(row.getQuestionId());
            item.setQuestionType(row.getQuestionType());
            item.setCategory(row.getCategory());
            item.setSubtopic(row.getSubtopic());
            item.setTitle(row.getTitle());
            item.setContent(row.getContent());
            item.setOptions(row.getOptions());
            item.setUserAnswer(row.getUserAnswer());
            item.setAnswer(row.getAnswer());
            item.setExplanation(row.getExplanation());
            item.setIsCorrect(row.getIsCorrect());
            item.setScore(row.getScore());
            return item;
        }).collect(Collectors.toList()));
        return view;
    }

    @Override
    public PageResult<KbExamPaper> history(Long userId, Integer pageNum, Integer pageSize) {
        KbExamPaperDalQuery query = new KbExamPaperDalQuery();
        query.setUserId(userId);
        query.setPageNum(pageNum == null ? 1 : pageNum);
        query.setPageSize(pageSize == null ? 10 : pageSize);
        List<KbExamPaperDO> rows = kbExamPaperMapper.selectPage(query);
        long total = kbExamPaperMapper.countByQuery(query);
        List<KbExamPaper> list = rows.stream().map(this::toPaperDomain).collect(Collectors.toList());
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public PageResult<KbExamWrongView> wrongBook(Long userId, String category, Integer pageNum, Integer pageSize) {
        int safePage = pageNum == null || pageNum < 1 ? 1 : pageNum;
        int safeSize = pageSize == null || pageSize < 1 ? 10 : pageSize;
        int offset = (safePage - 1) * safeSize;
        String safeCategory = StrUtil.trimToNull(category);
        List<Map<String, Object>> rows =
                kbUserQuestionStatMapper.selectWrongBook(userId, safeCategory, safeSize, offset);
        long total = kbUserQuestionStatMapper.countWrongBook(userId, safeCategory);
        List<KbExamWrongView> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            KbExamWrongView item = new KbExamWrongView();
            item.setQuestionId(asLong(row.get("question_id")));
            item.setTitle(asString(row.get("title")));
            item.setCategory(asString(row.get("category")));
            item.setSubtopic(asString(row.get("subtopic")));
            item.setQuestionType(asString(row.get("question_type")));
            item.setAnswer(asString(row.get("answer")));
            item.setUserAnswer(asString(row.get("user_answer")));
            item.setExplanation(asString(row.get("explanation")));
            item.setWrongCount(asInt(row.get("wrong_count")));
            item.setLastAnswerTime(asDateTime(row.get("last_answer_time")));
            list.add(item);
        }
        return new PageResult<>(total, safePage, safeSize, list);
    }

    @Override
    public void markMastered(Long userId, Long questionId) {
        int affected = kbUserQuestionStatMapper.updateWrongBookFlag(userId, questionId, 1, 0);
        if (affected == 0) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "该题目不在你的错题集中");
        }
    }

    /** 解析组卷规则：模板优先。 */
    private List<KbExamRuleParam> resolveRules(KbExamStartParam param) {
        if (param.getTemplateId() != null) {
            KbExamTemplateDO template = kbExamTemplateMapper.selectById(param.getTemplateId());
            if (template == null) {
                throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "试卷模板不存在");
            }
            KbExamTemplateRuleDalQuery query = new KbExamTemplateRuleDalQuery();
            query.setTemplateId(template.getId());
            List<KbExamTemplateRuleDO> rules = kbExamTemplateRuleMapper.selectList(query);
            if (rules == null || rules.isEmpty()) {
                throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "模板未配置知识点范围");
            }
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
        if (param.getRules() == null || param.getRules().isEmpty()) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "请选择模板或至少配置一个知识点");
        }
        return param.getRules();
    }

    /** 题量：模板优先，其次入参，最后默认值。 */
    private int resolveQuestionCount(KbExamStartParam param, List<KbExamRuleParam> rules) {
        if (param.getTemplateId() != null) {
            KbExamTemplateDO template = kbExamTemplateMapper.selectById(param.getTemplateId());
            if (template != null && template.getQuestionCount() != null) {
                return template.getQuestionCount();
            }
        }
        if (param.getQuestionCount() != null && param.getQuestionCount() > 0) {
            return param.getQuestionCount();
        }
        int sum = rules.stream().mapToInt(rule -> rule.getCount() == null ? 0 : rule.getCount()).sum();
        return sum > 0 ? sum : DEFAULT_QUESTION_COUNT;
    }

    /** 按规则抽题（随机 + 去重 + 排除已掌握）。 */
    private List<Long> pick(KbExamRuleParam rule, Long userId, boolean excludeMastered,
                            boolean objectiveOnly, int need, List<Long> selected) {
        KbQuestionPickQuery query = new KbQuestionPickQuery();
        query.setUserId(userId);
        query.setCategory(StrUtil.trimToNull(rule.getCategory()));
        query.setSubtopic(StrUtil.trimToNull(rule.getSubtopic()));
        query.setQuestionType(objectiveOnly && StrUtil.isBlank(rule.getQuestionType())
                ? null : StrUtil.trimToNull(rule.getQuestionType()));
        query.setDifficulty(StrUtil.trimToNull(rule.getDifficulty()));
        query.setExcludeMastered(excludeMastered);
        query.setLimit(Math.max(need * 5, 50));
        List<Long> candidates = kbQuestionMapper.selectPickIds(query);
        if (candidates == null || candidates.isEmpty()) {
            return Collections.emptyList();
        }
        Set<Long> taken = new LinkedHashSet<>(selected);
        List<Long> pool = candidates.stream()
                .filter(id -> !taken.contains(id))
                .collect(Collectors.toList());
        // 只出客观题时，过滤掉解答题（抽题 SQL 不按题型过滤的情况）
        if (objectiveOnly && StrUtil.isBlank(rule.getQuestionType()) && !pool.isEmpty()) {
            Map<Long, KbQuestionDO> map = new LinkedHashMap<>();
            for (KbQuestionDO row : kbQuestionMapper.selectByIds(pool)) {
                map.put(row.getId(), row);
            }
            pool = pool.stream()
                    .filter(id -> map.containsKey(id)
                            && OBJECTIVE_TYPES.contains(map.get(id).getQuestionType()))
                    .collect(Collectors.toList());
        }
        Collections.shuffle(pool);
        return pool.stream().limit(need).collect(Collectors.toList());
    }

    /** 掌握度回写：答对 → 已掌握并移出错题集；答错 → 进错题集。 */
    private void writeStat(Long userId, Long questionId, boolean correct) {
        KbQuestionStatDelta delta = new KbQuestionStatDelta();
        delta.setUserId(userId);
        delta.setQuestionId(questionId);
        delta.setRightDelta(correct ? 1 : 0);
        delta.setWrongDelta(correct ? 0 : 1);
        delta.setLastResult(correct ? 1 : 0);
        delta.setMastered(correct ? 1 : 0);
        delta.setInWrongBook(correct ? 0 : 1);
        kbUserQuestionStatMapper.upsertStat(delta);
    }

    private KbExamPaperDO requirePaper(Long paperId, Long userId) {
        KbExamPaperDO paper = kbExamPaperMapper.selectById(paperId);
        if (paper == null || !paper.getUserId().equals(userId)) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "试卷不存在或无权访问");
        }
        return paper;
    }

    private List<KbExamPaperQuestionDO> listQuestions(Long paperId) {
        KbExamPaperQuestionDalQuery query = new KbExamPaperQuestionDalQuery();
        query.setPaperId(paperId);
        query.setPageNum(1);
        query.setPageSize(200);
        List<KbExamPaperQuestionDO> rows = kbExamPaperQuestionMapper.selectList(query);
        rows.sort((a, b) -> Integer.compare(a.getSeq(), b.getSeq()));
        return rows;
    }

    private KbExamQuestionView toQuestionView(KbExamPaperQuestionDO row) {
        KbExamQuestionView item = new KbExamQuestionView();
        item.setSeq(row.getSeq());
        item.setQuestionId(row.getQuestionId());
        item.setQuestionType(row.getQuestionType());
        item.setCategory(row.getCategory());
        item.setSubtopic(row.getSubtopic());
        item.setTitle(row.getTitle());
        item.setContent(row.getContent());
        item.setOptions(row.getOptions());
        item.setScore(row.getScore());
        item.setUserAnswer(row.getUserAnswer());
        return item;
    }

    private KbExamPaper toPaperDomain(KbExamPaperDO source) {
        KbExamPaper target = new KbExamPaper();
        target.setId(source.getId());
        target.setUserId(source.getUserId());
        target.setTitle(source.getTitle());
        target.setMode(source.getMode());
        target.setStatus(source.getStatus());
        target.setQuestionCount(source.getQuestionCount());
        target.setTotalScore(source.getTotalScore());
        target.setScore(source.getScore());
        target.setCorrectCount(source.getCorrectCount());
        target.setWrongCount(source.getWrongCount());
        target.setUnansweredCount(source.getUnansweredCount());
        target.setCostSeconds(source.getCostSeconds());
        target.setStartTime(source.getStartTime());
        target.setSubmitTime(source.getSubmitTime());
        target.setCreateTime(source.getCreateTime());
        return target;
    }

    /** 答案归一化：去空格、转大写、去重排序（多选顺序无关）。 */
    private String normalizeAnswer(String answer) {
        if (answer == null) {
            return "";
        }
        return java.util.Arrays.stream(answer.split(","))
                .map(String::trim)
                .filter(item -> !item.isEmpty())
                .map(String::toUpperCase)
                .distinct()
                .sorted()
                .collect(Collectors.joining(","));
    }

    private String buildTitle(List<KbExamRuleParam> rules) {
        String scope = rules.stream()
                .map(KbExamRuleParam::getCategory)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .limit(3)
                .collect(Collectors.joining("、"));
        return StrUtil.isBlank(scope) ? "模拟考试" : scope + " 专项练习";
    }

    private long remainingSeconds(LocalDateTime deadline) {
        if (deadline == null) {
            return 0L;
        }
        long seconds = Duration.between(LocalDateTime.now(), deadline).getSeconds();
        return Math.max(seconds, 0L);
    }

    private int firstNonNull(Integer value, int defaultValue) {
        return value == null || value <= 0 ? defaultValue : value;
    }

    private String asString(Object value) {
        return value == null ? null : String.valueOf(value);
    }

    private Long asLong(Object value) {
        return value == null ? null : Long.valueOf(String.valueOf(value));
    }

    private Integer asInt(Object value) {
        return value == null ? null : Integer.valueOf(String.valueOf(value));
    }

    private LocalDateTime asDateTime(Object value) {
        if (value == null) {
            return null;
        }
        if (value instanceof LocalDateTime) {
            return (LocalDateTime) value;
        }
        if (value instanceof java.sql.Timestamp) {
            return ((java.sql.Timestamp) value).toLocalDateTime();
        }
        return LocalDateTime.parse(String.valueOf(value).replace(" ", "T"));
    }

}
