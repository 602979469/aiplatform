package com.jakt.aiplatform.web.result;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 题库知识点元数据响应：分类 → 子主题（含题量）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class KbQuestionMetaResponse extends BaseResult {

    /** 分类节点。 */
    private List<CategoryNode> categories;

    /**
     * 分类节点。
     */
    @Data
    public static class CategoryNode {

        /** 分类名称。 */
        private String category;

        /** 题量。 */
        private long total;

        /** 子主题。 */
        private List<SubtopicNode> subtopics;
    }

    /**
     * 子主题节点。
     */
    @Data
    public static class SubtopicNode {

        /** 子主题名称。 */
        private String subtopic;

        /** 题量。 */
        private long total;
    }
}
