package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.AiSystemMessageDO;
import com.jakt.aiplatform.core.model.domain.AiSystemMessage;


/**
 * 系统AI会话消息 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class AiSystemMessageConvertor {

    private AiSystemMessageConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param aiSystemMessageDO 系统AI会话消息数据对象；为空返回 null
     * @return 系统AI会话消息领域模型
     */
    public static AiSystemMessage toModel(AiSystemMessageDO source) {
        if (source == null) {
            return null;
        }
        AiSystemMessage target = new AiSystemMessage();
        target.setMessageId(source.getMessageId());
        target.setSessionId(source.getSessionId());
        target.setRole(source.getRole());
        target.setContent(source.getContent());
        target.setStatus(source.getStatus());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param aiSystemMessage 系统AI会话消息领域模型
     * @return 系统AI会话消息数据对象
     */
    public static AiSystemMessageDO toDO(AiSystemMessage source) {
        AiSystemMessageDO target = new AiSystemMessageDO();
        target.setMessageId(source.getMessageId());
        target.setSessionId(source.getSessionId());
        target.setRole(source.getRole());
        target.setContent(source.getContent());
        target.setStatus(source.getStatus());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
