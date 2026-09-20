package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbExamPaperQuestion;
import com.jakt.aiplatform.core.model.param.KbExamPaperQuestionQueryParam;

import java.util.List;

/**
 * 试卷题目快照与作答仓储：封装 Mapper，对外只暴露领域模型，不暴露 DO/DalQuery/DalResult。
 */
public interface KbExamPaperQuestionRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 试卷题目快照与作答领域模型
     */
    KbExamPaperQuestion findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<KbExamPaperQuestion> findPage(KbExamPaperQuestionQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 试卷题目快照与作答列表
     */
    List<KbExamPaperQuestion> findList(KbExamPaperQuestionQueryParam query);

    /**
     * 按条件查询单条：基于 {@code findList} 的结果集判断，不新增 Mapper 方法。
     *
     * @param query 查询参数
     * @return 试卷题目快照与作答领域模型；未查询到返回 null，多条由 Mapper selectOne 抛 TooManyResultsException
     */
    KbExamPaperQuestion findOne(KbExamPaperQuestionQueryParam query);

    /**
     * 新增。
     *
     * @param kbExamPaperQuestion 试卷题目快照与作答
     * @return 新增后的试卷题目快照与作答；主键已回填到入参，返回同一对象
     */
    KbExamPaperQuestion insert(KbExamPaperQuestion kbExamPaperQuestion);

    /**
     * 更新（全量）。
     *
     * @param kbExamPaperQuestion 试卷题目快照与作答（含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int update(KbExamPaperQuestion kbExamPaperQuestion);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param kbExamPaperQuestion 试卷题目快照与作答（至少含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateByCondition(KbExamPaperQuestion kbExamPaperQuestion);

    /**
     * 按主键删除。
     *
     * @param id 主键
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteById(Long id);

    /**
     * 按试卷ID查询全部答题明细（按题号正序）。
     *
     * @param paperId 试卷ID
     * @return 试卷题目快照与作答列表
     */
    List<KbExamPaperQuestion> findByPaperId(Long paperId);

    /**
     * 按试卷ID删除全部答题明细（删除考试记录时使用）。
     *
     * @param paperId 试卷ID
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteByPaperId(Long paperId);
}
