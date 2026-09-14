package com.jakt.aiplatform.web.result;

import lombok.Data;

/**
 * 题目详情响应（含完整解答）。
 */
@Data
public class KbQuestionDetailResponse {

    /** 题目 ID。 */
    private Long id;

    /** 类型。 */
    private String docType;

    /** 来源。 */
    private String category;

    /** 标题/问题。 */
    private String title;

    /** 解答正文（Markdown）。 */
    private String content;

    /** 选项（JSON）。 */
    private String options;

    /** 答案。 */
    private String answer;

    /** 解析。 */
    private String explanation;

    /** 难度。 */
    private String difficulty;

    /** 标签。 */
    private String tags;

    /** 出处。 */
    private String sourcePath;
}
