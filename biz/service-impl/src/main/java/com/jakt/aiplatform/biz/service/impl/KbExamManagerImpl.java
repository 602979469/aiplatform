package com.jakt.aiplatform.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
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
import com.jakt.aiplatform.core.service.AiCapabilityService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * 考题系统实现：组卷（排除已掌握题目）→ 答题落库 → 交卷判分 → 回写错题集与掌握度。
 */
@Service
public class KbExamManagerImpl implements KbExamManager {

    /** 默认每题秒数。 */
    private static final int DEFAULT_PER_QUESTION_SECONDS = 60;

    /** 默认题量。 */
    private static final int DEFAULT_QUESTION_COUNT = 10;

    /** AI 判分场景码。 */
    private static final String AI_SCENE_EXAM = "EXAM";

    /** AI 判分能力码。 */
    private static final String AI_CAPABILITY_GRADING = "ANSWER_GRADING";

    /** 客观题类型（1 分/题）。 */
    private static final Set<String> OBJECTIVE_TYPES = Set.of("单选", "多选", "判断");

    /** 客观题每题分值。 */
    private static final int SCORE_OBJECTIVE = 1;

    /** 解答题每题分值（满分，最终得分由 AI 判分决定）。 */
    private static final int SCORE_ESSAY = 5;

    /**
     * 题型配比：选择 : 问答 : 解答 = 5 : 2 : 1（按 8 份折算，每类至少 1 题）。
     *
     * <p>TODO 后续考虑做成表配置（题型配比模板，可按考试类型/难度自定义），当前先写死。
     */
    private static final int RATIO_SELECT = 5;

    /** 问答题配比份数。 */
    private static final int RATIO_QA = 2;

    /** 解答题配比份数。 */
    private static final int RATIO_ESSAY = 1;

    /** 配比总份数。 */
    private static final int RATIO_TOTAL = RATIO_SELECT + RATIO_QA + RATIO_ESSAY;

    /** 选择题型集合（单选 + 多选）。 */
    private static final List<String> GROUP_SELECT = List.of("单选", "多选");

    /** 问答题型集合（当前题库对应"判断"，后续新增问答题类型时在此扩展）。 */
    private static final List<String> GROUP_QA = List.of("判断");

    /** 解答题型集合。 */
    private static final List<String> GROUP_ESSAY = List.of("解答");

    private final KbQuestionMapper kbQuestionMapper;

    private final KbExamPaperMapper kbExamPaperMapper;

    private final KbExamPaperQuestionMapper kbExamPaperQuestionMapper;

    private final KbExamTemplateMapper kbExamTemplateMapper;

    private final KbExamTemplateRuleMapper kbExamTemplateRuleMapper;

    private final KbUserQuestionStatMapper kbUserQuestionStatMapper;

    private final TransactionTemplate transactionTemplate;

    /** AI 能力服务（解答题判分）。 */
    private final AiCapabilityService aiCapabilityService;

