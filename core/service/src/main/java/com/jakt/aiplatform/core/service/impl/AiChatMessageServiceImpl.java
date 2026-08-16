package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.AiChatMessage;
import com.jakt.aiplatform.core.model.enums.AiChatMessageStatusEnum;
import com.jakt.aiplatform.core.repository.AiChatMessageRepository;
import com.jakt.aiplatform.core.service.AiChatMessageService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI会话消息表领域服务实现：承载AI会话消息表相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class AiChatMessageServiceImpl implements AiChatMessageService {

    /** AI会话消息表仓储。 */
    private final AiChatMessageRepository aiChatMessageRepository;

    public AiChatMessageServiceImpl(AiChatMessageRepository aiChatMessageRepository) {
        this.aiChatMessageRepository = aiChatMessageRepository;
    }

    @Override
    public AiChatMessage createAiChatMessage(AiChatMessage aiChatMessage) {
        return aiChatMessageRepository.insert(aiChatMessage);
    }

    @Override
    public AiChatMessage getAiChatMessage(Long id) {
        return aiChatMessageRepository.findById(id);
    }

    @Override
    public void updateStatus(Long messageId, AiChatMessageStatusEnum status) {
        aiChatMessageRepository.updateStatus(messageId, status);
    }

    @Override
    public void deleteBySessionId(Long sessionId) {
        aiChatMessageRepository.deleteBySessionId(sessionId);
    }

    @Override
    public List<AiChatMessage> findBySessionAsc(Long sessionId) {
        return aiChatMessageRepository.findBySessionAsc(sessionId);
    }
}
