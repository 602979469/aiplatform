package com.jakt.aiplatform.core.repository.convertor;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.dal.dataobject.AiSystemSessionDO;
import com.jakt.aiplatform.core.model.domain.AiSystemSession;
import com.jakt.aiplatform.common.framework.enums.BaseEnum;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;


/**
 * 系统AI会话 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class AiSystemSessionConvertor {

    private AiSystemSessionConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param aiSystemSessionDO 系统AI会话数据对象；为空返回 null
     * @return 系统AI会话领域模型
     */
    public static AiSystemSession toModel(AiSystemSessionDO source) {
        if (source == null) {
            return null;
        }
        AiSystemSession target = new AiSystemSession();
        target.setSessionId(source.getSessionId());
        target.setCapabilityId(source.getCapabilityId());
        target.setSceneCode(source.getSceneCode());
        target.setCapabilityCode(source.getCapabilityCode());
        target.setSessionName(source.getSessionName());
        target.setStatus(BaseEnum.fromCode(EnableStatusEnum.class, source.getStatus()));
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param aiSystemSession 系统AI会话领域模型
     * @return 系统AI会话数据对象
     */
    public static AiSystemSessionDO toDO(AiSystemSession source) {
        if (source == null) {
            return null;
        }
        AiSystemSessionDO target = new AiSystemSessionDO();
        target.setSessionId(source.getSessionId());
        target.setCapabilityId(source.getCapabilityId());
        target.setSceneCode(source.getSceneCode());
        target.setCapabilityCode(source.getCapabilityCode());
        target.setSessionName(source.getSessionName());
        target.setStatus(ObjectUtil.isNull(source.getStatus()) ? null : source.getStatus().getCode());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
