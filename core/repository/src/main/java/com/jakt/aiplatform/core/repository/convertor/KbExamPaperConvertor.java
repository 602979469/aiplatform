package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.KbExamPaperDO;
import com.jakt.aiplatform.common.dal.query.KbExamPaperDalQuery;
import com.jakt.aiplatform.core.model.domain.KbExamPaper;
import com.jakt.aiplatform.core.model.param.KbExamPaperQueryParam;


/**
 * 考试试卷 DO/领域模型/查询参数互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）；
 * QueryParam（core-model）→ DalQuery（common-dal）在 Repository 调 Mapper 前完成，common-dal 不依赖 core-model。
 */
public final class KbExamPaperConvertor {

    private KbExamPaperConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param kbExamPaperDO 考试试卷数据对象；为空返回 null
     * @return 考试试卷领域模型
     */
    public static KbExamPaper toModel(KbExamPaperDO source) {
        if (source == null) {
            return null;
        }
        KbExamPaper target = new KbExamPaper();
        target.setId(source.getId());
        target.setUserId(source.getUserId());
        target.setTitle(source.getTitle());
        target.setMode(source.getMode());
        target.setStatus(source.getStatus());
        target.setPerQuestionSeconds(source.getPerQuestionSeconds());
        target.setTimeLimitSeconds(source.getTimeLimitSeconds());
        target.setQuestionCount(source.getQuestionCount());
        target.setTotalScore(source.getTotalScore());
        target.setScore(source.getScore());
        target.setCorrectCount(source.getCorrectCount());
        target.setWrongCount(source.getWrongCount());
        target.setUnansweredCount(source.getUnansweredCount());
        target.setCategories(source.getCategories());
        target.setStartTime(source.getStartTime());
        target.setDeadline(source.getDeadline());
        target.setSubmitTime(source.getSubmitTime());
        target.setCostSeconds(source.getCostSeconds());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param kbExamPaper 考试试卷领域模型
     * @return 考试试卷数据对象
     */
    public static KbExamPaperDO toDO(KbExamPaper source) {
        KbExamPaperDO target = new KbExamPaperDO();
        target.setId(source.getId());
        target.setUserId(source.getUserId());
        target.setTitle(source.getTitle());
        target.setMode(source.getMode());
        target.setStatus(source.getStatus());
        target.setPerQuestionSeconds(source.getPerQuestionSeconds());
        target.setTimeLimitSeconds(source.getTimeLimitSeconds());
        target.setQuestionCount(source.getQuestionCount());
        target.setTotalScore(source.getTotalScore());
        target.setScore(source.getScore());
        target.setCorrectCount(source.getCorrectCount());
        target.setWrongCount(source.getWrongCount());
        target.setUnansweredCount(source.getUnansweredCount());
        target.setCategories(source.getCategories());
        target.setStartTime(source.getStartTime());
        target.setDeadline(source.getDeadline());
        target.setSubmitTime(source.getSubmitTime());
        target.setCostSeconds(source.getCostSeconds());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 查询参数 → common-dal 查询参数。
     *
     * @param source 考试试卷查询参数；为空返回空对象
     * @return 考试试卷查询参数（common-dal）
     */
    public static KbExamPaperDalQuery toDalQuery(KbExamPaperQueryParam source) {
        KbExamPaperDalQuery target = new KbExamPaperDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setId(source.getId());
        target.setUserId(source.getUserId());
        target.setTitle(source.getTitle());
        target.setMode(source.getMode());
        target.setStatus(source.getStatus());
        target.setPerQuestionSeconds(source.getPerQuestionSeconds());
        target.setTimeLimitSeconds(source.getTimeLimitSeconds());
        target.setQuestionCount(source.getQuestionCount());
        target.setTotalScore(source.getTotalScore());
        target.setScore(source.getScore());
        target.setCorrectCount(source.getCorrectCount());
        target.setWrongCount(source.getWrongCount());
        target.setUnansweredCount(source.getUnansweredCount());
        target.setStartTime(source.getStartTime());
        target.setDeadline(source.getDeadline());
        target.setSubmitTime(source.getSubmitTime());
        target.setCostSeconds(source.getCostSeconds());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setRemark(source.getRemark());
        target.setCreateTimeBegin(source.getCreateTimeBegin());
        target.setCreateTimeEnd(source.getCreateTimeEnd());
        target.setUpdateTimeBegin(source.getUpdateTimeBegin());
        target.setUpdateTimeEnd(source.getUpdateTimeEnd());
        return target;
    }
}
