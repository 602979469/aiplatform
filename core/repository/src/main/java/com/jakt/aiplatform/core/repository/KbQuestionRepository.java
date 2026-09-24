package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbQuestion;
import com.jakt.aiplatform.core.model.dto.KbQuestionCategoryStat;
import com.jakt.aiplatform.core.model.dto.KbQuestionTypeStat;
import com.jakt.aiplatform.core.model.param.KbQuestionPickParam;
import com.jakt.aiplatform.core.model.param.KbQuestionQueryParam;

import java.util.List;

/**
 * 题库题目仓储：封装 Mapper，对外只暴露领域模型，不暴露 DO/DalQuery/DalResult。
 */
public interface KbQuestionRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 题库题目领域模型
     */
    KbQuestion findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<KbQuestion> findPage(KbQuestionQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 题库题目列表
     */
    List<KbQuestion> findList(KbQuestionQueryParam query);

    /**
     * 新增。
     *
     * @param kbQuestion 题库题目
     * @return 新增后的题库题目；主键已回填到入参，返回同一对象
     */
    KbQuestion insert(KbQuestion kbQuestion);

    /**
     * 更新（全量）。
     *
     * @param kbQuestion 题库题目
     * @return 受影响行数；0 表示未生效
     */
    int update(KbQuestion kbQuestion);

    /**
     * 按主键删除。
     *
     * @param id 主键
     * @return 受影响行数；0 表示未生效
     */
    int deleteById(Long id);

    /**
     * 按主键集合批量查询（组卷快照用）。
     *
     * @param ids 题目ID列表；为空返回空列表
     * @return 题库题目列表
     */
    List<KbQuestion> findByIds(List<Long> ids);

    /**
     * 组卷抽题：按知识点筛选 + 可选排除已掌握题目，随机返回指定数量的题目ID。
     *
     * @param param 抽题参数
     * @return 题目ID列表
     */
    List<Long> pickIds(KbQuestionPickParam param);

    /**
     * 组卷容量统计：知识点范围内各题型的可用题量（与 {@link #pickIds} 同一套筛选口径）。
     *
     * @param param 抽题参数（limit 字段不参与统计）
     * @return 题型题量统计列表
     */
    List<KbQuestionTypeStat> countPickByType(KbQuestionPickParam param);

    /**
     * 分类 / 子主题题量汇总（知识点下拉数据源）。
     *
     * @return 分类子主题题量统计列表
     */
    List<KbQuestionCategoryStat> findCategorySubtopicStats();
}
