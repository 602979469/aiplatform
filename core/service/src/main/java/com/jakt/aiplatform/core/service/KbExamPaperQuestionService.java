package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbExamPaperQuestion;
import com.jakt.aiplatform.core.model.param.KbExamPaperQuestionQueryParam;

import java.util.List;

/**
 * 试卷题目快照与作答领域服务
 *
 * 实现类为 KbExamPaperQuestionServiceImpl（core.service.impl 包）。
 */
public interface KbExamPaperQuestionService {

    /**
     * 创建试卷题目快照与作答
     *
     * @param kbExamPaperQuestion 试卷题目快照与作答
     * @return 创建后的试卷题目快照与作答（主键已回填）
     */
    KbExamPaperQuestion createKbExamPaperQuestion(KbExamPaperQuestion kbExamPaperQuestion);

    /**
     * 更新试卷题目快照与作答（全量）
     *
     * @param kbExamPaperQuestion 试卷题目快照与作答（含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateKbExamPaperQuestion(KbExamPaperQuestion kbExamPaperQuestion);

    /**
     * 按条件更新试卷题目快照与作答（只更新传入的非空字段）。
     *
     * @param kbExamPaperQuestion 试卷题目快照与作答（至少含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateByCondition(KbExamPaperQuestion kbExamPaperQuestion);

    /**
     * 删除试卷题目快照与作答
     *
     * @param id 试卷题目快照与作答主键
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteKbExamPaperQuestion(Long id);

    /**
     * 按主键获取试卷题目快照与作答
     *
     * @param id 试卷题目快照与作答主键
     * @return 试卷题目快照与作答
     */
    KbExamPaperQuestion getKbExamPaperQuestion(Long id);

    /**
     * 分页查询试卷题目快照与作答
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<KbExamPaperQuestion> findPage(KbExamPaperQuestionQueryParam query);

    /**
     * 列表查询试卷题目快照与作答
     *
     * @param query 查询参数
     * @return 试卷题目快照与作答列表
     */
    List<KbExamPaperQuestion> findList(KbExamPaperQuestionQueryParam query);
}
