package com.jakt.aiplatform.core.model.dto;

import lombok.Data;

/**
 * 题库分类/子主题题量统计（知识点下拉数据源）。
 */
@Data
public class KbQuestionCategoryStat {

    /** 分类（知识点一级）。 */
    private String category;

    /** 子主题（知识点二级）。 */
    private String subtopic;

    /** 该子主题题目总数。 */
    private long total;
}
