package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.AiCapabilityDO;
import com.jakt.aiplatform.core.model.domain.AiCapability;
import com.jakt.aiplatform.core.model.enums.BaseEnum;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;


/**
 * AI能力 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class AiCapabilityConvertor {

    private AiCapabilityConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param aiCapabilityDO AI能力数据对象；为空返回 null
     * @return AI能力领域模型
     */
    public static AiCapability toModel(AiCapabilityDO source) {
        if (source == null) {
            return null;
        }
        AiCapability target = new AiCapability();
        target.setCapabilityId(source.getCapabilityId());
        target.setSceneCode(source.getSceneCode());
        target.setCapabilityCode(source.getCapabilityCode());
        target.setCapabilityName(source.getCapabilityName());
        target.setDescription(source.getDescription());
        target.setSkillRules(source.getSkillRules());
        target.setStatus(BaseEnum.fromCode(EnableStatusEnum.class, source.getStatus()));
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param aiCapability AI能力领域模型
     * @return AI能力数据对象
     */
    public static AiCapabilityDO toDO(AiCapability source) {
        if (source == null) {
            return null;
        }
        AiCapabilityDO target = new AiCapabilityDO();
        target.setCapabilityId(source.getCapabilityId());
        target.setSceneCode(source.getSceneCode());
        target.setCapabilityCode(source.getCapabilityCode());
        target.setCapabilityName(source.getCapabilityName());
        target.setDescription(source.getDescription());
        target.setSkillRules(source.getSkillRules());
        target.setStatus(source.getStatus() == null ? null : source.getStatus().getCode());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
