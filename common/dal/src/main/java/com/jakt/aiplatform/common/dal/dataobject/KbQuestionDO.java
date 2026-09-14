package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 题库 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbQuestionDO extends BaseDO {
    /** 主键。 */
    private Long id;

    /** 题目类型：qa解答题 / choice选择题。 */
    private String docType;

    /** 来源。 */
    private String category;

    /** 问题/题干。 */
    private String title;

    /** 解答/正文。 */
    private String content;

    /** 选择题选项(A-D)。 */
    private String options;

    /** 选择题答案。 */
    private String answer;

    /** 解析。 */
    private String explanation;

    /** 创建者。 */
    private String createBy;

    /** 更新者。 */
    private String updateBy;

    /** 难度。 */
    private String difficulty;

    /** tags。 */
    private String tags;

    /** source_path。 */
    private String sourcePath;

}
