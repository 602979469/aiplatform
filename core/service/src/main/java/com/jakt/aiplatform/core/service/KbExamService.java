package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbExamPaper;
import com.jakt.aiplatform.core.model.dto.KbExamPaperView;
import com.jakt.aiplatform.core.model.dto.KbExamResultView;
import com.jakt.aiplatform.core.model.param.KbExamStartParam;

/**
 * 考试领域服务：组卷、续考、答题落库、交卷判分、成绩与历史记录。
 */
public interface KbExamService {

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
     * @param userId 用户ID
     * @return 试卷视图
     */
    KbExamPaperView getPaper(Long paperId, Long userId);

    /**
     * 提交单题作答（幂等，按题号覆盖）。
     *
     * @param paperId 试卷ID
     * @param userId 用户ID
     * @param seq 题号
     * @param userAnswer 作答内容
     * @param costSeconds 本题耗时（秒，可空）
     */
    void answer(Long paperId, Long userId, Integer seq, String userAnswer, Integer costSeconds);

    /**
     * 交卷判分（超时自动交卷也走这里；幂等：已判分的卷子直接返回成绩）。
     *
     * @param paperId 试卷ID
     * @param userId 用户ID
     * @return 成绩视图
     */
    KbExamResultView submit(Long paperId, Long userId);

    /**
     * 查询成绩。
     *
     * @param paperId 试卷ID
     * @param userId 用户ID
     * @return 成绩视图
     */
    KbExamResultView result(Long paperId, Long userId);

    /**
     * 考试历史记录（顺带兜底把超时未交卷的试卷自动交卷）。
     *
     * @param userId 用户ID
     * @param pageNum 页码
     * @param pageSize 每页条数
     * @return 分页结果
     */
    PageResult<KbExamPaper> history(Long userId, Integer pageNum, Integer pageSize);

    /**
     * 删除考试记录（先删答题明细，再删试卷）。
     *
     * @param paperId 试卷ID
     * @param userId 用户ID
     */
    void deletePaper(Long paperId, Long userId);
}
