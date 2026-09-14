package com.jakt.aiplatform.biz.service;

import lombok.Data;

import java.util.List;

/**
 * 题库搜索结果视图。
 */
@Data
public class KbQuestionSearchView {

    /** 命中总数。 */
    private Long total;

    /** 当前页数据。 */
    private List<Item> list;

    /** 单条结果。 */
    @Data
    public static class Item {

        /** 题目 ID。 */
        private String id;

        /** 标题/问题。 */
        private String title;

        /** 内容摘要（高亮片段或截断正文）。 */
        private String snippet;

        /** 来源。 */
        private String category;

        /** 标签。 */
        private String tags;

        /** 难度。 */
        private String difficulty;

        /** 类型：qa / choice。 */
        private String docType;
    }
}
