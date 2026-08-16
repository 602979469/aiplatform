package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.AiChatSession;
import com.jakt.aiplatform.core.model.param.AiChatSessionQueryParam;

import java.util.List;

/**
 * AI会话表领域服务
 *
 * 实现类为 AiChatSessionServiceImpl（core.service.impl 包）。
 */
public interface AiChatSessionService {

    /**
     * 创建AI会话表
     *
     * @param aiChatSession AI会话表
     * @return 创建后的AI会话表（主键已回填）
     */
    AiChatSession createAiChatSession(AiChatSession aiChatSession);

    /**
     * 按条件更新AI会话表（只更新传入的非空字段，改标题等场景）。
     *
     * @param aiChatSession AI会话表（至少含主键）
     */
    void updateByCondition(AiChatSession aiChatSession);

    /**
     * 删除AI会话表（连同消息，事务内执行）
     *
     * @param id AI会话表 ID
     */
    void deleteAiChatSession(Long id);

    /**
     * 按 ID 获取AI会话表
     *
     * @param id AI会话表 ID
     * @return AI会话表
     */
    AiChatSession getAiChatSession(Long id);

    /**
     * 按条件列表查询（当前用户会话列表）
     *
     * @param query 查询参数
     * @return AI会话表列表
     */
    List<AiChatSession> findList(AiChatSessionQueryParam query);
}
