package com.jakt.aiplatform.core.model.dto;

import lombok.Data;

import java.util.List;

/**
 * 题库知识点元数据：分类 → 子主题（含题量）。
 */
@Data
public class KbQuestionMetaView {

    /** 分类节点列表。 */
    private List<CategoryNode> categories;

    /**
     * 分类节点。
     */
    @Data
    public static class CategoryNode {

        /** 分类名称。 */
        private String category;

        /** 该分类题目总数。 */
        private long total;

        /** 子主题列表。 */
        private List<SubtopicNode> subtopics;
    }

    /**
     * 子主题节点。
     */
    @Data
    public static class SubtopicNode {

        /** 子主题名称。 */
        private String subtopic;

        /** 该子主题题目总数。 */
        private long total;
    }
}
