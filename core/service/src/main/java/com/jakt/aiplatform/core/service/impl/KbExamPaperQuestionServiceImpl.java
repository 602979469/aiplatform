package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbExamPaperQuestion;
import com.jakt.aiplatform.core.model.param.KbExamPaperQuestionQueryParam;
import com.jakt.aiplatform.core.repository.KbExamPaperQuestionRepository;
import com.jakt.aiplatform.core.service.KbExamPaperQuestionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 试卷题目快照与作答领域服务实现：承载试卷题目快照与作答相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class KbExamPaperQuestionServiceImpl implements KbExamPaperQuestionService {

    /** 试卷题目快照与作答仓储。 */
    private final KbExamPaperQuestionRepository kbExamPaperQuestionRepository;

    public KbExamPaperQuestionServiceImpl(KbExamPaperQuestionRepository kbExamPaperQuestionRepository) {
        this.kbExamPaperQuestionRepository = kbExamPaperQuestionRepository;
    }

    @Override
    public KbExamPaperQuestion createKbExamPaperQuestion(KbExamPaperQuestion kbExamPaperQuestion) {
        return kbExamPaperQuestionRepository.insert(kbExamPaperQuestion);
    }

    @Override
    public int updateKbExamPaperQuestion(KbExamPaperQuestion kbExamPaperQuestion) {
        return kbExamPaperQuestionRepository.update(kbExamPaperQuestion);
    }

    @Override
    public int updateByCondition(KbExamPaperQuestion kbExamPaperQuestion) {
        return kbExamPaperQuestionRepository.updateByCondition(kbExamPaperQuestion);
    }

    @Override
    public int deleteKbExamPaperQuestion(Long id) {
        return kbExamPaperQuestionRepository.deleteById(id);
    }

    @Override
    public KbExamPaperQuestion getKbExamPaperQuestion(Long id) {
        return kbExamPaperQuestionRepository.findById(id);
    }

    @Override
    public PageResult<KbExamPaperQuestion> findPage(KbExamPaperQuestionQueryParam query) {
        return kbExamPaperQuestionRepository.findPage(query);
    }

    @Override
    public List<KbExamPaperQuestion> findList(KbExamPaperQuestionQueryParam query) {
        return kbExamPaperQuestionRepository.findList(query);
    }
}
