package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbExamPaper;
import com.jakt.aiplatform.core.model.param.KbExamPaperQueryParam;

import java.util.List;

/**
 * 考试试卷管理类接口定义
 */
public interface KbExamPaperManager {

    /**
     * 创建考试试卷
     *
     * @param kbExamPaper 考试试卷
     * @return 创建成功后的考试试卷
     */
    KbExamPaper createKbExamPaper(KbExamPaper kbExamPaper);

    /**
     * 按主键查询考试试卷
     *
     * @param id 考试试卷主键
     * @return 考试试卷
     */
    KbExamPaper getKbExamPaper(Long id);

    /**
     * 分页查询考试试卷
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<KbExamPaper> pageKbExamPapers(KbExamPaperQueryParam query);

    /**
     * 列表查询考试试卷
     *
     * @param query 查询参数
     * @return 考试试卷列表
     */
    List<KbExamPaper> listKbExamPapers(KbExamPaperQueryParam query);

    /**
     * 更新考试试卷（全量）。
     *
     * @param kbExamPaper 考试试卷（含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateKbExamPaper(KbExamPaper kbExamPaper);

    /**
     * 按条件更新考试试卷（只更新传入的非空字段）。
     *
     * @param kbExamPaper 考试试卷（至少含主键）
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int updateByCondition(KbExamPaper kbExamPaper);

    /**
     * 删除考试试卷。
     *
     * @param id 考试试卷主键
     * @return 受影响行数；0 表示未生效，由上层决定
     */
    int deleteKbExamPaper(Long id);
}
