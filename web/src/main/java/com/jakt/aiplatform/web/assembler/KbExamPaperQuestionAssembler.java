package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.constant.PageConstants;
import com.jakt.aiplatform.core.model.domain.KbExamPaperQuestion;
import com.jakt.aiplatform.core.model.param.KbExamPaperQuestionQueryParam;
import com.jakt.aiplatform.web.param.KbExamPaperQuestionCreateRequest;
import com.jakt.aiplatform.web.param.KbExamPaperQuestionQueryRequest;
import com.jakt.aiplatform.web.param.KbExamPaperQuestionUpdateRequest;
import com.jakt.aiplatform.web.result.KbExamPaperQuestionResponse;

/**
 * 试卷题目快照与作答对象组装器：DTO 与领域模型互转，只存在于 web。
 */
public final class KbExamPaperQuestionAssembler {

    private KbExamPaperQuestionAssembler() {
    }

    /**
     * 创建请求 DTO → 领域模型。
     *
     * @param request 创建试卷题目快照与作答请求 DTO；为空返回 null
     * @return 试卷题目快照与作答领域模型
     */
    public static KbExamPaperQuestion toModel(KbExamPaperQuestionCreateRequest request) {
        if (request == null) {
            return null;
        }
        KbExamPaperQuestion kbExamPaperQuestion = new KbExamPaperQuestion();
        kbExamPaperQuestion.setPaperId(request.getPaperId());
        kbExamPaperQuestion.setQuestionId(request.getQuestionId());
        kbExamPaperQuestion.setSeq(request.getSeq());
        kbExamPaperQuestion.setQuestionType(request.getQuestionType());
        kbExamPaperQuestion.setCategory(request.getCategory());
        kbExamPaperQuestion.setSubtopic(request.getSubtopic());
        kbExamPaperQuestion.setTitle(request.getTitle());
        kbExamPaperQuestion.setContent(request.getContent());
        kbExamPaperQuestion.setOptions(request.getOptions());
        kbExamPaperQuestion.setAnswer(request.getAnswer());
        kbExamPaperQuestion.setExplanation(request.getExplanation());
        kbExamPaperQuestion.setDifficulty(request.getDifficulty());
        kbExamPaperQuestion.setScore(request.getScore());
        kbExamPaperQuestion.setUserAnswer(request.getUserAnswer());
        kbExamPaperQuestion.setIsCorrect(request.getIsCorrect());
        kbExamPaperQuestion.setAnswerCostSeconds(request.getAnswerCostSeconds());
        kbExamPaperQuestion.setAnswerTime(request.getAnswerTime());
        return kbExamPaperQuestion;
    }

    /**
     * 更新请求 DTO + 路径主键 → 领域模型。
     *
     * @param request 更新试卷题目快照与作答请求 DTO；为空返回 null
     * @param id 路径中的试卷题目快照与作答主键
     * @return 试卷题目快照与作答领域模型
     */
    public static KbExamPaperQuestion toModel(KbExamPaperQuestionUpdateRequest request, Long id) {
        if (request == null) {
            return null;
        }
        KbExamPaperQuestion kbExamPaperQuestion = new KbExamPaperQuestion();
        kbExamPaperQuestion.setId(id);
        kbExamPaperQuestion.setPaperId(request.getPaperId());
        kbExamPaperQuestion.setQuestionId(request.getQuestionId());
        kbExamPaperQuestion.setSeq(request.getSeq());
        kbExamPaperQuestion.setQuestionType(request.getQuestionType());
        kbExamPaperQuestion.setCategory(request.getCategory());
        kbExamPaperQuestion.setSubtopic(request.getSubtopic());
        kbExamPaperQuestion.setTitle(request.getTitle());
        kbExamPaperQuestion.setContent(request.getContent());
        kbExamPaperQuestion.setOptions(request.getOptions());
        kbExamPaperQuestion.setAnswer(request.getAnswer());
        kbExamPaperQuestion.setExplanation(request.getExplanation());
        kbExamPaperQuestion.setDifficulty(request.getDifficulty());
        kbExamPaperQuestion.setScore(request.getScore());
        kbExamPaperQuestion.setUserAnswer(request.getUserAnswer());
        kbExamPaperQuestion.setIsCorrect(request.getIsCorrect());
        kbExamPaperQuestion.setAnswerCostSeconds(request.getAnswerCostSeconds());
        kbExamPaperQuestion.setAnswerTime(request.getAnswerTime());
        return kbExamPaperQuestion;
    }

    /**
     * 查询请求 DTO → 查询参数。
     *
     * @param request 试卷题目快照与作答查询请求 DTO；为空返回空查询参数（分页走默认值）
     * @return 试卷题目快照与作答查询参数
     */
    public static KbExamPaperQuestionQueryParam toQueryParam(KbExamPaperQuestionQueryRequest request) {
        if (request == null) {
            return new KbExamPaperQuestionQueryParam();
        }
        KbExamPaperQuestionQueryParam param = new KbExamPaperQuestionQueryParam();
        param.setId(request.getId());
        param.setPaperId(request.getPaperId());
        param.setQuestionId(request.getQuestionId());
        param.setSeq(request.getSeq());
        param.setQuestionType(request.getQuestionType());
        param.setCategory(request.getCategory());
        param.setSubtopic(request.getSubtopic());
        param.setTitle(request.getTitle());
        param.setContent(request.getContent());
        param.setAnswer(request.getAnswer());
        param.setExplanation(request.getExplanation());
        param.setDifficulty(request.getDifficulty());
        param.setScore(request.getScore());
        param.setUserAnswer(request.getUserAnswer());
        param.setIsCorrect(request.getIsCorrect());
        param.setAnswerCostSeconds(request.getAnswerCostSeconds());
        param.setAnswerTime(request.getAnswerTime());
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
     * @param kbExamPaperQuestion 试卷题目快照与作答领域模型；为空返回 null
     * @return 试卷题目快照与作答响应 VO
     */
    public static KbExamPaperQuestionResponse toResponse(KbExamPaperQuestion kbExamPaperQuestion) {
        if (kbExamPaperQuestion == null) {
            return null;
        }
        KbExamPaperQuestionResponse response = new KbExamPaperQuestionResponse();
        response.setId(kbExamPaperQuestion.getId());
        response.setPaperId(kbExamPaperQuestion.getPaperId());
        response.setQuestionId(kbExamPaperQuestion.getQuestionId());
        response.setSeq(kbExamPaperQuestion.getSeq());
        response.setQuestionType(kbExamPaperQuestion.getQuestionType());
        response.setCategory(kbExamPaperQuestion.getCategory());
        response.setSubtopic(kbExamPaperQuestion.getSubtopic());
        response.setTitle(kbExamPaperQuestion.getTitle());
        response.setContent(kbExamPaperQuestion.getContent());
        response.setOptions(kbExamPaperQuestion.getOptions());
        response.setAnswer(kbExamPaperQuestion.getAnswer());
        response.setExplanation(kbExamPaperQuestion.getExplanation());
        response.setDifficulty(kbExamPaperQuestion.getDifficulty());
        response.setScore(kbExamPaperQuestion.getScore());
        response.setUserAnswer(kbExamPaperQuestion.getUserAnswer());
        response.setIsCorrect(kbExamPaperQuestion.getIsCorrect());
        response.setAnswerCostSeconds(kbExamPaperQuestion.getAnswerCostSeconds());
        response.setAnswerTime(kbExamPaperQuestion.getAnswerTime());
        response.setCreateTime(kbExamPaperQuestion.getCreateTime());
        response.setUpdateTime(kbExamPaperQuestion.getUpdateTime());
        return response;
    }
}
