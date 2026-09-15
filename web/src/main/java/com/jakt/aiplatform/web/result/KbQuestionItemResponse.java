package com.jakt.aiplatform.web.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 题库管理列表项（不含答案与解析，详情单独接口）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbQuestionItemResponse extends BaseResult {

    /** 题目ID。 */
    private Long id;

    /** 题型。 */
    private String questionType;

    /** 分类。 */
    private String category;

    /** 子主题。 */
    private String subtopic;

    /** 题干。 */
    private String title;

    /** 难度。 */
    private String difficulty;

    /** 标签（逗号分隔）。 */
    private String tags;

    /** 更新时间。 */
    private LocalDateTime updateTime;
}
