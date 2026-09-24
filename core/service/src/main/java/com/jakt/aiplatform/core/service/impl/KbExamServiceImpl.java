package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.collection.CollUtil;

import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.template.BizTemplate;
import com.jakt.aiplatform.common.framework.template.TransactionTemplate;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.model.constant.KbExamConstant;
import com.jakt.aiplatform.core.model.domain.KbExamPaper;
import com.jakt.aiplatform.core.model.domain.KbExamPaperQuestion;
import com.jakt.aiplatform.core.model.domain.KbExamTemplate;
import com.jakt.aiplatform.core.model.domain.KbQuestion;
import com.jakt.aiplatform.core.model.dto.KbExamPaperView;
import com.jakt.aiplatform.core.model.dto.KbExamQuestionView;
import com.jakt.aiplatform.core.model.dto.KbExamResultView;
import com.jakt.aiplatform.core.model.param.KbExamPaperQueryParam;
import com.jakt.aiplatform.core.model.param.KbExamPaperQuestionQueryParam;
import com.jakt.aiplatform.core.model.param.KbExamRuleParam;
import com.jakt.aiplatform.core.model.param.KbExamStartParam;
import com.jakt.aiplatform.core.model.param.KbUserQuestionStatDelta;
import com.jakt.aiplatform.core.repository.KbExamPaperQuestionRepository;
import com.jakt.aiplatform.core.repository.KbExamPaperRepository;
import com.jakt.aiplatform.core.repository.KbExamTemplateRepository;
import com.jakt.aiplatform.core.repository.KbQuestionRepository;
import com.jakt.aiplatform.core.repository.KbUserQuestionStatRepository;
import com.jakt.aiplatform.core.service.KbExamGradingService;
import com.jakt.aiplatform.core.service.KbExamService;
import com.jakt.aiplatform.core.service.KbExamRuleService;
import org.springframework.stereotype.Service;

import java.time.Duration;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 考试试卷领域服务实现：组卷（排除已掌握题目）→ 答题落库 → 交卷判分 → 回写错题集与掌握度。
 */
@Service
public class KbExamServiceImpl implements KbExamService {

    /** 选择题型集合（单选 + 多选）。 */
    private static final List<String> GROUP_SELECT =
            List.of(KbExamConstant.QUESTION_TYPE_SINGLE, KbExamConstant.QUESTION_TYPE_MULTI);

    /** 问答题型集合（当前题库对应"判断"，后续新增问答题类型时在此扩展）。 */
    private static final List<String> GROUP_QA = List.of(KbExamConstant.QUESTION_TYPE_JUDGE);

    /** 解答题型集合。 */
    private static final List<String> GROUP_ESSAY = List.of(KbExamConstant.QUESTION_TYPE_ESSAY);

    /** 超时兜底扫描的进行中试卷条数。 */
    private static final int EXPIRED_SCAN_LIMIT = 50;

    /** 试卷仓储。 */
    private final KbExamPaperRepository kbExamPaperRepository;

    /** 试卷答题明细仓储。 */
    private final KbExamPaperQuestionRepository kbExamPaperQuestionRepository;

    /** 试卷模板仓储。 */
    private final KbExamTemplateRepository kbExamTemplateRepository;

    /** 题库仓储。 */
    private final KbQuestionRepository kbQuestionRepository;

    /** 掌握度仓储。 */
    private final KbUserQuestionStatRepository kbUserQuestionStatRepository;

    /** 组卷规则服务。 */
    private final KbExamRuleService kbExamRuleService;

    /** 判分服务。 */
    private final KbExamGradingService kbExamGradingService;

    /** 事务模板。 */
    private final TransactionTemplate transactionTemplate;

