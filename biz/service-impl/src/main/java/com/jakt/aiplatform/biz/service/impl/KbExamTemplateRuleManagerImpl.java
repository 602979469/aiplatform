package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.biz.service.KbExamTemplateRuleManager;
import com.jakt.aiplatform.core.model.domain.KbExamTemplateRule;
import com.jakt.aiplatform.core.model.param.KbExamTemplateRuleQueryParam;
import com.jakt.aiplatform.core.service.KbExamTemplateRuleService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 试卷模板知识点规则管理实现类
 */
@Service
public class KbExamTemplateRuleManagerImpl implements KbExamTemplateRuleManager {

    /** 试卷模板知识点规则领域服务。 */
    private final KbExamTemplateRuleService kbExamTemplateRuleService;

    public KbExamTemplateRuleManagerImpl(KbExamTemplateRuleService kbExamTemplateRuleService) {
        this.kbExamTemplateRuleService = kbExamTemplateRuleService;
    }

    @Override
    public KbExamTemplateRule createKbExamTemplateRule(KbExamTemplateRule kbExamTemplateRule) {
        KbExamTemplateRule created = kbExamTemplateRuleService.createKbExamTemplateRule(kbExamTemplateRule);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建试卷模板知识点规则成功 id={}", created.getId());
        return created;
    }

    @Override
    public KbExamTemplateRule getKbExamTemplateRule(Long id) {
        return kbExamTemplateRuleService.getKbExamTemplateRule(id);
    }

    @Override
    public PageResult<KbExamTemplateRule> pageKbExamTemplateRules(KbExamTemplateRuleQueryParam query) {
        return kbExamTemplateRuleService.findPage(query);
    }

    @Override
    public List<KbExamTemplateRule> listKbExamTemplateRules(KbExamTemplateRuleQueryParam query) {
        return kbExamTemplateRuleService.findList(query);
    }

    @Override
    public int updateKbExamTemplateRule(KbExamTemplateRule kbExamTemplateRule) {
        int affected = kbExamTemplateRuleService.updateKbExamTemplateRule(kbExamTemplateRule);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新试卷模板知识点规则成功 id={} 影响行数={}",
                kbExamTemplateRule.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(KbExamTemplateRule kbExamTemplateRule) {
        int affected = kbExamTemplateRuleService.updateByCondition(kbExamTemplateRule);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新试卷模板知识点规则成功 id={} 影响行数={}",
                kbExamTemplateRule.getId(), affected);
        return affected;
    }

    @Override
    public int deleteKbExamTemplateRule(Long id) {
        int affected = kbExamTemplateRuleService.deleteKbExamTemplateRule(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除试卷模板知识点规则成功 id={} 影响行数={}", id, affected);
        return affected;
    }
}
