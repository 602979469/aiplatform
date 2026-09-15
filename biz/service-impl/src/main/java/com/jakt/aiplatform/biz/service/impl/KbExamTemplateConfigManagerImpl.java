package com.jakt.aiplatform.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.biz.service.KbExamRuleParam;
import com.jakt.aiplatform.biz.service.KbExamTemplateConfigManager;
import com.jakt.aiplatform.biz.service.KbExamTemplateView;
import com.jakt.aiplatform.common.dal.dataobject.KbExamTemplateDO;
import com.jakt.aiplatform.common.dal.dataobject.KbExamTemplateRuleDO;
import com.jakt.aiplatform.common.dal.mapper.KbExamTemplateMapper;
import com.jakt.aiplatform.common.dal.mapper.KbExamTemplateRuleMapper;
import com.jakt.aiplatform.common.dal.query.KbExamTemplateDalQuery;
import com.jakt.aiplatform.common.dal.query.KbExamTemplateRuleDalQuery;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.template.BizTemplate;
import com.jakt.aiplatform.common.framework.template.TransactionTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 试卷模板（配置管理）实现：模板主表 + 知识点规则（规则先删后插整体替换）。
 */
@Service
public class KbExamTemplateConfigManagerImpl implements KbExamTemplateConfigManager {

    private final KbExamTemplateMapper kbExamTemplateMapper;

    private final KbExamTemplateRuleMapper kbExamTemplateRuleMapper;

    private final TransactionTemplate transactionTemplate;

