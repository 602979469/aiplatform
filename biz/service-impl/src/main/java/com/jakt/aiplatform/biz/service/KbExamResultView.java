package com.jakt.aiplatform.biz.service;

import lombok.Data;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 成绩视图（交卷 / 成绩详情）。
 */
@Data
public class KbExamResultView {

    /** 试卷ID。 */
    private Long paperId;

    /** 标题。 */
    private String title;

    /** 得分。 */
    private Integer score;

    /** 总分。 */
    private Integer totalScore;

    /** 答对题数。 */
    private Integer correctCount;

    /** 答错题数。 */
    private Integer wrongCount;

    /** 未作答题数。 */
    private Integer unansweredCount;

    /** 实际用时（秒）。 */
    private Integer costSeconds;

    /** 交卷时间。 */
    private LocalDateTime submitTime;

    /** 逐题结果。 */
    private List<Item> questions;

    /**
     * 逐题结果。
     */
    @Data
    public static class Item {

        /** 题号。 */
        private Integer seq;

        /** 来源题目ID。 */
        private Long questionId;

        /** 题型。 */
        private String questionType;

        /** 分类。 */
        private String category;

        /** 子主题。 */
        private String subtopic;

        /** 题干。 */
        private String title;

        /** 正文（Markdown，题目补充说明）。 */
        private String content;

        /** 选项 JSON 字符串。 */
        private String options;

        /** 我的答案。 */
        private String userAnswer;

        /** 正确答案。 */
        private String answer;

        /** 解析。 */
        private String explanation;

        /** 是否正确（null=未判，如解答题）。 */
        private Integer isCorrect;

        /** 本题满分。 */
        private Integer score;

        /** 本题实际得分（客观题判分 / AI 判分）。 */
        private Integer actualScore;

        /** AI 判分评语（解答题）。 */
        private String aiComment;
    }
}
