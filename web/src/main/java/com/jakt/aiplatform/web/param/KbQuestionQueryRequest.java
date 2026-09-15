package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 题库管理分页查询请求：关键词 + 知识点/题型/难度筛选。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbQuestionQueryRequest extends PageQueryRequest {

    /** 关键词（题干/正文模糊匹配）。 */
    private String keyword;

    /** 分类（知识点一级）。 */
    private String category;

    /** 子主题（知识点二级）。 */
    private String subtopic;

    /** 题型（单选/多选/判断/解答）。 */
    private String questionType;

    /** 难度（easy/medium/hard）。 */
    private String difficulty;
}
