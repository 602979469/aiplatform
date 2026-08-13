package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.AiChatSession;
import com.jakt.aiplatform.core.model.param.AiChatSessionQueryParam;
import com.jakt.aiplatform.core.model.result.Result;
import com.jakt.aiplatform.core.model.template.AiPlatformTransactionTemplate;
import com.jakt.aiplatform.core.repository.AiChatSessionRepository;
import com.jakt.aiplatform.core.service.AiChatSessionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI会话表领域服务实现：承载AI会话表相关的业务规则。只写规则，不碰持久化细节。
 */
@Service
public class AiChatSessionServiceImpl implements AiChatSessionService {

    /** AI会话表仓储。 */
    private final AiChatSessionRepository aiChatSessionRepository;

    /** 事务模板：删会话（连同消息）跨表多写统一走事务。 */
    private final AiPlatformTransactionTemplate aiPlatformTransactionTemplate;

    public AiChatSessionServiceImpl(AiChatSessionRepository aiChatSessionRepository,
                                    AiPlatformTransactionTemplate aiPlatformTransactionTemplate) {
        this.aiChatSessionRepository = aiChatSessionRepository;
        this.aiPlatformTransactionTemplate = aiPlatformTransactionTemplate;
    }

    @Override
    public AiChatSession createAiChatSession(AiChatSession aiChatSession) {
        return aiChatSessionRepository.insert(aiChatSession);
    }

    @Override
    public void updateByCondition(AiChatSession aiChatSession) {
        aiChatSessionRepository.updateByCondition(aiChatSession);
    }

    @Override
    public void deleteAiChatSession(Long id) {
        Result<Void> result = aiPlatformTransactionTemplate.executeWithoutResult(
                () -> aiChatSessionRepository.deleteWithMessages(id));
        AiPlatformInvoker.throwErrWhenTrue(!result.isSuccess(), result.getErrorCodeEnum(), result.getErrorMessage());
    }

    @Override
    public AiChatSession getAiChatSession(Long id) {
        return aiChatSessionRepository.findById(id);
    }

    @Override
    public List<AiChatSession> findList(AiChatSessionQueryParam query) {
        return aiChatSessionRepository.findList(query);
    }
}
