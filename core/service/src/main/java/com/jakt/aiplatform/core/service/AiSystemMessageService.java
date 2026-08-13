package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.AiSystemMessage;

/**
 * 系统AI会话消息领域服务（内部机制）。
 */
public interface AiSystemMessageService {

    /**
     * 创建系统AI会话消息（能力调用时落库 system/user/assistant 消息）。
     *
     * @param aiSystemMessage 系统AI会话消息
     * @return 创建后的系统AI会话消息（主键已回填）
     */
    AiSystemMessage createAiSystemMessage(AiSystemMessage aiSystemMessage);
}
