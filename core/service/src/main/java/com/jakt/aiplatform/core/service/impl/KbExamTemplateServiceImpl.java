package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbExamTemplate;
import com.jakt.aiplatform.core.model.param.KbExamTemplateQueryParam;
import com.jakt.aiplatform.core.repository.KbExamTemplateRepository;
import com.jakt.aiplatform.core.service.KbExamTemplateService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 试卷模板领域服务实现：承载试卷模板相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class KbExamTemplateServiceImpl implements KbExamTemplateService {

    /** 试卷模板仓储。 */
    private final KbExamTemplateRepository kbExamTemplateRepository;

    public KbExamTemplateServiceImpl(KbExamTemplateRepository kbExamTemplateRepository) {
        this.kbExamTemplateRepository = kbExamTemplateRepository;
    }

    @Override
    public KbExamTemplate createKbExamTemplate(KbExamTemplate kbExamTemplate) {
        return kbExamTemplateRepository.insert(kbExamTemplate);
    }

    @Override
    public int updateKbExamTemplate(KbExamTemplate kbExamTemplate) {
        return kbExamTemplateRepository.update(kbExamTemplate);
    }

    @Override
    public int updateByCondition(KbExamTemplate kbExamTemplate) {
        return kbExamTemplateRepository.updateByCondition(kbExamTemplate);
    }

    @Override
    public int deleteKbExamTemplate(Long id) {
        return kbExamTemplateRepository.deleteById(id);
    }

    @Override
    public KbExamTemplate getKbExamTemplate(Long id) {
        return kbExamTemplateRepository.findById(id);
    }

    @Override
    public PageResult<KbExamTemplate> findPage(KbExamTemplateQueryParam query) {
        return kbExamTemplateRepository.findPage(query);
    }

    @Override
    public List<KbExamTemplate> findList(KbExamTemplateQueryParam query) {
        return kbExamTemplateRepository.findList(query);
    }
}
