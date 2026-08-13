package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.AiChatMessage;

import java.util.List;

/**
 * AI会话消息表仓储：封装 Mapper，对外只暴露领域模型。
 */
public interface AiChatMessageRepository {

    /**
     * 按主键查询（重试时校验消息归属）。
     *
     * @param id 主键
     * @return AI会话消息表领域模型
     */
    AiChatMessage findById(Long id);

    /**
     * 新增。
     *
     * @param aiChatMessage AI会话消息表
     * @return 新增后的AI会话消息表（主键已回填）
     */
    AiChatMessage insert(AiChatMessage aiChatMessage);

    /**
     * 更新消息状态（失败重试标记：0正常 1失败）。
     *
     * @param messageId 消息ID
     * @param status    目标状态
     */
    void updateStatus(Long messageId, String status);

    /**
     * 按会话逻辑删除全部消息（del_flag 0→2）。
     *
     * @param sessionId 会话ID
     */
    void deleteBySessionId(Long sessionId);

    /**
     * 按会话查询全部消息（时间正序）。
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<AiChatMessage> findBySessionAsc(Long sessionId);
}
