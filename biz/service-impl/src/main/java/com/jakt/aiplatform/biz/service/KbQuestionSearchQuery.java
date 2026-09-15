package com.jakt.aiplatform.biz.service;

import lombok.Data;

import java.util.List;

/**
 * 题库检索条件（关键词 + 题型/技术方向/知识点/难度过滤）。
 */
@Data
public class KbQuestionSearchQuery {

    /** 关键词。 */
    private String keyword;

    /** 题型（单选/多选/判断/解答）。 */
    private List<String> questionTypes;

    /** 技术方向。 */
    private List<String> categories;

    /** 知识点/子主题。 */
    private List<String> subtopics;

    /** 难度。 */
    private List<String> difficulties;

    /** 页码。 */
    private int pageNum = 1;

    /** 每页条数。 */
    private int pageSize = 10;
}
