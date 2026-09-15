package com.jakt.aiplatform.biz.service;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 错题集条目。
 */
@Data
public class KbExamWrongView {

    /** 题目ID。 */
    private Long questionId;

    /** 题干。 */
    private String title;

    /** 分类。 */
    private String category;

    /** 子主题。 */
    private String subtopic;

    /** 题型。 */
    private String questionType;

    /** 正确答案。 */
    private String answer;

    /** 我的答案（最近一次）。 */
    private String userAnswer;

    /** 解析。 */
    private String explanation;

    /** 答错次数。 */
    private Integer wrongCount;

    /** 最近作答时间。 */
    private LocalDateTime lastAnswerTime;
}
