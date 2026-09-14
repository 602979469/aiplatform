package com.jakt.aiplatform.common.dal.query;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;
/**
 * 题库查询参数（common-dal 专用）：字段为数据库原始类型，仅供 Mapper/XML 使用。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbQuestionDalQuery extends DalPageQuery {

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

    /** 创建时间起。 */
    private LocalDateTime createTimeBegin;

    /** 创建时间止。 */
    private LocalDateTime createTimeEnd;

    /** 更新时间起。 */
    private LocalDateTime updateTimeBegin;

    /** 更新时间止。 */
    private LocalDateTime updateTimeEnd;

}
