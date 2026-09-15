package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.constant.PageConstants;
import com.jakt.aiplatform.core.model.domain.KbExamTemplateRule;
import com.jakt.aiplatform.core.model.param.KbExamTemplateRuleQueryParam;
import com.jakt.aiplatform.web.param.KbExamTemplateRuleCreateRequest;
import com.jakt.aiplatform.web.param.KbExamTemplateRuleQueryRequest;
import com.jakt.aiplatform.web.param.KbExamTemplateRuleUpdateRequest;
import com.jakt.aiplatform.web.result.KbExamTemplateRuleResponse;

/**
 * 试卷模板知识点规则对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class KbExamTemplateRuleAssembler {

    private KbExamTemplateRuleAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建试卷模板知识点规则请求 DTO；为空返回 null
     * @return 试卷模板知识点规则领域模型
     */
    public static KbExamTemplateRule toModel(KbExamTemplateRuleCreateRequest request) {
        if (request == null) {
            return null;
        }
        KbExamTemplateRule kbExamTemplateRule = new KbExamTemplateRule();
        kbExamTemplateRule.setTemplateId(request.getTemplateId());
        kbExamTemplateRule.setCategory(request.getCategory());
        kbExamTemplateRule.setSubtopic(request.getSubtopic());
        kbExamTemplateRule.setQuestionType(request.getQuestionType());
        kbExamTemplateRule.setDifficulty(request.getDifficulty());
        kbExamTemplateRule.setQuestionCount(request.getQuestionCount());
        kbExamTemplateRule.setOrderNum(request.getOrderNum());
        return kbExamTemplateRule;
    }

    /**
     * 更新请求 DTO + 路径主键 → 领域模型。
     *
     * @param request 更新试卷模板知识点规则请求 DTO；为空返回 null
     * @param id 路径中的试卷模板知识点规则主键
     * @return 试卷模板知识点规则领域模型
     */
    public static KbExamTemplateRule toModel(KbExamTemplateRuleUpdateRequest request, Long id) {
        if (request == null) {
            return null;
        }
        KbExamTemplateRule kbExamTemplateRule = new KbExamTemplateRule();
        kbExamTemplateRule.setId(id);
        kbExamTemplateRule.setTemplateId(request.getTemplateId());
        kbExamTemplateRule.setCategory(request.getCategory());
        kbExamTemplateRule.setSubtopic(request.getSubtopic());
        kbExamTemplateRule.setQuestionType(request.getQuestionType());
        kbExamTemplateRule.setDifficulty(request.getDifficulty());
        kbExamTemplateRule.setQuestionCount(request.getQuestionCount());
        kbExamTemplateRule.setOrderNum(request.getOrderNum());
        return kbExamTemplateRule;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 试卷模板知识点规则查询请求 DTO；为空返回空查询参数（分页走默认值）
     * @return 试卷模板知识点规则查询参数
     */
    public static KbExamTemplateRuleQueryParam toQueryParam(KbExamTemplateRuleQueryRequest request) {
        if (request == null) {
            return new KbExamTemplateRuleQueryParam();
        }
        KbExamTemplateRuleQueryParam param = new KbExamTemplateRuleQueryParam();
        param.setId(request.getId());
        param.setTemplateId(request.getTemplateId());
        param.setCategory(request.getCategory());
        param.setSubtopic(request.getSubtopic());
        param.setQuestionType(request.getQuestionType());
        param.setDifficulty(request.getDifficulty());
        param.setQuestionCount(request.getQuestionCount());
        param.setOrderNum(request.getOrderNum());
        param.setCreateTimeBegin(request.getCreateTimeBegin());
        param.setCreateTimeEnd(request.getCreateTimeEnd());
        param.setUpdateTimeBegin(request.getUpdateTimeBegin());
        param.setUpdateTimeEnd(request.getUpdateTimeEnd());
        param.setPageNum(ObjectUtil.defaultIfNull(request.getPageNum(), PageConstants.DEFAULT_PAGE_NUM));
        param.setPageSize(ObjectUtil.defaultIfNull(request.getPageSize(), PageConstants.DEFAULT_PAGE_SIZE));
        return param;
    }

    /**
     * 领域模型 → 响应 VO。
     *
     * @param kbExamTemplateRule 试卷模板知识点规则领域模型；为空返回 null
     * @return 试卷模板知识点规则响应 VO
     */
    public static KbExamTemplateRuleResponse toResponse(KbExamTemplateRule kbExamTemplateRule) {
        if (kbExamTemplateRule == null) {
            return null;
        }
        KbExamTemplateRuleResponse response = new KbExamTemplateRuleResponse();
        response.setId(kbExamTemplateRule.getId());
        response.setTemplateId(kbExamTemplateRule.getTemplateId());
        response.setCategory(kbExamTemplateRule.getCategory());
        response.setSubtopic(kbExamTemplateRule.getSubtopic());
        response.setQuestionType(kbExamTemplateRule.getQuestionType());
        response.setDifficulty(kbExamTemplateRule.getDifficulty());
        response.setQuestionCount(kbExamTemplateRule.getQuestionCount());
        response.setOrderNum(kbExamTemplateRule.getOrderNum());
        response.setCreateTime(kbExamTemplateRule.getCreateTime());
        response.setUpdateTime(kbExamTemplateRule.getUpdateTime());
        return response;
    }
}
