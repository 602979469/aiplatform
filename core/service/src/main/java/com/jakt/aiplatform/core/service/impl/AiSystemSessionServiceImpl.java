package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.core.model.domain.AiSystemSession;
import com.jakt.aiplatform.core.repository.AiSystemSessionRepository;
import com.jakt.aiplatform.core.service.AiSystemSessionService;
import org.springframework.stereotype.Service;

/**
 * 系统AI会话领域服务实现。
 */
@Service
public class AiSystemSessionServiceImpl implements AiSystemSessionService {

    /** 系统AI会话仓储。 */
    private final AiSystemSessionRepository aiSystemSessionRepository;

    public AiSystemSessionServiceImpl(AiSystemSessionRepository aiSystemSessionRepository) {
        this.aiSystemSessionRepository = aiSystemSessionRepository;
    }

    @Override
    public AiSystemSession createAiSystemSession(AiSystemSession aiSystemSession) {
        return aiSystemSessionRepository.insert(aiSystemSession);
    }
}