    public KbExamTemplateConfigManagerImpl(KbExamTemplateMapper kbExamTemplateMapper,
                                           KbExamTemplateRuleMapper kbExamTemplateRuleMapper,
                                           TransactionTemplate transactionTemplate) {
        this.kbExamTemplateMapper = kbExamTemplateMapper;
        this.kbExamTemplateRuleMapper = kbExamTemplateRuleMapper;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public List<KbExamTemplateView> list(Long userId) {
        KbExamTemplateDalQuery globalQuery = new KbExamTemplateDalQuery();
        globalQuery.setScope("GLOBAL");
        globalQuery.setStatus("PUBLISHED");
        globalQuery.setPageNum(1);
        globalQuery.setPageSize(200);
        List<KbExamTemplateDO> templates = new ArrayList<>(kbExamTemplateMapper.selectList(globalQuery));
        if (userId != null) {
            KbExamTemplateDalQuery mineQuery = new KbExamTemplateDalQuery();
            mineQuery.setScope("PERSONAL");
            mineQuery.setOwnerUserId(userId);
            mineQuery.setPageNum(1);
            mineQuery.setPageSize(200);
            templates.addAll(kbExamTemplateMapper.selectList(mineQuery));
        }
        return templates.stream().map(this::toView).collect(Collectors.toList());
    }

    @Override
    public KbExamTemplateView get(Long id) {
        KbExamTemplateDO template = kbExamTemplateMapper.selectById(id);
        if (template == null) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "模板不存在: " + id);
        }
        KbExamTemplateView view = toView(template);
        view.setRules(listRules(id));
        return view;
    }

    @Override
    public Long save(Long userId, KbExamTemplateView view) {
        if (StrUtil.isBlank(view.getName())) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "模板名称不能为空");
        }
        List<KbExamRuleParam> rules = view.getRules() == null ? Collections.emptyList() : view.getRules();
        List<KbExamRuleParam> validRules = rules.stream()
                .filter(rule -> StrUtil.isNotBlank(rule.getCategory())
                        && rule.getCount() != null && rule.getCount() > 0)
                .collect(Collectors.toList());
        if (validRules.isEmpty()) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "请至少配置一个知识点");
        }
        int total = validRules.stream().mapToInt(KbExamRuleParam::getCount).sum();

        KbExamTemplateDO template = new KbExamTemplateDO();
        template.setId(view.getId());
        template.setName(view.getName());
        template.setDescription(view.getDescription());
        template.setScope(StrUtil.blankToDefault(view.getScope(), "PERSONAL"));
        template.setOwnerUserId(userId);
        template.setStatus(StrUtil.blankToDefault(view.getStatus(), "PUBLISHED"));
        template.setMode(StrUtil.blankToDefault(view.getMode(), "NORMAL"));
        template.setQuestionCount(view.getQuestionCount() == null ? total : view.getQuestionCount());
        template.setPerQuestionSeconds(view.getPerQuestionSeconds() == null ? 60 : view.getPerQuestionSeconds());
        template.setObjectiveOnly(view.getObjectiveOnly() == null ? 1 : view.getObjectiveOnly());
        template.setExcludeMastered(view.getExcludeMastered() == null ? 1 : view.getExcludeMastered());

        BizTemplate.executeWithoutResult(transactionTemplate, () -> {
            if (template.getId() == null) {
                template.setUseCount(0);
                kbExamTemplateMapper.insert(template);
            } else {
                kbExamTemplateMapper.updateByCondition(template);
                kbExamTemplateRuleMapper.deleteByTemplateId(template.getId());
            }
            int order = 1;
            for (KbExamRuleParam rule : validRules) {
                KbExamTemplateRuleDO row = new KbExamTemplateRuleDO();
                row.setTemplateId(template.getId());
                row.setCategory(rule.getCategory());
                row.setSubtopic(rule.getSubtopic());
                row.setQuestionType(rule.getQuestionType());
                row.setDifficulty(rule.getDifficulty());
                row.setQuestionCount(rule.getCount());
                row.setOrderNum(order++);
                kbExamTemplateRuleMapper.insert(row);
            }
        });
        return template.getId();
    }

    @Override
    public void delete(Long userId, Long id, boolean isAdmin) {
        KbExamTemplateDO template = kbExamTemplateMapper.selectById(id);
        if (template == null) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "模板不存在: " + id);
        }
        boolean owner = template.getOwnerUserId() != null && template.getOwnerUserId().equals(userId);
        if (!owner && !isAdmin) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "只能删除自己创建的模板");
        }
        BizTemplate.executeWithoutResult(transactionTemplate, () -> {
            kbExamTemplateRuleMapper.deleteByTemplateId(id);
            kbExamTemplateMapper.deleteById(id);
        });
    }

    /** 模板 → 视图。 */
    private KbExamTemplateView toView(KbExamTemplateDO template) {
        KbExamTemplateView view = new KbExamTemplateView();
        view.setId(template.getId());
        view.setName(template.getName());
        view.setDescription(template.getDescription());
        view.setScope(template.getScope());
        view.setStatus(template.getStatus());
        view.setOwnerUserId(template.getOwnerUserId());
        view.setMode(template.getMode());
        view.setQuestionCount(template.getQuestionCount());
        view.setPerQuestionSeconds(template.getPerQuestionSeconds());
        view.setObjectiveOnly(template.getObjectiveOnly());
        view.setExcludeMastered(template.getExcludeMastered());
        view.setUseCount(template.getUseCount());
        view.setUpdateTime(template.getUpdateTime());
        return view;
    }

    /** 模板规则列表。 */
    private List<KbExamRuleParam> listRules(Long templateId) {
        KbExamTemplateRuleDalQuery query = new KbExamTemplateRuleDalQuery();
        query.setTemplateId(templateId);
        query.setPageNum(1);
        query.setPageSize(200);
        return kbExamTemplateRuleMapper.selectList(query).stream().map(rule -> {
            KbExamRuleParam item = new KbExamRuleParam();
            item.setCategory(rule.getCategory());
            item.setSubtopic(rule.getSubtopic());
            item.setQuestionType(rule.getQuestionType());
            item.setDifficulty(rule.getDifficulty());
            item.setCount(rule.getQuestionCount());
            return item;
        }).collect(Collectors.toList());
    }
}
