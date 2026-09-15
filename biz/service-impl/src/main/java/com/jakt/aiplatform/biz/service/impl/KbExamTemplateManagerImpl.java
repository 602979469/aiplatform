package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.biz.service.KbExamTemplateManager;
import com.jakt.aiplatform.core.model.domain.KbExamTemplate;
import com.jakt.aiplatform.core.model.param.KbExamTemplateQueryParam;
import com.jakt.aiplatform.core.service.KbExamTemplateService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 试卷模板管理实现类
 */
@Service
public class KbExamTemplateManagerImpl implements KbExamTemplateManager {

    /** 试卷模板领域服务。 */
    private final KbExamTemplateService kbExamTemplateService;

    public KbExamTemplateManagerImpl(KbExamTemplateService kbExamTemplateService) {
        this.kbExamTemplateService = kbExamTemplateService;
    }

    @Override
    public KbExamTemplate createKbExamTemplate(KbExamTemplate kbExamTemplate) {
        KbExamTemplate created = kbExamTemplateService.createKbExamTemplate(kbExamTemplate);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "创建试卷模板成功 id={}", created.getId());
        return created;
    }

    @Override
    public KbExamTemplate getKbExamTemplate(Long id) {
        return kbExamTemplateService.getKbExamTemplate(id);
    }

    @Override
    public PageResult<KbExamTemplate> pageKbExamTemplates(KbExamTemplateQueryParam query) {
        return kbExamTemplateService.findPage(query);
    }

    @Override
    public List<KbExamTemplate> listKbExamTemplates(KbExamTemplateQueryParam query) {
        return kbExamTemplateService.findList(query);
    }

    @Override
    public int updateKbExamTemplate(KbExamTemplate kbExamTemplate) {
        int affected = kbExamTemplateService.updateKbExamTemplate(kbExamTemplate);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "更新试卷模板成功 id={} 影响行数={}",
                kbExamTemplate.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(KbExamTemplate kbExamTemplate) {
        int affected = kbExamTemplateService.updateByCondition(kbExamTemplate);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "按条件更新试卷模板成功 id={} 影响行数={}",
                kbExamTemplate.getId(), affected);
        return affected;
    }

    @Override
    public int deleteKbExamTemplate(Long id) {
        int affected = kbExamTemplateService.deleteKbExamTemplate(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除试卷模板成功 id={} 影响行数={}", id, affected);
        return affected;
    }
}
