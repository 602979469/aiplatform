package com.jakt.aiplatform.core.model.dto;

import lombok.Data;

import java.time.LocalDateTime;

/**
 * 题目详情视图（列表只回摘要，详情单独查询，避免列表传输大字段）。
 */
@Data
public class KbQuestionDetailView {

    /** 题目 ID。 */
    private Long id;

    /** 类型：qa / choice。 */
    private String docType;

    /** 来源。 */
    private String category;

    /** 子主题（知识点二级）。 */
    private String subtopic;

    /** 标题/问题。 */
    private String title;

    /** 解答正文（支持 Markdown）。 */
    private String content;

    /** 选择题选项（JSON 字符串）。 */
    private String options;

    /** 选择题答案。 */
    private String answer;

    /** 解析。 */
    private String explanation;

    /** 难度。 */
    private String difficulty;

    /** 标签。 */
    private String tags;

    /** 来源文件/出处。 */
    private String sourcePath;

    /** 创建时间。 */
    private LocalDateTime createTime;
}