    public KbExamManagerImpl(KbQuestionMapper kbQuestionMapper,
                             KbExamPaperMapper kbExamPaperMapper,
                             KbExamPaperQuestionMapper kbExamPaperQuestionMapper,
                             KbExamTemplateMapper kbExamTemplateMapper,
                             KbExamTemplateRuleMapper kbExamTemplateRuleMapper,
                             KbUserQuestionStatMapper kbUserQuestionStatMapper,
                             TransactionTemplate transactionTemplate,
                             AiCapabilityService aiCapabilityService) {
        this.kbQuestionMapper = kbQuestionMapper;
        this.kbExamPaperMapper = kbExamPaperMapper;
        this.kbExamPaperQuestionMapper = kbExamPaperQuestionMapper;
        this.kbExamTemplateMapper = kbExamTemplateMapper;
        this.kbExamTemplateRuleMapper = kbExamTemplateRuleMapper;
        this.kbUserQuestionStatMapper = kbUserQuestionStatMapper;
        this.transactionTemplate = transactionTemplate;
        this.aiCapabilityService = aiCapabilityService;
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
        int questionCount = resolveQuestionCount(param, rules);

        // 题型配额：优先用模板的 typeMix（如 {"解答":14,"单选":4,"判断":2}），没配则回退固定配比 5:2:1
        int[] quotas = resolveTypeQuotas(param, questionCount);
        int selectQuota = quotas[0];
        int qaQuota = quotas[1];
        int essayQuota = quotas[2];

        List<Long> picked = new ArrayList<>();
        picked.addAll(pickByGroup(GROUP_SELECT, selectQuota, rules, userId, excludeMastered, picked));
        picked.addAll(pickByGroup(GROUP_QA, qaQuota, rules, userId, excludeMastered, picked));
        picked.addAll(pickByGroup(GROUP_ESSAY, essayQuota, rules, userId, excludeMastered, picked));
        // 某类题量不足时用客观题补足，保证整卷题量
        if (picked.size() < questionCount) {
            KbExamRuleParam fill = new KbExamRuleParam();
            picked.addAll(pick(fill, OBJECTIVE_TYPES, userId, excludeMastered,
                    questionCount - picked.size(), picked));
        }
        if (picked.isEmpty()) {
            // 区分两种为空：知识点本身没有题（配置问题） vs 都做对了（可切换复习模式）
            for (KbExamRuleParam rule : rules) {
                KbQuestionPickQuery probe = new KbQuestionPickQuery();
                probe.setCategory(StrUtil.trimToNull(rule.getCategory()));
                probe.setSubtopic(StrUtil.trimToNull(rule.getSubtopic()));
                probe.setQuestionType(StrUtil.trimToNull(rule.getQuestionType()));
                probe.setDifficulty(StrUtil.trimToNull(rule.getDifficulty()));
                probe.setExcludeMastered(false);
                probe.setLimit(1);
                List<Long> exists = kbQuestionMapper.selectPickIds(probe);
                if (exists == null || exists.isEmpty()) {
                    String target = StrUtil.isBlank(rule.getSubtopic())
                            ? rule.getCategory() : rule.getCategory() + "/" + rule.getSubtopic();
                    throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID,
                            "知识点「" + target + "」下暂无题目，请重新配置试卷");
                }
            }
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
        int totalScore = 0;
        for (KbQuestionDO question : questions) {
            totalScore += scoreOf(question.getQuestionType());
        }
        LocalDateTime now = LocalDateTime.now();

        KbExamPaperDO paper = new KbExamPaperDO();
        paper.setUserId(userId);
        paper.setTitle(StrUtil.blankToDefault(param.getTitle(), buildTitle(rules)));
        paper.setMode(mode);
        paper.setStatus("IN_PROGRESS");
        paper.setPerQuestionSeconds(perQuestionSeconds);
        paper.setTimeLimitSeconds(count * perQuestionSeconds);
        paper.setQuestionCount(count);
        paper.setTotalScore(totalScore);
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
            row.setScore(scoreOf(question.getQuestionType()));
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
        return submitInternal(paperId, userId, true);
    }

