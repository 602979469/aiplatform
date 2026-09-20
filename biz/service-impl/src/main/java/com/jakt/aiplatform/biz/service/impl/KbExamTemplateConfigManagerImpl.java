package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.KbExamTemplateConfigManager;
import com.jakt.aiplatform.core.model.dto.KbExamTemplateView;
import com.jakt.aiplatform.core.service.KbExamTemplateConfigService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 试卷模板配置用例编排：规则与持久化下沉 core-service。
 */
@Service
public class KbExamTemplateConfigManagerImpl implements KbExamTemplateConfigManager {

    /** 试卷模板配置领域服务。 */
    private final KbExamTemplateConfigService kbExamTemplateConfigService;

    public KbExamTemplateConfigManagerImpl(KbExamTemplateConfigService kbExamTemplateConfigService) {
        this.kbExamTemplateConfigService = kbExamTemplateConfigService;
    }

    @Override
    public List<KbExamTemplateView> list(Long userId) {
        return kbExamTemplateConfigService.list(userId);
    }

    @Override
    public KbExamTemplateView get(Long id) {
        return kbExamTemplateConfigService.get(id);
    }

    @Override
    public Long save(Long userId, KbExamTemplateView view) {
        return kbExamTemplateConfigService.save(userId, view);
    }

    @Override
    public void delete(Long userId, Long id, boolean isAdmin) {
        kbExamTemplateConfigService.delete(userId, id, isAdmin);
    }
}
