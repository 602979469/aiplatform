package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.collection.CollUtil;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.template.BizTemplate;
import com.jakt.aiplatform.common.framework.template.TransactionTemplate;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.constant.KbExamConstant;
import com.jakt.aiplatform.core.model.domain.KbExamTemplate;
import com.jakt.aiplatform.core.model.domain.KbExamTemplateRule;
import com.jakt.aiplatform.core.model.dto.KbExamTemplateView;
import com.jakt.aiplatform.core.model.param.KbExamRuleParam;
import com.jakt.aiplatform.core.model.param.KbExamTemplateQueryParam;
import com.jakt.aiplatform.core.repository.KbExamTemplateRepository;
import com.jakt.aiplatform.core.repository.KbExamTemplateRuleRepository;
import com.jakt.aiplatform.core.service.KbExamTemplateConfigService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 试卷模板配置领域服务实现：模板主表 + 知识点规则（规则先删后插整体替换）。
 */
@Service
public class KbExamTemplateConfigServiceImpl implements KbExamTemplateConfigService {

    /** 模板仓储。 */
    private final KbExamTemplateRepository kbExamTemplateRepository;

    /** 模板规则仓储。 */
    private final KbExamTemplateRuleRepository kbExamTemplateRuleRepository;

    /** 事务模板。 */
    private final TransactionTemplate transactionTemplate;

    public KbExamTemplateConfigServiceImpl(KbExamTemplateRepository kbExamTemplateRepository,
                                           KbExamTemplateRuleRepository kbExamTemplateRuleRepository,
                                           TransactionTemplate transactionTemplate) {
        this.kbExamTemplateRepository = kbExamTemplateRepository;
        this.kbExamTemplateRuleRepository = kbExamTemplateRuleRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public List<KbExamTemplateView> list(Long userId) {
        KbExamTemplateQueryParam globalQuery = new KbExamTemplateQueryParam();
        globalQuery.setScope(KbExamConstant.SCOPE_GLOBAL);
        globalQuery.setStatus(KbExamConstant.TEMPLATE_STATUS_PUBLISHED);
        globalQuery.setPageNum(1);
        globalQuery.setPageSize(KbExamConstant.LIST_LIMIT);
        List<KbExamTemplate> templates = new ArrayList<>(kbExamTemplateRepository.findList(globalQuery));

        KbExamTemplateQueryParam mineQuery = new KbExamTemplateQueryParam();
        mineQuery.setScope(KbExamConstant.SCOPE_PERSONAL);
        mineQuery.setOwnerUserId(userId);
        mineQuery.setPageNum(1);
        mineQuery.setPageSize(KbExamConstant.LIST_LIMIT);
        templates.addAll(kbExamTemplateRepository.findList(mineQuery));

        return ConvertUtil.map(templates, this::toView);
    }

    @Override
    public KbExamTemplateView get(Long id) {
        KbExamTemplate template = kbExamTemplateRepository.findById(id);
        AssertUtil.throwErrWhenNull(template, ErrorCodeEnum.PARAM_INVALID, "模板不存在: " + id);
        KbExamTemplateView view = toView(template);
        view.setRules(listRules(id));
        return view;
    }

    @Override
    public Long save(Long userId, KbExamTemplateView view) {
        AssertUtil.throwErrWhenBlank(view.getName(), ErrorCodeEnum.PARAM_INVALID, "模板名称不能为空");

        List<KbExamRuleParam> sourceRules =
                ObjectUtil.isNull(view.getRules()) ? Collections.emptyList() : view.getRules();
        List<KbExamRuleParam> validRules = sourceRules.stream()
                .filter(rule -> StrUtil.isNotBlank(rule.getCategory()))
                .filter(rule -> ObjectUtil.isNotNull(rule.getCount()) && rule.getCount() > 0)
                .collect(Collectors.toList());
        AssertUtil.throwErrWhenTrue(CollUtil.isEmpty(validRules), ErrorCodeEnum.PARAM_INVALID, "请至少配置一个知识点");

        int total = validRules.stream().mapToInt(KbExamRuleParam::getCount).sum();
        KbExamTemplate template = new KbExamTemplate();
        template.setId(view.getId());
        template.setName(view.getName());
        template.setDescription(view.getDescription());
        template.setScope(StrUtil.blankToDefault(view.getScope(), KbExamConstant.SCOPE_PERSONAL));
        template.setOwnerUserId(userId);
        template.setStatus(StrUtil.blankToDefault(view.getStatus(), KbExamConstant.TEMPLATE_STATUS_PUBLISHED));
        template.setMode(StrUtil.blankToDefault(view.getMode(), KbExamConstant.MODE_NORMAL));
        template.setQuestionCount(ObjectUtil.isNull(view.getQuestionCount()) ? total : view.getQuestionCount());
        template.setPerQuestionSeconds(ObjectUtil.isNull(view.getPerQuestionSeconds())
                ? KbExamConstant.DEFAULT_PER_QUESTION_SECONDS : view.getPerQuestionSeconds());
        template.setObjectiveOnly(ObjectUtil.isNull(view.getObjectiveOnly()) ? 1 : view.getObjectiveOnly());
        template.setExcludeMastered(ObjectUtil.isNull(view.getExcludeMastered()) ? 1 : view.getExcludeMastered());

        BizTemplate.executeWithoutResult(transactionTemplate, () -> {
            if (ObjectUtil.isNull(template.getId())) {
                template.setUseCount(0);
                kbExamTemplateRepository.insert(template);
            } else {
                kbExamTemplateRepository.updateByCondition(template);
                kbExamTemplateRuleRepository.deleteByTemplateId(template.getId());
            }
            int order = 1;
            for (KbExamRuleParam rule : validRules) {
                KbExamTemplateRule row = new KbExamTemplateRule();
                row.setTemplateId(template.getId());
                row.setCategory(rule.getCategory());
                row.setSubtopic(rule.getSubtopic());
                row.setQuestionType(rule.getQuestionType());
                row.setDifficulty(rule.getDifficulty());
                row.setQuestionCount(rule.getCount());
                row.setOrderNum(order++);
                kbExamTemplateRuleRepository.insert(row);
            }
        });
        return template.getId();
    }

    @Override
    public void delete(Long userId, Long id, boolean isAdmin) {
        KbExamTemplate template = kbExamTemplateRepository.findById(id);
        AssertUtil.throwErrWhenNull(template, ErrorCodeEnum.PARAM_INVALID, "模板不存在: " + id);
        boolean owner = ObjectUtil.isNotNull(template.getOwnerUserId()) && template.getOwnerUserId().equals(userId);
        AssertUtil.throwErrWhenFalse(owner || isAdmin, ErrorCodeEnum.PARAM_INVALID, "只能删除自己创建的模板");

        BizTemplate.executeWithoutResult(transactionTemplate, () -> {
            kbExamTemplateRuleRepository.deleteByTemplateId(id);
            kbExamTemplateRepository.deleteById(id);
        });
    }

    /**
     * 领域模型 → 视图。
     *
     * @param template 模板领域模型
     * @return 模板视图
     */
    private KbExamTemplateView toView(KbExamTemplate template) {
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

    /**
     * 查询模板下的知识点规则。
     *
     * @param templateId 模板ID
     * @return 规则列表
     */
    private List<KbExamRuleParam> listRules(Long templateId) {
        List<KbExamTemplateRule> rules = kbExamTemplateRuleRepository.findByTemplateId(templateId);
        return rules.stream().map(rule -> {
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