    /**
     * 交卷判分。
     *
     * @param aiGradeEssay 是否对解答题调用 AI 判分；列表超时兜底时为 false（避免拖慢查询）
     */
    private KbExamResultView submitInternal(Long paperId, Long userId, boolean aiGradeEssay) {
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
            int fullScore = row.getScore() == null ? scoreOf(row.getQuestionType()) : row.getScore();
            int actualScore = 0;
            String aiComment = null;
            if (StrUtil.isBlank(row.getUserAnswer())) {
                // 未作答：客观题与解答题都记 0 分
                isCorrect = 0;
                unanswered++;
            } else if (objective) {
                if (normalizeAnswer(row.getUserAnswer()).equals(normalizeAnswer(row.getAnswer()))) {
                    isCorrect = 1;
                    actualScore = fullScore;
                    correct++;
                } else {
                    isCorrect = 0;
                    wrong++;
                }
            } else if (!aiGradeEssay) {
                // TODO 后续用定时任务/异步补判：这里（列表超时兜底）不调 AI，解答题先置为待判分
                isCorrect = null;
                actualScore = 0;
                aiComment = "超时自动交卷，解答题待 AI 判分";
                answerScoreRow(row, isCorrect, actualScore, aiComment);
                continue;
            } else {
                // 解答题：调用 AI 能力判分（严格的面试官口径，0~满分）
                AiGrade grade = gradeEssay(row, fullScore);
                actualScore = grade.score;
                aiComment = grade.comment;
                isCorrect = actualScore * 2 >= fullScore ? 1 : 0;
                if (isCorrect == 1) {
                    correct++;
                } else {
                    wrong++;
                }
            }
            score += actualScore;
            answerScoreRow(row, isCorrect, actualScore, aiComment);
            writeStat(userId, row.getQuestionId(), isCorrect != null && isCorrect == 1);
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
            item.setActualScore(row.getActualScore());
            item.setAiComment(row.getAiComment());
            return item;
        }).collect(Collectors.toList()));
        return view;
    }

    @Override
    public PageResult<KbExamPaper> history(Long userId, Integer pageNum, Integer pageSize) {
        // TODO 后续改为定时任务统一处理：这里在查询列表时兜底把「已超时但仍在进行中」的试卷自动交卷，
        //      避免用户中途退出后记录一直挂在"未完成"。单次判断成本极低（只查当前用户的进行中记录）。
        autoSubmitExpired(userId);
        KbExamPaperDalQuery query = new KbExamPaperDalQuery();
        query.setUserId(userId);
        query.setPageNum(pageNum == null ? 1 : pageNum);
        query.setPageSize(pageSize == null ? 10 : pageSize);
        List<KbExamPaperDO> rows = kbExamPaperMapper.selectPage(query);
        long total = kbExamPaperMapper.countByQuery(query);
        List<KbExamPaper> list = rows.stream().map(this::toPaperDomain).collect(Collectors.toList());
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    /** 超时兜底：进行中且已过 deadline 的试卷按已作答内容自动交卷。 */
    private void autoSubmitExpired(Long userId) {
        KbExamPaperDalQuery query = new KbExamPaperDalQuery();
        query.setUserId(userId);
        query.setStatus("IN_PROGRESS");
        query.setPageNum(1);
        query.setPageSize(50);
        List<KbExamPaperDO> papers = kbExamPaperMapper.selectList(query);
        if (papers == null || papers.isEmpty()) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (KbExamPaperDO paper : papers) {
            if (paper.getDeadline() != null && paper.getDeadline().isBefore(now)) {
                LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "考试超时自动交卷 paperId={} userId={}",
                        paper.getId(), userId);
                // 列表兜底不调 AI 判分（避免拖慢查询），解答题置为待判分
                submitInternal(paper.getId(), userId, false);
            }
        }
    }

    @Override
    public void deletePaper(Long paperId, Long userId) {
        requirePaper(paperId, userId);
        BizTemplate.executeWithoutResult(transactionTemplate, () -> {
            kbExamPaperQuestionMapper.deleteByPaperId(paperId);
            kbExamPaperMapper.deleteById(paperId);
        });
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除考试记录 paperId={} userId={}", paperId, userId);
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

    /**
     * 题型配额：优先读模板的 typeMix（如 {"解答":14,"单选":4,"判断":2}），未配置时回退固定配比 5:2:1。
     *
     * @param param         开考参数
     * @param questionCount 总题量
     * @return 长度 3 的数组：[选择题配额(单选+多选), 判断题配额, 解答题配额]
     */
    private int[] resolveTypeQuotas(KbExamStartParam param, int questionCount) {
        Map<String, Integer> mix = resolveTypeMix(param.getTemplateId());
        if (!mix.isEmpty()) {
            int select = mix.getOrDefault("单选", 0) + mix.getOrDefault("多选", 0);
            int qa = mix.getOrDefault("判断", 0);
            int essay = mix.getOrDefault("解答", 0);
            if (select + qa + essay > 0) {
                return new int[]{Math.max(0, select), Math.max(0, qa), Math.max(0, essay)};
            }
        }
        return fixedTypeQuotas(questionCount);
    }

    /**
     * 固定配比 5:2:1 的配额（历史行为，兜底用）。
     *
     * @param questionCount 总题量
     * @return 长度 3 的数组：[选择题配额, 判断题配额, 解答题配额]
     */
    private int[] fixedTypeQuotas(int questionCount) {
        int essayQuota = Math.max(1, (int) Math.round(questionCount * RATIO_ESSAY / (double) RATIO_TOTAL));
        int qaQuota = Math.max(1, (int) Math.round(questionCount * RATIO_QA / (double) RATIO_TOTAL));
        int selectQuota = Math.max(1, questionCount - essayQuota - qaQuota);
        return new int[]{selectQuota, qaQuota, essayQuota};
    }

    /**
     * 解析模板的题型配比 JSON。
     *
     * @param templateId 模板ID
     * @return 题型 -> 题量；未配置或解析失败返回空 Map
     */
    private Map<String, Integer> resolveTypeMix(Long templateId) {
        if (templateId == null) {
            return new LinkedHashMap<>();
        }
        KbExamTemplateDO template = kbExamTemplateMapper.selectById(templateId);
        if (template == null || StrUtil.isBlank(template.getTypeMix())) {
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

    /**
     * 按题型分组抽题：把一个题型组的配额按各知识点权重分配到规则上，再逐条抽题。
     *
     * @param allowedTypes    允许的题型（如 选择=单选+多选）
     * @param count           该组需要的题量
     * @param rules           知识点规则
     * @param userId          用户ID
     * @param excludeMastered 是否排除已做对
     * @param selected        已选中的题目ID（去重）
     * @return 抽中的题目ID
     */
    private List<Long> pickByGroup(List<String> allowedTypes, int count, List<KbExamRuleParam> rules,
                                   Long userId, boolean excludeMastered, List<Long> selected) {
        List<Long> result = new ArrayList<>();
        if (count <= 0 || rules.isEmpty()) {
            return result;
        }
        int totalWeight = rules.stream().mapToInt(rule -> rule.getCount() == null ? 1 : rule.getCount()).sum();
        if (totalWeight <= 0) {
            totalWeight = rules.size();
        }
        int remaining = count;
        for (int i = 0; i < rules.size() && remaining > 0; i++) {
            KbExamRuleParam rule = rules.get(i);
            int weight = rule.getCount() == null ? 1 : rule.getCount();
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

    /** 按规则 + 题型集合抽题（随机 + 去重 + 排除已掌握）。 */
    private List<Long> pick(KbExamRuleParam rule, Collection<String> allowedTypes, Long userId,
                            boolean excludeMastered, int need, List<Long> selected) {
        if (need <= 0) {
            return Collections.emptyList();
        }
        KbQuestionPickQuery query = new KbQuestionPickQuery();
        query.setUserId(userId);
        query.setCategory(StrUtil.trimToNull(rule.getCategory()));
        query.setSubtopic(StrUtil.trimToNull(rule.getSubtopic()));
        // 规则里显式指定了题型时以规则为准，否则按题型组过滤
        query.setQuestionType(StrUtil.trimToNull(rule.getQuestionType()));
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
        // 按题型组过滤（抽题 SQL 只支持单个题型，这里在内存里按分组筛）
        if (allowedTypes != null && !allowedTypes.isEmpty() && StrUtil.isBlank(rule.getQuestionType())) {
            Map<Long, KbQuestionDO> map = new LinkedHashMap<>();
            for (KbQuestionDO row : kbQuestionMapper.selectByIds(pool)) {
                map.put(row.getId(), row);
            }
            pool = pool.stream()
                    .filter(id -> map.containsKey(id)
                            && allowedTypes.contains(map.get(id).getQuestionType()))
                    .collect(Collectors.toList());
        }
        Collections.shuffle(pool);
        return pool.stream().limit(need).collect(Collectors.toList());
    }

    /** 按题型给分：客观题 1 分，解答题 5 分。 */
    private int scoreOf(String questionType) {
        return OBJECTIVE_TYPES.contains(questionType) ? SCORE_OBJECTIVE : SCORE_ESSAY;
    }

    /**
     * 解答题 AI 判分：调用 EXAM/ANSWER_GRADING 能力（严格的面试官口径），返回 0~满分。
     *
     * @param row       题目快照
     * @param fullScore 本题满分
     * @return 判分结果
     */
    private AiGrade gradeEssay(KbExamPaperQuestionDO row, int fullScore) {
        String input = "题目：\n" + StrUtil.nullToEmpty(row.getTitle())
                + "\n\n参考答案：\n" + StrUtil.nullToEmpty(row.getAnswer())
                + "\n\n考生作答：\n" + StrUtil.nullToEmpty(row.getUserAnswer())
                + "\n\n本题满分：" + fullScore + " 分";
        try {
            String output = aiCapabilityService.invoke(AI_SCENE_EXAM, AI_CAPABILITY_GRADING, input);
            return parseGrade(output, fullScore);
        } catch (Exception e) {
            LoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "【考试】AI 判分失败 questionId={}: {}",
                    row.getQuestionId(), e.getMessage());
            return new AiGrade(0, "AI 判分失败，本题按 0 分计");
        }
    }

    /** 解析 AI 判分输出（优先严格 JSON，失败则从文本里取第一个数字兜底）。 */
    private AiGrade parseGrade(String output, int fullScore) {
        if (StrUtil.isBlank(output)) {
            return new AiGrade(0, "AI 未返回判分结果");
        }
        String text = output.trim().replace("```json", "").replace("```", "").trim();
        int score = 0;
        String comment = text;
        try {
            JSONObject json = JSONUtil.parseObj(text);
            score = json.getInt("score", 0);
            comment = json.getStr("comment", "");
        } catch (Exception ignore) {
            Matcher matcher = Pattern.compile("(\\d+)").matcher(text);
            if (matcher.find()) {
                score = Integer.parseInt(matcher.group(1));
            }
        }
        int safeScore = Math.max(0, Math.min(score, fullScore));
        return new AiGrade(safeScore, StrUtil.maxLength(StrUtil.nullToEmpty(comment), 200));
    }

    /**
     * AI 判分结果。
     */
    private record AiGrade(int score, String comment) {
    }

    /**
     * 回写单题判分结果（is_correct / actual_score / ai_comment）。
     *
     * @param row         题目行
     * @param isCorrect   是否正确（null=待判分）
     * @param actualScore 实际得分
     * @param aiComment   AI 评语（可空）
     */
    private void answerScoreRow(KbExamPaperQuestionDO row, Integer isCorrect, int actualScore, String aiComment) {
        KbExamPaperQuestionDO update = new KbExamPaperQuestionDO();
        update.setId(row.getId());
        update.setIsCorrect(isCorrect);
        update.setActualScore(actualScore);
        update.setAiComment(aiComment);
        kbExamPaperQuestionMapper.updateByCondition(update);
        row.setIsCorrect(isCorrect);
        row.setActualScore(actualScore);
        row.setAiComment(aiComment);
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
