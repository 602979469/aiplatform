package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.KbExamTemplateRuleDO;
import com.jakt.aiplatform.common.dal.query.KbExamTemplateRuleDalQuery;
import com.jakt.aiplatform.core.model.domain.KbExamTemplateRule;
import com.jakt.aiplatform.core.model.param.KbExamTemplateRuleQueryParam;


/**
 * 试卷模板知识点规则 DO/领域模型/查询参数互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）；
 * QueryParam（core-model）→ DalQuery（common-dal）在 Repository 调 Mapper 前完成，common-dal 不依赖 core-model。
 */
public final class KbExamTemplateRuleConvertor {

    private KbExamTemplateRuleConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param kbExamTemplateRuleDO 试卷模板知识点规则数据对象；为空返回 null
     * @return 试卷模板知识点规则领域模型
     */
    public static KbExamTemplateRule toModel(KbExamTemplateRuleDO source) {
        if (source == null) {
            return null;
        }
        KbExamTemplateRule target = new KbExamTemplateRule();
        target.setId(source.getId());
        target.setTemplateId(source.getTemplateId());
        target.setCategory(source.getCategory());
        target.setSubtopic(source.getSubtopic());
        target.setQuestionType(source.getQuestionType());
        target.setDifficulty(source.getDifficulty());
        target.setQuestionCount(source.getQuestionCount());
        target.setOrderNum(source.getOrderNum());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param kbExamTemplateRule 试卷模板知识点规则领域模型
     * @return 试卷模板知识点规则数据对象
     */
    public static KbExamTemplateRuleDO toDO(KbExamTemplateRule source) {
        KbExamTemplateRuleDO target = new KbExamTemplateRuleDO();
        target.setId(source.getId());
        target.setTemplateId(source.getTemplateId());
        target.setCategory(source.getCategory());
        target.setSubtopic(source.getSubtopic());
        target.setQuestionType(source.getQuestionType());
        target.setDifficulty(source.getDifficulty());
        target.setQuestionCount(source.getQuestionCount());
        target.setOrderNum(source.getOrderNum());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 查询参数 → common-dal 查询参数。
     *
     * @param source 试卷模板知识点规则查询参数；为空返回空对象
     * @return 试卷模板知识点规则查询参数（common-dal）
     */
    public static KbExamTemplateRuleDalQuery toDalQuery(KbExamTemplateRuleQueryParam source) {
        KbExamTemplateRuleDalQuery target = new KbExamTemplateRuleDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setId(source.getId());
        target.setTemplateId(source.getTemplateId());
        target.setCategory(source.getCategory());
        target.setSubtopic(source.getSubtopic());
        target.setQuestionType(source.getQuestionType());
        target.setDifficulty(source.getDifficulty());
        target.setQuestionCount(source.getQuestionCount());
        target.setOrderNum(source.getOrderNum());
        target.setCreateTimeBegin(source.getCreateTimeBegin());
        target.setCreateTimeEnd(source.getCreateTimeEnd());
        target.setUpdateTimeBegin(source.getUpdateTimeBegin());
        target.setUpdateTimeEnd(source.getUpdateTimeEnd());
        return target;
    }
}
