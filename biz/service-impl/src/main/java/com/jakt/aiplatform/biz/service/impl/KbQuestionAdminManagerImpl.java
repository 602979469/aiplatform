package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.KbQuestionAdminManager;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbQuestion;
import com.jakt.aiplatform.core.model.dto.KbQuestionMetaView;
import com.jakt.aiplatform.core.model.param.KbQuestionQueryParam;
import com.jakt.aiplatform.core.service.KbQuestionService;
import org.springframework.stereotype.Service;

/**
 * 题库管理用例编排：只做编排，规则与持久化下沉 core-service。
 */
@Service
public class KbQuestionAdminManagerImpl implements KbQuestionAdminManager {

    /** 题库领域服务。 */
    private final KbQuestionService kbQuestionService;

    public KbQuestionAdminManagerImpl(KbQuestionService kbQuestionService) {
        this.kbQuestionService = kbQuestionService;
    }

    @Override
    public PageResult<KbQuestion> page(KbQuestionQueryParam query) {
        return kbQuestionService.page(query);
    }

    @Override
    public KbQuestion get(Long id) {
        return kbQuestionService.get(id);
    }

    @Override
    public Long create(KbQuestion question) {
        return kbQuestionService.create(question);
    }

    @Override
    public void update(KbQuestion question) {
        kbQuestionService.update(question);
    }

    @Override
    public void delete(Long id) {
        kbQuestionService.delete(id);
    }

    @Override
    public KbQuestionMetaView meta() {
        return kbQuestionService.meta();
    }
}
