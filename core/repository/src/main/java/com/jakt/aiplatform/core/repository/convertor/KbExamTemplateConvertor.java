package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.KbExamTemplateDO;
import com.jakt.aiplatform.common.dal.query.KbExamTemplateDalQuery;
import com.jakt.aiplatform.core.model.domain.KbExamTemplate;
import com.jakt.aiplatform.core.model.param.KbExamTemplateQueryParam;


/**
 * 试卷模板 DO/领域模型/查询参数互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）；
 * QueryParam（core-model）→ DalQuery（common-dal）在 Repository 调 Mapper 前完成，common-dal 不依赖 core-model。
 */
public final class KbExamTemplateConvertor {

    private KbExamTemplateConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param kbExamTemplateDO 试卷模板数据对象；为空返回 null
     * @return 试卷模板领域模型
     */
    public static KbExamTemplate toModel(KbExamTemplateDO source) {
        if (source == null) {
            return null;
        }
        KbExamTemplate target = new KbExamTemplate();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setScope(source.getScope());
        target.setOwnerUserId(source.getOwnerUserId());
        target.setStatus(source.getStatus());
        target.setMode(source.getMode());
        target.setQuestionCount(source.getQuestionCount());
        target.setPerQuestionSeconds(source.getPerQuestionSeconds());
        target.setObjectiveOnly(source.getObjectiveOnly());
        target.setExcludeMastered(source.getExcludeMastered());
        target.setTypeMix(source.getTypeMix());
        target.setDifficultyMix(source.getDifficultyMix());
        target.setUseCount(source.getUseCount());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param kbExamTemplate 试卷模板领域模型
     * @return 试卷模板数据对象
     */
    public static KbExamTemplateDO toDO(KbExamTemplate source) {
        KbExamTemplateDO target = new KbExamTemplateDO();
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setScope(source.getScope());
        target.setOwnerUserId(source.getOwnerUserId());
        target.setStatus(source.getStatus());
        target.setMode(source.getMode());
        target.setQuestionCount(source.getQuestionCount());
        target.setPerQuestionSeconds(source.getPerQuestionSeconds());
        target.setObjectiveOnly(source.getObjectiveOnly());
        target.setExcludeMastered(source.getExcludeMastered());
        target.setTypeMix(source.getTypeMix());
        target.setDifficultyMix(source.getDifficultyMix());
        target.setUseCount(source.getUseCount());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 查询参数 → common-dal 查询参数。
     *
     * @param source 试卷模板查询参数；为空返回空对象
     * @return 试卷模板查询参数（common-dal）
     */
    public static KbExamTemplateDalQuery toDalQuery(KbExamTemplateQueryParam source) {
        KbExamTemplateDalQuery target = new KbExamTemplateDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setId(source.getId());
        target.setName(source.getName());
        target.setDescription(source.getDescription());
        target.setScope(source.getScope());
        target.setOwnerUserId(source.getOwnerUserId());
        target.setStatus(source.getStatus());
        target.setMode(source.getMode());
        target.setQuestionCount(source.getQuestionCount());
        target.setPerQuestionSeconds(source.getPerQuestionSeconds());
        target.setObjectiveOnly(source.getObjectiveOnly());
        target.setExcludeMastered(source.getExcludeMastered());
        target.setUseCount(source.getUseCount());
        target.setCreateBy(source.getCreateBy());
        target.setUpdateBy(source.getUpdateBy());
        target.setCreateTimeBegin(source.getCreateTimeBegin());
        target.setCreateTimeEnd(source.getCreateTimeEnd());
        target.setUpdateTimeBegin(source.getUpdateTimeBegin());
        target.setUpdateTimeEnd(source.getUpdateTimeEnd());
        return target;
    }
}
