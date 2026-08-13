package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.AiSystemSession;

/**
 * 系统AI会话领域服务（内部机制）。
 */
public interface AiSystemSessionService {

    /**
     * 创建系统AI会话（能力调用时创建独立上下文）。
     *
     * @param aiSystemSession 系统AI会话
     * @return 创建后的系统AI会话（主键已回填）
     */
    AiSystemSession createAiSystemSession(AiSystemSession aiSystemSession);
}
