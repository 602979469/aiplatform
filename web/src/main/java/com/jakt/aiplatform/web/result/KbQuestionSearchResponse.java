package com.jakt.aiplatform.web.result;

import lombok.Data;

import java.util.List;

/**
 * 题库搜索响应。
 */
@Data
public class KbQuestionSearchResponse {

    /** 命中总数。 */
    private Long total;

    /** 结果列表。 */
    private List<Item> list;

    /** 单条结果。 */
    @Data
    public static class Item {

        /** 题目 ID。 */
        private String id;

        /** 标题。 */
        private String title;

        /** 摘要（含高亮）。 */
        private String snippet;

        /** 来源。 */
        private String category;

        /** 标签。 */
        private String tags;

        /** 难度。 */
        private String difficulty;

        /** 类型。 */
        private String docType;
    }
}
