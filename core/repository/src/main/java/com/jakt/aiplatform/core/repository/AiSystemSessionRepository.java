package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.AiSystemSession;

/**
 * 系统AI会话仓储：封装 Mapper，对外只暴露领域模型。
 */
public interface AiSystemSessionRepository {

    /**
     * 新增（能力调用时创建独立上下文会话）。
     *
     * @param aiSystemSession 系统AI会话
     * @return 新增后的系统AI会话（主键已回填）
     */
    AiSystemSession insert(AiSystemSession aiSystemSession);
}
