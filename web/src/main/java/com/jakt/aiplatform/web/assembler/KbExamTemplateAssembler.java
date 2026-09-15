package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.constant.PageConstants;
import com.jakt.aiplatform.core.model.domain.KbExamTemplate;
import com.jakt.aiplatform.core.model.param.KbExamTemplateQueryParam;
import com.jakt.aiplatform.web.param.KbExamTemplateCreateRequest;
import com.jakt.aiplatform.web.param.KbExamTemplateQueryRequest;
import com.jakt.aiplatform.web.param.KbExamTemplateUpdateRequest;
import com.jakt.aiplatform.web.result.KbExamTemplateResponse;

/**
 * 试卷模板对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class KbExamTemplateAssembler {

    private KbExamTemplateAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建试卷模板请求 DTO；为空返回 null
     * @return 试卷模板领域模型
     */
    public static KbExamTemplate toModel(KbExamTemplateCreateRequest request) {
        if (request == null) {
            return null;
        }
        KbExamTemplate kbExamTemplate = new KbExamTemplate();
        kbExamTemplate.setName(request.getName());
        kbExamTemplate.setDescription(request.getDescription());
        kbExamTemplate.setScope(request.getScope());
        kbExamTemplate.setOwnerUserId(request.getOwnerUserId());
        kbExamTemplate.setStatus(request.getStatus());
        kbExamTemplate.setMode(request.getMode());
        kbExamTemplate.setQuestionCount(request.getQuestionCount());
        kbExamTemplate.setPerQuestionSeconds(request.getPerQuestionSeconds());
        kbExamTemplate.setObjectiveOnly(request.getObjectiveOnly());
        kbExamTemplate.setExcludeMastered(request.getExcludeMastered());
        kbExamTemplate.setTypeMix(request.getTypeMix());
        kbExamTemplate.setDifficultyMix(request.getDifficultyMix());
        kbExamTemplate.setUseCount(request.getUseCount());
        kbExamTemplate.setCreateBy(request.getCreateBy());
        kbExamTemplate.setUpdateBy(request.getUpdateBy());
        return kbExamTemplate;
    }

    /**
     * 更新请求 DTO + 路径主键 → 领域模型。
     *
     * @param request 更新试卷模板请求 DTO；为空返回 null
     * @param id 路径中的试卷模板主键
     * @return 试卷模板领域模型
     */
    public static KbExamTemplate toModel(KbExamTemplateUpdateRequest request, Long id) {
        if (request == null) {
            return null;
        }
        KbExamTemplate kbExamTemplate = new KbExamTemplate();
        kbExamTemplate.setId(id);
        kbExamTemplate.setName(request.getName());
        kbExamTemplate.setDescription(request.getDescription());
        kbExamTemplate.setScope(request.getScope());
        kbExamTemplate.setOwnerUserId(request.getOwnerUserId());
        kbExamTemplate.setStatus(request.getStatus());
        kbExamTemplate.setMode(request.getMode());
        kbExamTemplate.setQuestionCount(request.getQuestionCount());
        kbExamTemplate.setPerQuestionSeconds(request.getPerQuestionSeconds());
        kbExamTemplate.setObjectiveOnly(request.getObjectiveOnly());
        kbExamTemplate.setExcludeMastered(request.getExcludeMastered());
        kbExamTemplate.setTypeMix(request.getTypeMix());
        kbExamTemplate.setDifficultyMix(request.getDifficultyMix());
        kbExamTemplate.setUseCount(request.getUseCount());
        kbExamTemplate.setCreateBy(request.getCreateBy());
        kbExamTemplate.setUpdateBy(request.getUpdateBy());
        return kbExamTemplate;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 试卷模板查询请求 DTO；为空返回空查询参数（分页走默认值）
     * @return 试卷模板查询参数
     */
    public static KbExamTemplateQueryParam toQueryParam(KbExamTemplateQueryRequest request) {
        if (request == null) {
            return new KbExamTemplateQueryParam();
        }
        KbExamTemplateQueryParam param = new KbExamTemplateQueryParam();
        param.setId(request.getId());
        param.setName(request.getName());
        param.setDescription(request.getDescription());
        param.setScope(request.getScope());
        param.setOwnerUserId(request.getOwnerUserId());
        param.setStatus(request.getStatus());
        param.setMode(request.getMode());
        param.setQuestionCount(request.getQuestionCount());
        param.setPerQuestionSeconds(request.getPerQuestionSeconds());
        param.setObjectiveOnly(request.getObjectiveOnly());
        param.setExcludeMastered(request.getExcludeMastered());
        param.setUseCount(request.getUseCount());
        param.setCreateBy(request.getCreateBy());
        param.setUpdateBy(request.getUpdateBy());
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
     * @param kbExamTemplate 试卷模板领域模型；为空返回 null
     * @return 试卷模板响应 VO
     */
    public static KbExamTemplateResponse toResponse(KbExamTemplate kbExamTemplate) {
        if (kbExamTemplate == null) {
            return null;
        }
        KbExamTemplateResponse response = new KbExamTemplateResponse();
        response.setId(kbExamTemplate.getId());
        response.setName(kbExamTemplate.getName());
        response.setDescription(kbExamTemplate.getDescription());
        response.setScope(kbExamTemplate.getScope());
        response.setOwnerUserId(kbExamTemplate.getOwnerUserId());
        response.setStatus(kbExamTemplate.getStatus());
        response.setMode(kbExamTemplate.getMode());
        response.setQuestionCount(kbExamTemplate.getQuestionCount());
        response.setPerQuestionSeconds(kbExamTemplate.getPerQuestionSeconds());
        response.setObjectiveOnly(kbExamTemplate.getObjectiveOnly());
        response.setExcludeMastered(kbExamTemplate.getExcludeMastered());
        response.setTypeMix(kbExamTemplate.getTypeMix());
        response.setDifficultyMix(kbExamTemplate.getDifficultyMix());
        response.setUseCount(kbExamTemplate.getUseCount());
        response.setCreateBy(kbExamTemplate.getCreateBy());
        response.setUpdateBy(kbExamTemplate.getUpdateBy());
        response.setCreateTime(kbExamTemplate.getCreateTime());
        response.setUpdateTime(kbExamTemplate.getUpdateTime());
        return response;
    }
}
