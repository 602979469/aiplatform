package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.AiChatSessionDO;
import com.jakt.aiplatform.core.model.domain.AiChatSession;


/**
 * 用户AI会话 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class AiChatSessionConvertor {

    private AiChatSessionConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param aiChatSessionDO 用户AI会话数据对象；为空返回 null
     * @return 用户AI会话领域模型
     */
    public static AiChatSession toModel(AiChatSessionDO source) {
        if (source == null) {
            return null;
        }
        AiChatSession target = new AiChatSession();
        target.setSessionId(source.getSessionId());
        target.setSessionName(source.getSessionName());
        target.setUserId(source.getUserId());
        target.setUserName(source.getUserName());
        target.setStatus(source.getStatus());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param aiChatSession 用户AI会话领域模型
     * @return 用户AI会话数据对象
     */
    public static AiChatSessionDO toDO(AiChatSession source) {
        AiChatSessionDO target = new AiChatSessionDO();
        target.setSessionId(source.getSessionId());
        target.setSessionName(source.getSessionName());
        target.setUserId(source.getUserId());
        target.setUserName(source.getUserName());
        target.setStatus(source.getStatus());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
