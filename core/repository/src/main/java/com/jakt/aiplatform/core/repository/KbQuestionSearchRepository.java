package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.dto.KbQuestionSearchView;
import com.jakt.aiplatform.core.model.param.KbQuestionSearchQuery;

import java.util.List;
import java.util.Map;

/**
 * 题库检索仓储：封装 Elasticsearch 检索，对外只暴露领域模型，不暴露 ES 客户端与原始响应。
 */
public interface KbQuestionSearchRepository {

    /**
     * 全文检索题库。
     *
     * @param query 检索条件
     * @return 搜索结果（含命中总数、当前页数据、聚合结果）
     */
    KbQuestionSearchView search(KbQuestionSearchQuery query);

    /**
     * 题库筛选项聚合（题型/分类/子主题/难度 + 数量），无需先查询。
     *
     * @return 聚合结果
     */
    Map<String, List<KbQuestionSearchView.Bucket>> facets();
}
