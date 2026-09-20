package com.jakt.aiplatform.core.model.domain;

import com.jakt.aiplatform.common.framework.model.BaseModel;
import lombok.EqualsAndHashCode;
import lombok.Data;

/**
 * 题库题目领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbQuestion extends BaseModel {

    /** 主键。 */
    private Long id;

    /** 题型（单选/多选/判断/解答/qa/choice）。 */
    private String questionType;

    /** 分类（知识点一级）。 */
    private String category;

    /** 子主题（知识点二级）。 */
    private String subtopic;

    /** 题干。 */
    private String title;

    /** 正文/解答（支持 Markdown）。 */
    private String content;

    /** 选择题选项（JSON 字符串）。 */
    private String options;

    /** 选择题答案。 */
    private String answer;

    /** 解析。 */
    private String explanation;

    /** 创建者。 */
    private String createBy;

    /** 更新者。 */
    private String updateBy;

    /** 难度（easy/medium/hard）。 */
    private String difficulty;

    /** 标签（逗号分隔）。 */
    private String tags;

    /** 来源文件/出处。 */
    private String sourcePath;
}
