package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbExamPaper;

/**
 * 考题系统用例编排：组卷、续考、答题、交卷判分、成绩、历史记录、错题集。
 */
public interface KbExamManager {

    /**
     * 开始考试（选模板或快速创建），返回试卷（不含答案）。
     *
     * @param param 组卷入参
     * @return 试卷视图
     */
    KbExamPaperView start(KbExamStartParam param);

    /**
     * 续考：按试卷ID取回题目与已作答内容（不含答案）。
     *
     * @param paperId 试卷ID
     * @param userId  用户ID
     * @return 试卷视图
     */
    KbExamPaperView getPaper(Long paperId, Long userId);

    /**
     * 提交单题作答（幂等，按题号覆盖）。
     *
     * @param paperId     试卷ID
     * @param userId      用户ID
     * @param seq         题号
     * @param userAnswer  作答内容
     * @param costSeconds 本题耗时（秒，可空）
     */
    void answer(Long paperId, Long userId, Integer seq, String userAnswer, Integer costSeconds);

    /**
     * 交卷判分（超时自动交卷也走这里；幂等：已判分的卷子直接返回成绩）。
     *
     * @param paperId 试卷ID
     * @param userId  用户ID
     * @return 成绩视图
     */
    KbExamResultView submit(Long paperId, Long userId);

    /**
     * 成绩详情。
     *
     * @param paperId 试卷ID
     * @param userId  用户ID
     * @return 成绩视图
     */
    KbExamResultView result(Long paperId, Long userId);

    /**
     * 考试记录（按时间倒序）。
     *
     * @param userId   用户ID
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<KbExamPaper> history(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 错题集。
     *
     * @param userId   用户ID
     * @param category 分类筛选（可空）
     * @param pageNum  页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<KbExamWrongView> wrongBook(Long userId, String category, Integer pageNum, Integer pageSize);

    /**
     * 标记已掌握（移出错题集）。
     *
     * @param userId     用户ID
     * @param questionId 题目ID
     */
    void markMastered(Long userId, Long questionId);
}
