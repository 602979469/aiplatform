package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.KbQuestionSearchManager;
import com.jakt.aiplatform.core.model.dto.KbQuestionDetailView;
import com.jakt.aiplatform.core.model.dto.KbQuestionSearchView;
import com.jakt.aiplatform.core.model.param.KbQuestionSearchQuery;
import com.jakt.aiplatform.core.service.KbQuestionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 题库检索用例编排：检索与详情规则下沉 core-service。
 */
@Service
public class KbQuestionSearchManagerImpl implements KbQuestionSearchManager {

    /** 题库领域服务。 */
    private final KbQuestionService kbQuestionService;

    public KbQuestionSearchManagerImpl(KbQuestionService kbQuestionService) {
        this.kbQuestionService = kbQuestionService;
    }

    @Override
    public Map<String, List<KbQuestionSearchView.Bucket>> facets() {
        return kbQuestionService.facets();
    }

    @Override
    public KbQuestionSearchView search(KbQuestionSearchQuery query) {
        return kbQuestionService.search(query);
    }

    @Override
    public KbQuestionDetailView detail(Long id) {
        return kbQuestionService.detail(id);
    }
}
