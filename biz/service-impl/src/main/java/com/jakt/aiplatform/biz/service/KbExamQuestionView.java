package com.jakt.aiplatform.biz.service;

import lombok.Data;

/**
 * 答题页题目视图（不含答案与解析）。
 */
@Data
public class KbExamQuestionView {

    /** 题号（从 1 开始）。 */
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

    /** 正文/补充内容。 */
    private String content;

    /** 选项 JSON 字符串。 */
    private String options;

    /** 本题分值。 */
    private Integer score;

    /** 已作答答案（续考回显）。 */
    private String userAnswer;
}
