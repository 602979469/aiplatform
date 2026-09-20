package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.KbUserQuestionStatDO;
import com.jakt.aiplatform.common.dal.query.KbQuestionStatDelta;
import com.jakt.aiplatform.common.dal.query.KbUserQuestionStatDalQuery;
import com.jakt.aiplatform.core.model.domain.KbUserQuestionStat;
import com.jakt.aiplatform.core.model.param.KbUserQuestionStatDelta;
import com.jakt.aiplatform.core.model.param.KbUserQuestionStatQueryParam;


/**
 * 用户题目掌握状态 DO/领域模型/查询参数互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）；
 * QueryParam（core-model）→ DalQuery（common-dal）在 Repository 调 Mapper 前完成，common-dal 不依赖 core-model。
 */
public final class KbUserQuestionStatConvertor {

    private KbUserQuestionStatConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param kbUserQuestionStatDO 用户题目掌握状态数据对象；为空返回 null
     * @return 用户题目掌握状态领域模型
     */
    public static KbUserQuestionStat toModel(KbUserQuestionStatDO source) {
        if (source == null) {
            return null;
        }
        KbUserQuestionStat target = new KbUserQuestionStat();
        target.setId(source.getId());
        target.setUserId(source.getUserId());
        target.setQuestionId(source.getQuestionId());
        target.setRightCount(source.getRightCount());
        target.setWrongCount(source.getWrongCount());
        target.setLastResult(source.getLastResult());
        target.setLastAnswerTime(source.getLastAnswerTime());
        target.setFirstRightTime(source.getFirstRightTime());
        target.setMastered(source.getMastered());
        target.setInWrongBook(source.getInWrongBook());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param kbUserQuestionStat 用户题目掌握状态领域模型
     * @return 用户题目掌握状态数据对象
     */
    public static KbUserQuestionStatDO toDO(KbUserQuestionStat source) {
        KbUserQuestionStatDO target = new KbUserQuestionStatDO();
        target.setId(source.getId());
        target.setUserId(source.getUserId());
        target.setQuestionId(source.getQuestionId());
        target.setRightCount(source.getRightCount());
        target.setWrongCount(source.getWrongCount());
        target.setLastResult(source.getLastResult());
        target.setLastAnswerTime(source.getLastAnswerTime());
        target.setFirstRightTime(source.getFirstRightTime());
        target.setMastered(source.getMastered());
        target.setInWrongBook(source.getInWrongBook());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 查询参数 → common-dal 查询参数。
     *
     * @param source 用户题目掌握状态查询参数；为空返回空对象
     * @return 用户题目掌握状态查询参数（common-dal）
     */
    public static KbUserQuestionStatDalQuery toDalQuery(KbUserQuestionStatQueryParam source) {
        KbUserQuestionStatDalQuery target = new KbUserQuestionStatDalQuery();
        if (source == null) {
            return target;
        }
        target.setPageNum(source.getPageNum());
        target.setPageSize(source.getPageSize());
        target.setId(source.getId());
        target.setUserId(source.getUserId());
        target.setQuestionId(source.getQuestionId());
        target.setRightCount(source.getRightCount());
        target.setWrongCount(source.getWrongCount());
        target.setLastResult(source.getLastResult());
        target.setLastAnswerTime(source.getLastAnswerTime());
        target.setFirstRightTime(source.getFirstRightTime());
        target.setMastered(source.getMastered());
        target.setInWrongBook(source.getInWrongBook());
        target.setCreateTimeBegin(source.getCreateTimeBegin());
        target.setCreateTimeEnd(source.getCreateTimeEnd());
        target.setUpdateTimeBegin(source.getUpdateTimeBegin());
        target.setUpdateTimeEnd(source.getUpdateTimeEnd());
        return target;
    }

    /**
     * 掌握度增量（core-model）→ 掌握度增量（common-dal）。
     *
     * @param source 掌握度增量；为空返回 null
     * @return common-dal 掌握度增量
     */
    public static KbQuestionStatDelta toDelta(KbUserQuestionStatDelta source) {
        if (source == null) {
            return null;
        }
        KbQuestionStatDelta target = new KbQuestionStatDelta();
        target.setUserId(source.getUserId());
        target.setQuestionId(source.getQuestionId());
        target.setRightDelta(source.getRightDelta() == null ? 0 : source.getRightDelta());
        target.setWrongDelta(source.getWrongDelta() == null ? 0 : source.getWrongDelta());
        target.setLastResult(source.getLastResult());
        target.setMastered(source.getMastered());
        target.setInWrongBook(source.getInWrongBook());
        return target;
    }
}
