package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.KbExamPaperQuestion;

/**
 * 考试判分领域服务：客观题按答案比对，解答题走 AI 能力判分。
 */
public interface KbExamGradingService {

    /**
     * 是否客观题（单选/多选/判断）。
     *
     * @param questionType 题型
     * @return 是否客观题
     */
    boolean isObjective(String questionType);

    /**
     * 按题型给分：客观题 1 分，解答题 5 分。
     *
     * @param questionType 题型
     * @return 本题满分
     */
    int scoreOf(String questionType);

    /**
     * 答案归一化：去空格、转大写、去重排序（多选顺序无关）。
     *
     * @param answer 原始答案
     * @return 归一化后的答案
     */
    String normalizeAnswer(String answer);

    /**
     * 解答题 AI 判分：调用 EXAM/ANSWER_GRADING 能力（严格的面试官口径），返回 0~满分。
     *
     * @param row 题目快照
     * @param fullScore 本题满分
     * @return 判分结果
     */
    AiGrade gradeEssay(KbExamPaperQuestion row, int fullScore);

    /**
     * AI 判分结果。
     *
     * @param score 得分
     * @param comment 评语
     */
    record AiGrade(int score, String comment) {
    }
}
