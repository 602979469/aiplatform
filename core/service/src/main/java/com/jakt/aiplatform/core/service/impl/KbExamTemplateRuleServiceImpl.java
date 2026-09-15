package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbExamTemplateRule;
import com.jakt.aiplatform.core.model.param.KbExamTemplateRuleQueryParam;
import com.jakt.aiplatform.core.repository.KbExamTemplateRuleRepository;
import com.jakt.aiplatform.core.service.KbExamTemplateRuleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 试卷模板知识点规则领域服务实现：承载试卷模板知识点规则相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class KbExamTemplateRuleServiceImpl implements KbExamTemplateRuleService {

    /** 试卷模板知识点规则仓储。 */
    private final KbExamTemplateRuleRepository kbExamTemplateRuleRepository;

    public KbExamTemplateRuleServiceImpl(KbExamTemplateRuleRepository kbExamTemplateRuleRepository) {
        this.kbExamTemplateRuleRepository = kbExamTemplateRuleRepository;
    }

    @Override
    public KbExamTemplateRule createKbExamTemplateRule(KbExamTemplateRule kbExamTemplateRule) {
        return kbExamTemplateRuleRepository.insert(kbExamTemplateRule);
    }

    @Override
    public int updateKbExamTemplateRule(KbExamTemplateRule kbExamTemplateRule) {
        return kbExamTemplateRuleRepository.update(kbExamTemplateRule);
    }

    @Override
    public int updateByCondition(KbExamTemplateRule kbExamTemplateRule) {
        return kbExamTemplateRuleRepository.updateByCondition(kbExamTemplateRule);
    }

    @Override
    public int deleteKbExamTemplateRule(Long id) {
        return kbExamTemplateRuleRepository.deleteById(id);
    }

    @Override
    public KbExamTemplateRule getKbExamTemplateRule(Long id) {
        return kbExamTemplateRuleRepository.findById(id);
    }

    @Override
    public PageResult<KbExamTemplateRule> findPage(KbExamTemplateRuleQueryParam query) {
        return kbExamTemplateRuleRepository.findPage(query);
    }

    @Override
    public List<KbExamTemplateRule> findList(KbExamTemplateRuleQueryParam query) {
        return kbExamTemplateRuleRepository.findList(query);
    }
}
