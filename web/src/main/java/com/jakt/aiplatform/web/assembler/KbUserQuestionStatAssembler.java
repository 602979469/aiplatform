package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.constant.PageConstants;
import com.jakt.aiplatform.core.model.domain.KbUserQuestionStat;
import com.jakt.aiplatform.core.model.param.KbUserQuestionStatQueryParam;
import com.jakt.aiplatform.web.param.KbUserQuestionStatCreateRequest;
import com.jakt.aiplatform.web.param.KbUserQuestionStatQueryRequest;
import com.jakt.aiplatform.web.param.KbUserQuestionStatUpdateRequest;
import com.jakt.aiplatform.web.result.KbUserQuestionStatResponse;

/**
 * 用户题目掌握状态对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class KbUserQuestionStatAssembler {

    private KbUserQuestionStatAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建用户题目掌握状态请求 DTO；为空返回 null
     * @return 用户题目掌握状态领域模型
     */
    public static KbUserQuestionStat toModel(KbUserQuestionStatCreateRequest request) {
        if (request == null) {
            return null;
        }
        KbUserQuestionStat kbUserQuestionStat = new KbUserQuestionStat();
        kbUserQuestionStat.setUserId(request.getUserId());
        kbUserQuestionStat.setQuestionId(request.getQuestionId());
        kbUserQuestionStat.setRightCount(request.getRightCount());
        kbUserQuestionStat.setWrongCount(request.getWrongCount());
        kbUserQuestionStat.setLastResult(request.getLastResult());
        kbUserQuestionStat.setLastAnswerTime(request.getLastAnswerTime());
        kbUserQuestionStat.setFirstRightTime(request.getFirstRightTime());
        kbUserQuestionStat.setMastered(request.getMastered());
        kbUserQuestionStat.setInWrongBook(request.getInWrongBook());
        return kbUserQuestionStat;
    }

    /**
     * 更新请求 DTO + 路径主键 → 领域模型。
     *
     * @param request 更新用户题目掌握状态请求 DTO；为空返回 null
     * @param id 路径中的用户题目掌握状态主键
     * @return 用户题目掌握状态领域模型
     */
    public static KbUserQuestionStat toModel(KbUserQuestionStatUpdateRequest request, Long id) {
        if (request == null) {
            return null;
        }
        KbUserQuestionStat kbUserQuestionStat = new KbUserQuestionStat();
        kbUserQuestionStat.setId(id);
        kbUserQuestionStat.setUserId(request.getUserId());
        kbUserQuestionStat.setQuestionId(request.getQuestionId());
        kbUserQuestionStat.setRightCount(request.getRightCount());
        kbUserQuestionStat.setWrongCount(request.getWrongCount());
        kbUserQuestionStat.setLastResult(request.getLastResult());
        kbUserQuestionStat.setLastAnswerTime(request.getLastAnswerTime());
        kbUserQuestionStat.setFirstRightTime(request.getFirstRightTime());
        kbUserQuestionStat.setMastered(request.getMastered());
        kbUserQuestionStat.setInWrongBook(request.getInWrongBook());
        return kbUserQuestionStat;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 用户题目掌握状态查询请求 DTO；为空返回空查询参数（分页走默认值）
     * @return 用户题目掌握状态查询参数
     */
    public static KbUserQuestionStatQueryParam toQueryParam(KbUserQuestionStatQueryRequest request) {
        if (request == null) {
            return new KbUserQuestionStatQueryParam();
        }
        KbUserQuestionStatQueryParam param = new KbUserQuestionStatQueryParam();
        param.setId(request.getId());
        param.setUserId(request.getUserId());
        param.setQuestionId(request.getQuestionId());
        param.setRightCount(request.getRightCount());
        param.setWrongCount(request.getWrongCount());
        param.setLastResult(request.getLastResult());
        param.setLastAnswerTime(request.getLastAnswerTime());
        param.setFirstRightTime(request.getFirstRightTime());
        param.setMastered(request.getMastered());
        param.setInWrongBook(request.getInWrongBook());
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
     * @param kbUserQuestionStat 用户题目掌握状态领域模型；为空返回 null
     * @return 用户题目掌握状态响应 VO
     */
    public static KbUserQuestionStatResponse toResponse(KbUserQuestionStat kbUserQuestionStat) {
        if (kbUserQuestionStat == null) {
            return null;
        }
        KbUserQuestionStatResponse response = new KbUserQuestionStatResponse();
        response.setId(kbUserQuestionStat.getId());
        response.setUserId(kbUserQuestionStat.getUserId());
        response.setQuestionId(kbUserQuestionStat.getQuestionId());
        response.setRightCount(kbUserQuestionStat.getRightCount());
        response.setWrongCount(kbUserQuestionStat.getWrongCount());
        response.setLastResult(kbUserQuestionStat.getLastResult());
        response.setLastAnswerTime(kbUserQuestionStat.getLastAnswerTime());
        response.setFirstRightTime(kbUserQuestionStat.getFirstRightTime());
        response.setMastered(kbUserQuestionStat.getMastered());
        response.setInWrongBook(kbUserQuestionStat.getInWrongBook());
        response.setCreateTime(kbUserQuestionStat.getCreateTime());
        response.setUpdateTime(kbUserQuestionStat.getUpdateTime());
        return response;
    }
}
