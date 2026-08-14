package com.jakt.aiplatform.core.repository.convertor;

import com.jakt.aiplatform.common.dal.dataobject.AiChatMessageDO;
import com.jakt.aiplatform.core.model.domain.AiChatMessage;
import com.jakt.aiplatform.core.model.enums.AiChatMessageStatusEnum;
import com.jakt.aiplatform.core.model.enums.BaseEnum;


/**
 * 用户AI会话消息 DO 与领域模型互转，只存在于 repository。
 * 显式 get/set 赋值：DO 保持数据库原始类型，Model 按列级配置转换（枚举 / json / 强制类型）。
 */
public final class AiChatMessageConvertor {

    private AiChatMessageConvertor() {
    }

    /**
     * DO → 领域模型。
     *
     * @param aiChatMessageDO 用户AI会话消息数据对象；为空返回 null
     * @return 用户AI会话消息领域模型
     */
    public static AiChatMessage toModel(AiChatMessageDO source) {
        if (source == null) {
            return null;
        }
        AiChatMessage target = new AiChatMessage();
        target.setMessageId(source.getMessageId());
        target.setSessionId(source.getSessionId());
        target.setUserId(source.getUserId());
        target.setRole(source.getRole());
        target.setContent(source.getContent());
        target.setStatus(BaseEnum.fromCode(AiChatMessageStatusEnum.class, source.getStatus()));
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 领域模型 → DO。
     *
     * @param aiChatMessage 用户AI会话消息领域模型
     * @return 用户AI会话消息数据对象
     */
    public static AiChatMessageDO toDO(AiChatMessage source) {
        if (source == null) {
            return null;
        }
        AiChatMessageDO target = new AiChatMessageDO();
        target.setMessageId(source.getMessageId());
        target.setSessionId(source.getSessionId());
        target.setUserId(source.getUserId());
        target.setRole(source.getRole());
        target.setContent(source.getContent());
        target.setStatus(source.getStatus() == null ? null : source.getStatus().getCode());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }
}
