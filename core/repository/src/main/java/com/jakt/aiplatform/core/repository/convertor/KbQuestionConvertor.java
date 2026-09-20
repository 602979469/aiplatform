package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.KbQuestionDO;
import com.jakt.aiplatform.common.dal.query.KbQuestionDalQuery;
import com.jakt.aiplatform.common.dal.query.KbQuestionPickQuery;
import com.jakt.aiplatform.core.model.domain.KbQuestion;
import com.jakt.aiplatform.core.model.param.KbQuestionPickParam;
import com.jakt.aiplatform.core.model.param.KbQuestionQueryParam;

/**
 * 题库题目 DO/领域模型/查询参数互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）；
 * QueryParam（core-model）→ DalQuery（common-dal）在 Repository 调 Mapper 前完成，common-dal 不依赖 core-model。
 */
public final class KbQuestionConvertor {

    private KbQuestionConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param source 题库题目数据对象；为空返回 null
     * @return 题库题目领域模型
     */
    public static KbQuestion toModel(KbQuestionDO source) {
        if (source == null) {
            return null;
        }
        KbQuestion target = new KbQuestion();
        target.setId(source.getId());
        target.setQuestionType(source.getQuestionType());
        target.setCategory(source.getCategory());
        target.setSubtopic(source.getSubtopic());
        target.setTitle(source.getTitle());
        target.setContent(source.getContent());
        target.setOptions(source.getOptions());
        target.setAnswer(source.getAnswer());
        target.setExplanation(source.getExplanation());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setDifficulty(source.getDifficulty());
        target.setTags(source.getTags());
        target.setSourcePath(source.getSourcePath());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param source 题库题目领域模型；为空返回 null
     * @return 题库题目数据对象
     */
    public static KbQuestionDO toDO(KbQuestion source) {
        if (source == null) {
            return null;
        }
        KbQuestionDO target = new KbQuestionDO();
        target.setId(source.getId());
        target.setQuestionType(source.getQuestionType());
        target.setCategory(source.getCategory());
        target.setSubtopic(source.getSubtopic());
        target.setTitle(source.getTitle());
        target.setContent(source.getContent());
        target.setOptions(source.getOptions());
        target.setAnswer(source.getAnswer());
        target.setExplanation(source.getExplanation());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setDifficulty(source.getDifficulty());
        target.setTags(source.getTags());
        target.setSourcePath(source.getSourcePath());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 查询参数（core-model）→ 查询参数（common-dal）。
     *
     * @param source 题库管理查询参数；为空返回 null
     * @return common-dal 题库查询参数
     */
    public static KbQuestionDalQuery toDalQuery(KbQuestionQueryParam source) {
        if (source == null) {
            return null;
        }
        KbQuestionDalQuery target = new KbQuestionDalQuery();
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setKeyword(source.getKeyword());
        target.setCategory(source.getCategory());
        target.setSubtopic(source.getSubtopic());
        target.setQuestionType(source.getQuestionType());
        target.setDifficulty(source.getDifficulty());
        return target;
    }

    /**
     * 抽题参数（core-model）→ 抽题查询（common-dal）。
     *
     * @param source 组卷抽题参数；为空返回 null
     * @return common-dal 组卷抽题查询
     */
    public static KbQuestionPickQuery toPickQuery(KbQuestionPickParam source) {
        if (source == null) {
            return null;
        }
        KbQuestionPickQuery target = new KbQuestionPickQuery();
        target.setUserId(source.getUserId());
        target.setCategory(source.getCategory());
        target.setSubtopic(source.getSubtopic());
        target.setQuestionType(source.getQuestionType());
        target.setDifficulty(source.getDifficulty());
        target.setExcludeMastered(source.isExcludeMastered());
        target.setLimit(source.getLimit());
        return target;
    }
}
