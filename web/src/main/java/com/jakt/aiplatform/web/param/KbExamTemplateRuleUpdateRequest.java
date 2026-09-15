package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;

import com.jakt.aiplatform.web.param.BaseRequest;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 更新试卷模板知识点规则请求 DTO。
 *
 * <p>校验规则与 kb_exam_template_rule 表字段对齐：非空 + varchar 长度，不做业务自定义规则。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbExamTemplateRuleUpdateRequest extends BaseRequest {
    /** 模板ID（kb_exam_template.id）。 */
    @NotNull(message = "模板ID（kb_exam_template.id）不能为空")
    private Long templateId;

    /** 分类（知识点一级，如 并发编程）。 */
    @NotBlank(message = "分类（知识点一级，如 并发编程）不能为空")
    @Size(max = 64, message = "分类（知识点一级，如 并发编程）长度不能超过 64")
    private String category;

    /** 子主题（知识点二级，NULL=该分类下全部子主题）。 */
    @Size(max = 64, message = "子主题（知识点二级，NULL=该分类下全部子主题）长度不能超过 64")
    private String subtopic;

    /** 限定题型（NULL=不限）。 */
    @Size(max = 16, message = "限定题型（NULL=不限）长度不能超过 16")
    private String questionType;

    /** 限定难度（NULL=不限）。 */
    @Size(max = 16, message = "限定难度（NULL=不限）长度不能超过 16")
    private String difficulty;

    /** 该知识点抽题数量。 */
    @NotNull(message = "该知识点抽题数量不能为空")
    private Integer questionCount;

    /** 排序。 */
    private Integer orderNum;

}
