package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 题库新增/修改请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbQuestionSaveRequest extends BaseRequest {

    /** 题目ID（修改时必填，新增留空）。 */
    private Long id;

    /** 题型（单选/多选/判断/解答）。 */
    @NotBlank(message = "题型不能为空")
    @Size(max = 16, message = "题型长度不能超过 16")
    private String questionType;

    /** 分类（知识点一级）。 */
    @NotBlank(message = "分类不能为空")
    @Size(max = 64, message = "分类长度不能超过 64")
    private String category;

    /** 子主题（知识点二级）。 */
    @Size(max = 64, message = "子主题长度不能超过 64")
    private String subtopic;

    /** 题干。 */
    @NotBlank(message = "题干不能为空")
    @Size(max = 255, message = "题干长度不能超过 255")
    private String title;

    /** 正文/解答内容（Markdown）。 */
    private String content;

    /** 选项 JSON 字符串，如 [{"key":"A","text":"..."}]。 */
    private String options;

    /** 正确答案（单选/判断 A；多选 A,B；解答题填参考答案）。 */
    @Size(max = 512, message = "答案长度不能超过 512")
    private String answer;

    /** 解析。 */
    private String explanation;

    /** 难度（easy/medium/hard）。 */
    @Size(max = 16, message = "难度长度不能超过 16")
    private String difficulty;

    /** 标签（逗号分隔）。 */
    @Size(max = 128, message = "标签长度不能超过 128")
    private String tags;

    /** 来源路径。 */
    @Size(max = 512, message = "来源路径长度不能超过 512")
    private String sourcePath;
}
