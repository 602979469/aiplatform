package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.biz.service.KbExamPaperQuestionManager;
import com.jakt.aiplatform.core.model.domain.KbExamPaperQuestion;
import com.jakt.aiplatform.core.model.param.KbExamPaperQuestionQueryParam;
import com.jakt.aiplatform.core.service.KbExamPaperQuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 试卷题目快照与作答管理实现类
 */
@Service
public class KbExamPaperQuestionManagerImpl implements KbExamPaperQuestionManager {

    /** 试卷题目快照与作答领域服务。 */
    private final KbExamPaperQuestionService kbExamPaperQuestionService;

    public KbExamPaperQuestionManagerImpl(KbExamPaperQuestionService kbExamPaperQuestionService) {
        this.kbExamPaperQuestionService = kbExamPaperQuestionService;
    }

    @Override
    public KbExamPaperQuestion createKbExamPaperQuestion(KbExamPaperQuestion kbExamPaperQuestion) {
        KbExamPaperQuestion created = kbExamPaperQuestionService.createKbExamPaperQuestion(kbExamPaperQuestion);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建试卷题目快照与作答成功 id={}", created.getId());
        return created;
    }

    @Override
    public KbExamPaperQuestion getKbExamPaperQuestion(Long id) {
        return kbExamPaperQuestionService.getKbExamPaperQuestion(id);
    }

    @Override
    public PageResult<KbExamPaperQuestion> pageKbExamPaperQuestions(KbExamPaperQuestionQueryParam query) {
        return kbExamPaperQuestionService.findPage(query);
    }

    @Override
    public List<KbExamPaperQuestion> listKbExamPaperQuestions(KbExamPaperQuestionQueryParam query) {
        return kbExamPaperQuestionService.findList(query);
    }

    @Override
    public int updateKbExamPaperQuestion(KbExamPaperQuestion kbExamPaperQuestion) {
        int affected = kbExamPaperQuestionService.updateKbExamPaperQuestion(kbExamPaperQuestion);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新试卷题目快照与作答成功 id={} 影响行数={}",
                kbExamPaperQuestion.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(KbExamPaperQuestion kbExamPaperQuestion) {
        int affected = kbExamPaperQuestionService.updateByCondition(kbExamPaperQuestion);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新试卷题目快照与作答成功 id={} 影响行数={}",
                kbExamPaperQuestion.getId(), affected);
        return affected;
    }

    @Override
    public int deleteKbExamPaperQuestion(Long id) {
        int affected = kbExamPaperQuestionService.deleteKbExamPaperQuestion(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除试卷题目快照与作答成功 id={} 影响行数={}", id, affected);
        return affected;
    }
}
