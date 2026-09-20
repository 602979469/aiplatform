package com.jakt.aiplatform.core.service.impl;


import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONObject;
import cn.hutool.json.JSONUtil;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.model.constant.KbExamConstant;
import com.jakt.aiplatform.core.model.domain.KbExamPaperQuestion;
import com.jakt.aiplatform.core.service.AiCapabilityService;
import com.jakt.aiplatform.core.service.KbExamGradingService;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Set;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

/**
 * 考试判分领域服务实现。
 */
@Service
public class KbExamGradingServiceImpl implements KbExamGradingService {

    /** 客观题类型（1 分/题）。 */
    private static final Set<String> OBJECTIVE_TYPES =
            Set.of(KbExamConstant.QUESTION_TYPE_SINGLE, KbExamConstant.QUESTION_TYPE_MULTI,
                    KbExamConstant.QUESTION_TYPE_JUDGE);

    /** AI 评语最大长度。 */
    private static final int COMMENT_MAX_LENGTH = 200;

    /** 数字兜底解析正则。 */
    private static final Pattern NUMBER_PATTERN = Pattern.compile("(\\d+)");

    /** AI 能力服务（解答题判分）。 */
    private final AiCapabilityService aiCapabilityService;

    public KbExamGradingServiceImpl(AiCapabilityService aiCapabilityService) {
        this.aiCapabilityService = aiCapabilityService;
    }

    @Override
    public boolean isObjective(String questionType) {
        return OBJECTIVE_TYPES.contains(questionType);
    }

    @Override
    public int scoreOf(String questionType) {
        return isObjective(questionType) ? KbExamConstant.SCORE_OBJECTIVE : KbExamConstant.SCORE_ESSAY;
    }

    @Override
    public String normalizeAnswer(String answer) {
        if (StrUtil.isBlank(answer)) {
            return StrUtil.EMPTY;
        }
        return Arrays.stream(answer.split(","))
                .map(String::trim)
                .filter(StrUtil::isNotEmpty)
                .map(String::toUpperCase)
                .distinct()
                .sorted()
                .collect(Collectors.joining(","));
    }

    @Override
    public AiGrade gradeEssay(KbExamPaperQuestion row, int fullScore) {
        String input = "题目：\n" + StrUtil.nullToEmpty(row.getTitle())
                + "\n\n参考答案：\n" + StrUtil.nullToEmpty(row.getAnswer())
                + "\n\n考生作答：\n" + StrUtil.nullToEmpty(row.getUserAnswer())
                + "\n\n本题满分：" + fullScore + " 分";
        try {
            String output = aiCapabilityService.invoke(KbExamConstant.AI_SCENE_EXAM,
                    KbExamConstant.AI_CAPABILITY_GRADING, input);
            return parseGrade(output, fullScore);
        } catch (Exception e) {
            LoggerUtil.warn(LogFileEnum.BIZ_SERVICE, "【考试】AI 判分失败 questionId={}: {}",
                    row.getQuestionId(), e.getMessage());
            return new AiGrade(0, "AI 判分失败，本题按 0 分计");
        }
    }

    /**
     * 解析 AI 判分输出（优先严格 JSON，失败则从文本里取第一个数字兜底）。
     *
     * @param output AI 原始输出
     * @param fullScore 本题满分
     * @return 判分结果
     */
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
            Matcher matcher = NUMBER_PATTERN.matcher(text);
            if (matcher.find()) {
                score = Integer.parseInt(matcher.group(1));
            }
        }
        int safeScore = Math.max(0, Math.min(score, fullScore));
        return new AiGrade(safeScore, StrUtil.maxLength(StrUtil.nullToEmpty(comment), COMMENT_MAX_LENGTH));
    }
}
