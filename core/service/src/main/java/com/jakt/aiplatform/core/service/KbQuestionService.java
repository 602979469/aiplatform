package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbQuestion;
import com.jakt.aiplatform.core.model.dto.KbQuestionDetailView;
import com.jakt.aiplatform.core.model.dto.KbQuestionMetaView;
import com.jakt.aiplatform.core.model.dto.KbQuestionSearchView;
import com.jakt.aiplatform.core.model.param.KbQuestionQueryParam;
import com.jakt.aiplatform.core.model.param.KbQuestionSearchQuery;

import java.util.List;
import java.util.Map;

/**
 * 题库领域服务：管理端 CRUD、知识点元数据与检索（MySQL + Elasticsearch）。
 */
public interface KbQuestionService {

    /**
     * 分页查询题目（关键词 + 知识点/题型/难度筛选）。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<KbQuestion> page(KbQuestionQueryParam query);

    /**
     * 查询题目详情（不存在时抛参数异常）。
     *
     * @param id 题目ID
     * @return 题库题目领域模型
     */
    KbQuestion get(Long id);

    /**
     * 新增题目。
     *
     * @param question 题库题目
     * @return 新增后的主键
     */
    Long create(KbQuestion question);

    /**
     * 修改题目（全量字段）。
     *
     * @param question 题库题目（含主键）
     */
    void update(KbQuestion question);

    /**
     * 物理删除题目。
     *
     * @param id 题目ID
     */
    void delete(Long id);

    /**
     * 分类 / 子主题汇总（知识点题量），供筛选下拉使用。
     *
     * @return 知识点元数据
     */
    KbQuestionMetaView meta();

    /**
     * 全文检索题库。
     *
     * @param query 检索条件
     * @return 搜索结果
     */
    KbQuestionSearchView search(KbQuestionSearchQuery query);

    /**
     * 题库筛选项聚合（题型/分类/子主题/难度 + 数量）。
     *
     * @return 聚合结果
     */
    Map<String, List<KbQuestionSearchView.Bucket>> facets();

    /**
     * 查询题目详情视图（含完整解答，列表接口不返回大字段）。
     *
     * @param id 题目ID
     * @return 题目详情视图
     */
    KbQuestionDetailView detail(Long id);
}