    public KbExamServiceImpl(KbExamPaperRepository kbExamPaperRepository,
                                  KbExamPaperQuestionRepository kbExamPaperQuestionRepository,
                                  KbExamTemplateRepository kbExamTemplateRepository,
                                  KbQuestionRepository kbQuestionRepository,
                                  KbUserQuestionStatRepository kbUserQuestionStatRepository,
                                  KbExamRuleService kbExamRuleService,
                                  KbExamGradingService kbExamGradingService,
                                  TransactionTemplate transactionTemplate) {
        this.kbExamPaperRepository = kbExamPaperRepository;
        this.kbExamPaperQuestionRepository = kbExamPaperQuestionRepository;
        this.kbExamTemplateRepository = kbExamTemplateRepository;
        this.kbQuestionRepository = kbQuestionRepository;
        this.kbUserQuestionStatRepository = kbUserQuestionStatRepository;
        this.kbExamRuleService = kbExamRuleService;
        this.kbExamGradingService = kbExamGradingService;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public KbExamPaperView start(KbExamStartParam param) {
        Long userId = param.getUserId();
        AssertUtil.throwErrWhenNull(userId, ErrorCodeEnum.PARAM_INVALID, "缺少答题用户");

        KbExamTemplate template = null;
        if (ObjectUtil.isNotNull(param.getTemplateId())) {
            template = kbExamTemplateRepository.findById(param.getTemplateId());
            AssertUtil.throwErrWhenNull(template, ErrorCodeEnum.PARAM_INVALID, "试卷模板不存在");
        }

        List<KbExamRuleParam> rules = kbExamRuleService.resolveRules(param);
        String mode = StrUtil.blankToDefault(param.getMode(), ObjectUtil.isNull(template)
                ? KbExamConstant.MODE_NORMAL
                : StrUtil.blankToDefault(template.getMode(), KbExamConstant.MODE_NORMAL));
        int perQuestionSeconds = firstNonNull(param.getPerQuestionSeconds(),
                ObjectUtil.isNotNull(template) && ObjectUtil.isNotNull(template.getPerQuestionSeconds())
                        ? template.getPerQuestionSeconds() : KbExamConstant.DEFAULT_PER_QUESTION_SECONDS);
        boolean excludeMastered = ObjectUtil.isNotNull(param.getExcludeMastered())
                ? param.getExcludeMastered() == 1
                : ObjectUtil.isNull(template) || ObjectUtil.isNull(template.getExcludeMastered()) || template.getExcludeMastered() == 1;
        int questionCount = kbExamRuleService.resolveQuestionCount(param, rules);

        // 题型配额：优先用模板的 typeMix，没配则回退固定配比 5:2:1；
        // 再按所选知识点实际可用的题型收敛，避免"知识点没有解答题却硬留解答题配额"
        int[] desiredQuotas = kbExamRuleService.resolveTypeQuotas(param, questionCount);
        Map<String, Integer> availableByType = kbExamRuleService.countAvailableByType(rules, userId, excludeMastered);
        int[] quotas = kbExamRuleService.allocateTypeQuotas(desiredQuotas, availableByType, questionCount);
        List<Long> picked = new ArrayList<>();
        picked.addAll(kbExamRuleService.pickByGroup(GROUP_SELECT, quotas[0], rules, userId, excludeMastered, picked));
        picked.addAll(kbExamRuleService.pickByGroup(GROUP_QA, quotas[1], rules, userId, excludeMastered, picked));
        picked.addAll(kbExamRuleService.pickByGroup(GROUP_ESSAY, quotas[2], rules, userId, excludeMastered, picked));

        // 题型配额没抽满时继续补题，但只在所选知识点范围内补（不限题型），绝不跨知识点抽题
        if (picked.size() < questionCount) {
            picked.addAll(kbExamRuleService.pickWithinRules(rules, userId, excludeMastered,
                    questionCount - picked.size(), picked));
        }
        // 题量仍不足：明确失败，不再静默拿其它知识点的题凑成一张混科卷
        assertEnoughQuestions(rules, excludeMastered, questionCount, picked.size(), availableByType);

        Map<Long, KbQuestion> questionMap = new LinkedHashMap<>();
        for (KbQuestion row : kbQuestionRepository.findByIds(picked)) {
            questionMap.put(row.getId(), row);
        }
        List<KbQuestion> questions = picked.stream()
                .map(questionMap::get)
                .filter(ObjectUtil::isNotNull)
                .collect(Collectors.toList());
        int count = questions.size();
        int totalScore = 0;
        for (KbQuestion question : questions) {
            totalScore += kbExamGradingService.scoreOf(question.getQuestionType());
        }

        LocalDateTime now = LocalDateTime.now();
        KbExamPaper paper = new KbExamPaper();
        paper.setUserId(userId);
        paper.setTitle(StrUtil.blankToDefault(param.getTitle(), buildTitle(rules)));
        paper.setMode(mode);
        paper.setStatus(KbExamConstant.PAPER_STATUS_IN_PROGRESS);
        paper.setPerQuestionSeconds(perQuestionSeconds);
        paper.setTimeLimitSeconds(count * perQuestionSeconds);
        paper.setQuestionCount(count);
        paper.setTotalScore(totalScore);
        paper.setStartTime(now);
        paper.setDeadline(now.plusSeconds((long) count * perQuestionSeconds));

        List<KbExamPaperQuestion> paperQuestions = new ArrayList<>();
        for (int i = 0; i < count; i++) {
            KbQuestion question = questions.get(i);
            KbExamPaperQuestion row = new KbExamPaperQuestion();
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
            row.setScore(kbExamGradingService.scoreOf(question.getQuestionType()));
            paperQuestions.add(row);
        }

        BizTemplate.executeWithoutResult(transactionTemplate, () -> {
            kbExamPaperRepository.insert(paper);
            for (KbExamPaperQuestion row : paperQuestions) {
                row.setPaperId(paper.getId());
                kbExamPaperQuestionRepository.insert(row);
            }
        });
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "组卷成功 paperId={} userId={} 题量={} 模式={}",
                paper.getId(), userId, count, mode);

        if (ObjectUtil.isNotNull(template)) {
            KbExamTemplate templateUpdate = new KbExamTemplate();
            templateUpdate.setId(template.getId());
            templateUpdate.setUseCount((ObjectUtil.isNull(template.getUseCount()) ? 0 : template.getUseCount()) + 1);
            kbExamTemplateRepository.updateByCondition(templateUpdate);
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
        KbExamPaper paper = requirePaper(paperId, userId);
        List<KbExamPaperQuestion> rows = listQuestions(paperId);
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
        view.setRemainingSeconds(KbExamConstant.PAPER_STATUS_IN_PROGRESS.equals(paper.getStatus())
                ? remainingSeconds(paper.getDeadline()) : 0L);
        view.setQuestions(rows.stream().map(this::toQuestionView).collect(Collectors.toList()));
        return view;
    }

    @Override
    public void answer(Long paperId, Long userId, Integer seq, String userAnswer, Integer costSeconds) {
        KbExamPaper paper = requirePaper(paperId, userId);
        AssertUtil.throwErrWhenFalse(KbExamConstant.PAPER_STATUS_IN_PROGRESS.equals(paper.getStatus()),
                ErrorCodeEnum.PARAM_INVALID, "试卷已交卷，不能继续作答");

        KbExamPaperQuestionQueryParam query = new KbExamPaperQuestionQueryParam();
        query.setPaperId(paperId);
        query.setSeq(seq);
        KbExamPaperQuestion row = kbExamPaperQuestionRepository.findOne(query);
        AssertUtil.throwErrWhenNull(row, ErrorCodeEnum.PARAM_INVALID, "题号不存在: " + seq);

        KbExamPaperQuestion update = new KbExamPaperQuestion();
        update.setId(row.getId());
        update.setUserAnswer(userAnswer);
        update.setAnswerCostSeconds(costSeconds);
        update.setAnswerTime(LocalDateTime.now());
        kbExamPaperQuestionRepository.updateByCondition(update);
    }

    @Override
    public KbExamResultView submit(Long paperId, Long userId) {
        return submitInternal(paperId, userId, true);
    }

    @Override
    public KbExamResultView result(Long paperId, Long userId) {
        KbExamPaper paper = requirePaper(paperId, userId);
        List<KbExamPaperQuestion> rows = listQuestions(paperId);
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
        // TODO 后续改为定时任务统一处理：这里在查询列表时兜底把「已超时但仍在进行中」的试卷自动交卷
        autoSubmitExpired(userId);
        KbExamPaperQueryParam query = new KbExamPaperQueryParam();
        query.setUserId(userId);
        query.setPageNum(ObjectUtil.defaultIfNull(pageNum, 1));
        query.setPageSize(ObjectUtil.defaultIfNull(pageSize, 10));
        return kbExamPaperRepository.findPage(query);
    }

    @Override
    public void deletePaper(Long paperId, Long userId) {
        requirePaper(paperId, userId);
        BizTemplate.executeWithoutResult(transactionTemplate, () -> {
            kbExamPaperQuestionRepository.deleteByPaperId(paperId);
            kbExamPaperRepository.deleteById(paperId);
        });
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除考试记录 paperId={} userId={}", paperId, userId);
    }

    /**
     * 交卷判分。
     *
     * @param paperId 试卷ID
     * @param userId 用户ID
     * @param aiGradeEssay 是否对解答题调用 AI 判分；列表超时兜底时为 false（避免拖慢查询）
     * @return 成绩视图
     */
    private KbExamResultView submitInternal(Long paperId, Long userId, boolean aiGradeEssay) {
        KbExamPaper paper = requirePaper(paperId, userId);
        if (!KbExamConstant.PAPER_STATUS_IN_PROGRESS.equals(paper.getStatus())) {
            return result(paperId, userId);
        }
        List<KbExamPaperQuestion> rows = listQuestions(paperId);
        int correct = 0;
        int wrong = 0;
        int unanswered = 0;
        int score = 0;
        for (KbExamPaperQuestion row : rows) {
            Integer isCorrect = null;
            int fullScore = ObjectUtil.isNull(row.getScore())
                    ? kbExamGradingService.scoreOf(row.getQuestionType()) : row.getScore();
            int actualScore = 0;
            String aiComment = null;
            boolean answered = !StrUtil.isBlank(row.getUserAnswer());
            if (!answered) {
                // 未作答：只记 0 分与未答数，不计入错题集/掌握度（没作答不算错题）
                isCorrect = 0;
                unanswered++;
            } else if (kbExamGradingService.isObjective(row.getQuestionType())) {
                String userAnswer = kbExamGradingService.normalizeAnswer(row.getUserAnswer());
                String rightAnswer = kbExamGradingService.normalizeAnswer(row.getAnswer());
                if (userAnswer.equals(rightAnswer)) {
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
                KbExamGradingService.AiGrade grade = kbExamGradingService.gradeEssay(row, fullScore);
                actualScore = grade.score();
                aiComment = grade.comment();
                isCorrect = actualScore * 2 >= fullScore ? 1 : 0;
                if (isCorrect == 1) {
                    correct++;
                } else {
                    wrong++;
                }
            }
            score += actualScore;
            answerScoreRow(row, isCorrect, actualScore, aiComment);
            // 只有真正作答过的题才回写掌握度与错题集
            if (answered) {
                writeStat(userId, row.getQuestionId(), ObjectUtil.isNotNull(isCorrect) && isCorrect == 1);
            }
        }

        LocalDateTime now = LocalDateTime.now();
        int cost = (int) Duration.between(paper.getStartTime(), now).getSeconds();
        KbExamPaper updatePaper = new KbExamPaper();
        updatePaper.setId(paperId);
        updatePaper.setStatus(KbExamConstant.PAPER_STATUS_GRADED);
        updatePaper.setScore(score);
        updatePaper.setCorrectCount(correct);
        updatePaper.setWrongCount(wrong);
        updatePaper.setUnansweredCount(unanswered);
        updatePaper.setSubmitTime(now);
        updatePaper.setCostSeconds(cost);
        kbExamPaperRepository.updateByCondition(updatePaper);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "交卷成功 paperId={} userId={} 得分={} 对/错/未答={}/{}/{}",
                paperId, userId, score, correct, wrong, unanswered);
        return result(paperId, userId);
    }

    /**
     * 超时兜底：进行中且已过 deadline 的试卷按已作答内容自动交卷。
     *
     * @param userId 用户ID
     */
    private void autoSubmitExpired(Long userId) {
        KbExamPaperQueryParam query = new KbExamPaperQueryParam();
        query.setUserId(userId);
        query.setStatus(KbExamConstant.PAPER_STATUS_IN_PROGRESS);
        query.setPageNum(1);
        query.setPageSize(EXPIRED_SCAN_LIMIT);
        List<KbExamPaper> papers = kbExamPaperRepository.findList(query);
        if (CollUtil.isEmpty(papers)) {
            return;
        }
        LocalDateTime now = LocalDateTime.now();
        for (KbExamPaper paper : papers) {
            if (ObjectUtil.isNotNull(paper.getDeadline()) && paper.getDeadline().isBefore(now)) {
                LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "考试超时自动交卷 paperId={} userId={}", paper.getId(), userId);
                // 列表兜底不调 AI 判分（避免拖慢查询），解答题置为待判分
                submitInternal(paper.getId(), userId, false);
            }
        }
    }

