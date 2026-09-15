package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 试卷模板知识点规则 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbExamTemplateRuleDO extends BaseDO {
    /** 主键。 */
    private Long id;

    /** 模板ID（kb_exam_template.id）。 */
    private Long templateId;

    /** 分类（知识点一级，如 并发编程）。 */
    private String category;

    /** 子主题（知识点二级，NULL=该分类下全部子主题）。 */
    private String subtopic;

    /** 限定题型（NULL=不限）。 */
    private String questionType;

    /** 限定难度（NULL=不限）。 */
    private String difficulty;

    /** 该知识点抽题数量。 */
    private Integer questionCount;

    /** 排序。 */
    private Integer orderNum;

}
