package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.constant.PageConstants;
import com.jakt.aiplatform.core.model.domain.KbExamPaper;
import com.jakt.aiplatform.core.model.param.KbExamPaperQueryParam;
import com.jakt.aiplatform.web.param.KbExamPaperCreateRequest;
import com.jakt.aiplatform.web.param.KbExamPaperQueryRequest;
import com.jakt.aiplatform.web.param.KbExamPaperUpdateRequest;
import com.jakt.aiplatform.web.result.KbExamPaperResponse;

/**
 * 考试试卷对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class KbExamPaperAssembler {

    private KbExamPaperAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建考试试卷请求 DTO；为空返回 null
     * @return 考试试卷领域模型
     */
    public static KbExamPaper toModel(KbExamPaperCreateRequest request) {
        if (request == null) {
            return null;
        }
        KbExamPaper kbExamPaper = new KbExamPaper();
        kbExamPaper.setUserId(request.getUserId());
        kbExamPaper.setTitle(request.getTitle());
        kbExamPaper.setMode(request.getMode());
        kbExamPaper.setStatus(request.getStatus());
        kbExamPaper.setPerQuestionSeconds(request.getPerQuestionSeconds());
        kbExamPaper.setTimeLimitSeconds(request.getTimeLimitSeconds());
        kbExamPaper.setQuestionCount(request.getQuestionCount());
        kbExamPaper.setTotalScore(request.getTotalScore());
        kbExamPaper.setScore(request.getScore());
        kbExamPaper.setCorrectCount(request.getCorrectCount());
        kbExamPaper.setWrongCount(request.getWrongCount());
        kbExamPaper.setUnansweredCount(request.getUnansweredCount());
        kbExamPaper.setCategories(request.getCategories());
        kbExamPaper.setStartTime(request.getStartTime());
        kbExamPaper.setDeadline(request.getDeadline());
        kbExamPaper.setSubmitTime(request.getSubmitTime());
        kbExamPaper.setCostSeconds(request.getCostSeconds());
        kbExamPaper.setCreateBy(request.getCreateBy());
        kbExamPaper.setUpdateBy(request.getUpdateBy());
        kbExamPaper.setRemark(request.getRemark());
        return kbExamPaper;
    }

    /**
     * 更新请求 DTO + 路径主键 → 领域模型。
     *
     * @param request 更新考试试卷请求 DTO；为空返回 null
     * @param id 路径中的考试试卷主键
     * @return 考试试卷领域模型
     */
    public static KbExamPaper toModel(KbExamPaperUpdateRequest request, Long id) {
        if (request == null) {
            return null;
        }
        KbExamPaper kbExamPaper = new KbExamPaper();
        kbExamPaper.setId(id);
        kbExamPaper.setUserId(request.getUserId());
        kbExamPaper.setTitle(request.getTitle());
        kbExamPaper.setMode(request.getMode());
        kbExamPaper.setStatus(request.getStatus());
        kbExamPaper.setPerQuestionSeconds(request.getPerQuestionSeconds());
        kbExamPaper.setTimeLimitSeconds(request.getTimeLimitSeconds());
        kbExamPaper.setQuestionCount(request.getQuestionCount());
        kbExamPaper.setTotalScore(request.getTotalScore());
        kbExamPaper.setScore(request.getScore());
        kbExamPaper.setCorrectCount(request.getCorrectCount());
        kbExamPaper.setWrongCount(request.getWrongCount());
        kbExamPaper.setUnansweredCount(request.getUnansweredCount());
        kbExamPaper.setCategories(request.getCategories());
        kbExamPaper.setStartTime(request.getStartTime());
        kbExamPaper.setDeadline(request.getDeadline());
        kbExamPaper.setSubmitTime(request.getSubmitTime());
        kbExamPaper.setCostSeconds(request.getCostSeconds());
        kbExamPaper.setCreateBy(request.getCreateBy());
        kbExamPaper.setUpdateBy(request.getUpdateBy());
        kbExamPaper.setRemark(request.getRemark());
        return kbExamPaper;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 考试试卷查询请求 DTO；为空返回空查询参数（分页走默认值）
     * @return 考试试卷查询参数
     */
    public static KbExamPaperQueryParam toQueryParam(KbExamPaperQueryRequest request) {
        if (request == null) {
            return new KbExamPaperQueryParam();
        }
        KbExamPaperQueryParam param = new KbExamPaperQueryParam();
        param.setId(request.getId());
        param.setUserId(request.getUserId());
        param.setTitle(request.getTitle());
        param.setMode(request.getMode());
        param.setStatus(request.getStatus());
        param.setPerQuestionSeconds(request.getPerQuestionSeconds());
        param.setTimeLimitSeconds(request.getTimeLimitSeconds());
        param.setQuestionCount(request.getQuestionCount());
        param.setTotalScore(request.getTotalScore());
        param.setScore(request.getScore());
        param.setCorrectCount(request.getCorrectCount());
        param.setWrongCount(request.getWrongCount());
        param.setUnansweredCount(request.getUnansweredCount());
        param.setStartTime(request.getStartTime());
        param.setDeadline(request.getDeadline());
        param.setSubmitTime(request.getSubmitTime());
        param.setCostSeconds(request.getCostSeconds());
        param.setCreateBy(request.getCreateBy());
        param.setUpdateBy(request.getUpdateBy());
        param.setRemark(request.getRemark());
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
     * @param kbExamPaper 考试试卷领域模型；为空返回 null
     * @return 考试试卷响应 VO
     */
    public static KbExamPaperResponse toResponse(KbExamPaper kbExamPaper) {
        if (kbExamPaper == null) {
            return null;
        }
        KbExamPaperResponse response = new KbExamPaperResponse();
        response.setId(kbExamPaper.getId());
        response.setUserId(kbExamPaper.getUserId());
        response.setTitle(kbExamPaper.getTitle());
        response.setMode(kbExamPaper.getMode());
        response.setStatus(kbExamPaper.getStatus());
        response.setPerQuestionSeconds(kbExamPaper.getPerQuestionSeconds());
        response.setTimeLimitSeconds(kbExamPaper.getTimeLimitSeconds());
        response.setQuestionCount(kbExamPaper.getQuestionCount());
        response.setTotalScore(kbExamPaper.getTotalScore());
        response.setScore(kbExamPaper.getScore());
        response.setCorrectCount(kbExamPaper.getCorrectCount());
        response.setWrongCount(kbExamPaper.getWrongCount());
        response.setUnansweredCount(kbExamPaper.getUnansweredCount());
        response.setCategories(kbExamPaper.getCategories());
        response.setStartTime(kbExamPaper.getStartTime());
        response.setDeadline(kbExamPaper.getDeadline());
        response.setSubmitTime(kbExamPaper.getSubmitTime());
        response.setCostSeconds(kbExamPaper.getCostSeconds());
        response.setCreateBy(kbExamPaper.getCreateBy());
        response.setUpdateBy(kbExamPaper.getUpdateBy());
        response.setRemark(kbExamPaper.getRemark());
        response.setCreateTime(kbExamPaper.getCreateTime());
        response.setUpdateTime(kbExamPaper.getUpdateTime());
        return response;
    }
}
