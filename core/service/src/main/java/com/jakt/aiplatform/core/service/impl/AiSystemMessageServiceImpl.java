package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.AiSystemMessage;
import com.jakt.aiplatform.core.repository.AiSystemMessageRepository;
import com.jakt.aiplatform.core.service.AiSystemMessageService;
import org.springframework.stereotype.Service;

/**
 * 系统AI会话消息领域服务实现。
 */
@Service
public class AiSystemMessageServiceImpl implements AiSystemMessageService {

    /** 系统AI会话消息仓储。 */
    private final AiSystemMessageRepository aiSystemMessageRepository;

    public AiSystemMessageServiceImpl(AiSystemMessageRepository aiSystemMessageRepository) {
        this.aiSystemMessageRepository = aiSystemMessageRepository;
    }

    @Override
    public AiSystemMessage createAiSystemMessage(AiSystemMessage aiSystemMessage) {
        return aiSystemMessageRepository.insert(aiSystemMessage);
    }
}
