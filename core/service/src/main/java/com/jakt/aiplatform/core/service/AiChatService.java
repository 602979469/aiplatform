package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.AiChatResult;

/**
 * AI 对话领域服务：承载对话编排规则（会话解析、上下文组装、失败重试标记）。
 */
public interface AiChatService {

    /**
     * 发起一次对话：保存用户消息，调用模型，返回完整回答。
     *
     * @param sessionId 会话ID（为空自动新建）
     * @param messageId 重试时携带的失败用户消息ID（为空新增提问）
     * @param content   用户输入
     * @param userId    当前用户ID
     * @param userName  当前用户名
     * @return 对话结果
     */
    AiChatResult chat(Long sessionId, Long messageId, String content, Long userId, String userName);
}
