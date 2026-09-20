package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.KbExamManager;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbExamPaper;
import com.jakt.aiplatform.core.model.dto.KbExamPaperView;
import com.jakt.aiplatform.core.model.dto.KbExamResultView;
import com.jakt.aiplatform.core.model.dto.KbExamWrongView;
import com.jakt.aiplatform.core.model.param.KbExamStartParam;
import com.jakt.aiplatform.core.service.KbExamService;
import com.jakt.aiplatform.core.service.KbExamWrongBookService;
import org.springframework.stereotype.Service;

/**
 * 考题系统用例编排：组卷、续考、答题、交卷判分、成绩、历史记录、错题集。
 *
 * <p>规则与持久化全部下沉 core-service，本类只做用例编排。
 */
@Service
public class KbExamManagerImpl implements KbExamManager {

    /** 考试领域服务。 */
    private final KbExamService kbExamService;

    /** 错题集领域服务。 */
    private final KbExamWrongBookService kbExamWrongBookService;

    public KbExamManagerImpl(KbExamService kbExamService,
                             KbExamWrongBookService kbExamWrongBookService) {
        this.kbExamService = kbExamService;
        this.kbExamWrongBookService = kbExamWrongBookService;
    }

    @Override
    public KbExamPaperView start(KbExamStartParam param) {
        return kbExamService.start(param);
    }

    @Override
    public KbExamPaperView getPaper(Long paperId, Long userId) {
        return kbExamService.getPaper(paperId, userId);
    }

    @Override
    public void answer(Long paperId, Long userId, Integer seq, String userAnswer, Integer costSeconds) {
        kbExamService.answer(paperId, userId, seq, userAnswer, costSeconds);
    }

    @Override
    public KbExamResultView submit(Long paperId, Long userId) {
        return kbExamService.submit(paperId, userId);
    }

    @Override
    public KbExamResultView result(Long paperId, Long userId) {
        return kbExamService.result(paperId, userId);
    }

    @Override
    public PageResult<KbExamPaper> history(Long userId, Integer pageNum, Integer pageSize) {
        return kbExamService.history(userId, pageNum, pageSize);
    }

    @Override
    public void deletePaper(Long paperId, Long userId) {
        kbExamService.deletePaper(paperId, userId);
    }

    @Override
    public PageResult<KbExamWrongView> wrongBook(Long userId, String category, Integer pageNum, Integer pageSize) {
        return kbExamWrongBookService.wrongBook(userId, category, pageNum, pageSize);
    }

    @Override
    public void markMastered(Long userId, Long questionId) {
        kbExamWrongBookService.markMastered(userId, questionId);
    }
}
