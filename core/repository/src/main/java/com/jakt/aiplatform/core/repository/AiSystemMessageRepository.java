package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.AiSystemMessage;

/**
 * 系统AI会话消息仓储：封装 Mapper，对外只暴露领域模型。
 */
public interface AiSystemMessageRepository {

    /**
     * 新增（能力调用时落库 system/user/assistant 消息）。
     *
     * @param aiSystemMessage 系统AI会话消息
     * @return 新增后的系统AI会话消息（主键已回填）
     */
    AiSystemMessage insert(AiSystemMessage aiSystemMessage);
}
