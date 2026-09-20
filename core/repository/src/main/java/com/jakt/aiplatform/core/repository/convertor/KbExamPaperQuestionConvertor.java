package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.KbExamPaperQuestionDO;
import com.jakt.aiplatform.common.dal.query.KbExamPaperQuestionDalQuery;
import com.jakt.aiplatform.core.model.domain.KbExamPaperQuestion;
import com.jakt.aiplatform.core.model.param.KbExamPaperQuestionQueryParam;


/**
 * 试卷题目快照与作答 DO/领域模型/查询参数互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）；
 * QueryParam（core-model）→ DalQuery（common-dal）在 Repository 调 Mapper 前完成，common-dal 不依赖 core-model。
 */
public final class KbExamPaperQuestionConvertor {

    private KbExamPaperQuestionConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param kbExamPaperQuestionDO 试卷题目快照与作答数据对象；为空返回 null
     * @return 试卷题目快照与作答领域模型
     */
    public static KbExamPaperQuestion toModel(KbExamPaperQuestionDO source) {
        if (source == null) {
            return null;
        }
        KbExamPaperQuestion target = new KbExamPaperQuestion();
        target.setId(source.getId());
        target.setPaperId(source.getPaperId());
        target.setQuestionId(source.getQuestionId());
        target.setSeq(source.getSeq());
        target.setQuestionType(source.getQuestionType());
        target.setCategory(source.getCategory());
        target.setSubtopic(source.getSubtopic());
        target.setTitle(source.getTitle());
        target.setContent(source.getContent());
        target.setOptions(source.getOptions());
        target.setAnswer(source.getAnswer());
        target.setExplanation(source.getExplanation());
        target.setDifficulty(source.getDifficulty());
        target.setScore(source.getScore());
        target.setActualScore(source.getActualScore());
        target.setAiComment(source.getAiComment());
        target.setUserAnswer(source.getUserAnswer());
        target.setIsCorrect(source.getIsCorrect());
        target.setAnswerCostSeconds(source.getAnswerCostSeconds());
        target.setAnswerTime(source.getAnswerTime());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param kbExamPaperQuestion 试卷题目快照与作答领域模型
     * @return 试卷题目快照与作答数据对象
     */
    public static KbExamPaperQuestionDO toDO(KbExamPaperQuestion source) {
        KbExamPaperQuestionDO target = new KbExamPaperQuestionDO();
        target.setId(source.getId());
        target.setPaperId(source.getPaperId());
        target.setQuestionId(source.getQuestionId());
        target.setSeq(source.getSeq());
        target.setQuestionType(source.getQuestionType());
        target.setCategory(source.getCategory());
        target.setSubtopic(source.getSubtopic());
        target.setTitle(source.getTitle());
        target.setContent(source.getContent());
        target.setOptions(source.getOptions());
        target.setAnswer(source.getAnswer());
        target.setExplanation(source.getExplanation());
        target.setDifficulty(source.getDifficulty());
        target.setScore(source.getScore());
        target.setActualScore(source.getActualScore());
        target.setAiComment(source.getAiComment());
        target.setUserAnswer(source.getUserAnswer());
        target.setIsCorrect(source.getIsCorrect());
        target.setAnswerCostSeconds(source.getAnswerCostSeconds());
        target.setAnswerTime(source.getAnswerTime());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 查询参数 → common-dal 查询参数。
     *
     * @param source 试卷题目快照与作答查询参数；为空返回空对象
     * @return 试卷题目快照与作答查询参数（common-dal）
     */
    public static KbExamPaperQuestionDalQuery toDalQuery(KbExamPaperQuestionQueryParam source) {
        KbExamPaperQuestionDalQuery target = new KbExamPaperQuestionDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setId(source.getId());
        target.setPaperId(source.getPaperId());
        target.setQuestionId(source.getQuestionId());
        target.setSeq(source.getSeq());
        target.setQuestionType(source.getQuestionType());
        target.setCategory(source.getCategory());
        target.setSubtopic(source.getSubtopic());
        target.setTitle(source.getTitle());
        target.setContent(source.getContent());
        target.setAnswer(source.getAnswer());
        target.setExplanation(source.getExplanation());
        target.setDifficulty(source.getDifficulty());
        target.setScore(source.getScore());
        target.setUserAnswer(source.getUserAnswer());
        target.setIsCorrect(source.getIsCorrect());
        target.setAnswerCostSeconds(source.getAnswerCostSeconds());
        target.setAnswerTime(source.getAnswerTime());
        target.setCreateTimeBegin(source.getCreateTimeBegin());
        target.setCreateTimeEnd(source.getCreateTimeEnd());
        target.setUpdateTimeBegin(source.getUpdateTimeBegin());
        target.setUpdateTimeEnd(source.getUpdateTimeEnd());
        return target;
    }
}
