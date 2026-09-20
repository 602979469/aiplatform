package com.jakt.aiplatform.core.model.param;

import lombok.Data;

/**
 * 组卷规则：某知识点抽多少道题（对应 kb_exam_template_rule）。
 */
@Data
public class KbExamRuleParam {

    /** 分类（知识点一级）。 */
    private String category;

    /** 子主题（知识点二级，空=该分类全部）。 */
    private String subtopic;

    /** 限定题型（空=不限）。 */
    private String questionType;

    /** 限定难度（空=不限）。 */
    private String difficulty;

    /** 抽题数量。 */
    private Integer count;
}