    /**
     * 校验试卷归属，不存在或非本人试卷直接抛参数异常。
     *
     * @param paperId 试卷ID
     * @param userId 用户ID
     * @return 试卷领域模型
     */
    private KbExamPaper requirePaper(Long paperId, Long userId) {
        KbExamPaper paper = kbExamPaperRepository.findById(paperId);
        AssertUtil.throwErrWhenTrue(ObjectUtil.isNull(paper) || !ObjectUtil.equal(paper.getUserId(), userId),
                ErrorCodeEnum.PARAM_INVALID, "试卷不存在或无权访问");
        return paper;
    }

    /**
     * 查询试卷全部答题明细（按题号正序）。
     *
     * @param paperId 试卷ID
     * @return 答题明细列表
     */
    private List<KbExamPaperQuestion> listQuestions(Long paperId) {
        List<KbExamPaperQuestion> rows = new ArrayList<>(kbExamPaperQuestionRepository.findByPaperId(paperId));
        rows.sort((a, b) -> Integer.compare(a.getSeq(), b.getSeq()));
        return rows;
    }

    /**
     * 校验题量是否凑得齐：不足时给出可执行的失败原因，避免静默跨知识点补题。
     *
     * @param rules 知识点规则
     * @param excludeMastered 是否排除了已做对的题目
     * @param questionCount 目标题量
     * @param pickedSize 实际抽到的题量
     * @param availableByType 知识点范围内的题型容量
     */
    private void assertEnoughQuestions(List<KbExamRuleParam> rules, boolean excludeMastered,
                                       int questionCount, int pickedSize,
                                       Map<String, Integer> availableByType) {
        if (pickedSize >= questionCount) {
            return;
        }
        String scope = describeScope(rules);
        int available = availableByType.values().stream().mapToInt(Integer::intValue).sum();
        if (available <= 0) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID,
                    "知识点「" + scope + "」下暂无题目，请重新配置试卷");
        }
        String hint = excludeMastered ? "，可减少题量或关闭「排除已做对」" : "，请减少题量或补充题库";
        throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID,
                "知识点「" + scope + "」可用题目不足：需要 " + questionCount + " 题，实际只有 "
                        + available + " 题" + hint);
    }

    /**
     * 描述知识点范围，多知识点用顿号连接，用于失败提示。
     *
     * @param rules 知识点规则
     * @return 知识点描述
     */
    private String describeScope(List<KbExamRuleParam> rules) {
        if (CollUtil.isEmpty(rules)) {
            return "未指定";
        }
        return rules.stream()
                .map(rule -> StrUtil.isBlank(rule.getSubtopic())
                        ? StrUtil.blankToDefault(rule.getCategory(), "未指定")
                        : rule.getCategory() + "/" + rule.getSubtopic())
                .distinct()
                .collect(Collectors.joining("、"));
    }

    /**
     * 回写单题判分结果（is_correct / actual_score / ai_comment）。
     *
     * @param row 题目行
     * @param isCorrect 是否正确（null=待判分）
     * @param actualScore 实际得分
     * @param aiComment AI 评语（可空）
     */
    private void answerScoreRow(KbExamPaperQuestion row, Integer isCorrect, int actualScore, String aiComment) {
        KbExamPaperQuestion update = new KbExamPaperQuestion();
        update.setId(row.getId());
        update.setIsCorrect(isCorrect);
        update.setActualScore(actualScore);
        update.setAiComment(aiComment);
        kbExamPaperQuestionRepository.updateByCondition(update);
        row.setIsCorrect(isCorrect);
        row.setActualScore(actualScore);
        row.setAiComment(aiComment);
    }

    /**
     * 掌握度回写：答对 → 已掌握并移出错题集；答错 → 进错题集。
     *
     * @param userId 用户ID
     * @param questionId 题目ID
     * @param correct 是否答对
     */
    private void writeStat(Long userId, Long questionId, boolean correct) {
        KbUserQuestionStatDelta delta = new KbUserQuestionStatDelta();
        delta.setUserId(userId);
        delta.setQuestionId(questionId);
        delta.setRightDelta(correct ? 1 : 0);
        delta.setWrongDelta(correct ? 0 : 1);
        delta.setLastResult(correct ? 1 : 0);
        delta.setMastered(correct ? 1 : 0);
        delta.setInWrongBook(correct ? 0 : 1);
        kbUserQuestionStatRepository.upsertStat(delta);
    }

    /**
     * 答题明细 → 答题页题目视图。
     *
     * @param row 答题明细
     * @return 题目视图
     */
    private KbExamQuestionView toQuestionView(KbExamPaperQuestion row) {
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

    /**
     * 按知识点生成卷名。
     *
     * @param rules 知识点规则
     * @return 卷名
     */
    private String buildTitle(List<KbExamRuleParam> rules) {
        String scope = rules.stream()
                .map(KbExamRuleParam::getCategory)
                .filter(StrUtil::isNotBlank)
                .distinct()
                .limit(3)
                .collect(Collectors.joining("、"));
        return StrUtil.isBlank(scope) ? "模拟考试" : scope + " 专项练习";
    }

    /**
     * 剩余秒数（已超时返回 0）。
     *
     * @param deadline 截止时间
     * @return 剩余秒数
     */
    private long remainingSeconds(LocalDateTime deadline) {
        if (ObjectUtil.isNull(deadline)) {
            return 0L;
        }
        long seconds = Duration.between(LocalDateTime.now(), deadline).getSeconds();
        return Math.max(seconds, 0L);
    }

    /**
     * 取首个有效正整数，否则用默认值。
     *
     * @param value 待判定值
     * @param defaultValue 默认值
     * @return 有效值
     */
    private int firstNonNull(Integer value, int defaultValue) {
        return ObjectUtil.isNull(value) || value <= 0 ? defaultValue : value;
    }

}
