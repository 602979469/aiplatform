package com.jakt.aiplatform.biz.service;

/**
 * 题库检索（走 Elasticsearch）。
 */
public interface KbQuestionSearchManager {

    /**
     * 检索题库。
     *
     * @param keyword  关键词（可空，空则按最新返回）
     * @param pageNum  页码（从 1 开始）
     * @param pageSize 每页条数
     * @return 搜索结果
     */
    KbQuestionSearchView search(KbQuestionSearchQuery query);

    /**
     * 查询题目详情（含完整解答，列表接口不返回大字段）。
     *
     * @param id 题目 ID
     * @return 题目详情
     */
    KbQuestionDetailView detail(Long id);
}
