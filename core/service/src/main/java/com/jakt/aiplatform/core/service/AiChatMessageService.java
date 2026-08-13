package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.AiChatMessage;

import java.util.List;

/**
 * AI会话消息表领域服务
 *
 * 实现类为 AiChatMessageServiceImpl（core.service.impl 包）。
 */
public interface AiChatMessageService {

    /**
     * 创建AI会话消息表
     *
     * @param aiChatMessage AI会话消息表
     * @return 创建后的AI会话消息表（主键已回填）
     */
    AiChatMessage createAiChatMessage(AiChatMessage aiChatMessage);

    /**
     * 按 ID 获取AI会话消息表
     *
     * @param id AI会话消息表 ID
     * @return AI会话消息表
     */
    AiChatMessage getAiChatMessage(Long id);

    /**
     * 更新消息状态（失败重试标记：0正常 1失败）。
     *
     * @param messageId 消息ID
     * @param status    目标状态
     */
    void updateStatus(Long messageId, String status);

    /**
     * 按会话逻辑删除全部消息。
     *
     * @param sessionId 会话ID
     */
    void deleteBySessionId(Long sessionId);

    /**
     * 按会话查询全部消息（时间正序，聊天记录/上下文组装用）。
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<AiChatMessage> findBySessionAsc(Long sessionId);
}
