package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.AiChatMessage;
import com.jakt.aiplatform.core.model.domain.AiChatResult;
import com.jakt.aiplatform.core.model.domain.AiChatSession;

import java.util.List;

/**
 * AI 对话管理接口：会话/消息/对话用例编排。
 */
public interface AiChatManager {

    /**
     * 当前用户会话列表。
     *
     * @return 会话列表
     */
    List<AiChatSession> listSessions();

    /**
     * 新建会话。
     *
     * @return 新建后的会话（主键已回填）
     */
    AiChatSession createSession();

    /**
     * 修改会话标题。
     *
     * @param sessionId   会话ID
     * @param sessionName 新标题
     */
    void renameSession(Long sessionId, String sessionName);

    /**
     * 删除会话（连同消息）。
     *
     * @param sessionId 会话ID
     */
    void deleteSession(Long sessionId);

    /**
     * 查询会话消息记录（时间正序）。
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<AiChatMessage> listMessages(Long sessionId);

    /**
     * 发起对话。
     *
     * @param sessionId 会话ID（为空自动新建）
     * @param messageId 重试时携带的失败用户消息ID
     * @param content   用户输入
     * @return 对话结果
     */
    AiChatResult chat(Long sessionId, Long messageId, String content);
}
