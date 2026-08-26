package com.jakt.aiplatform.core.service.impl;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;

import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.AiChatSession;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.core.model.param.AiChatSessionQueryParam;
import com.jakt.aiplatform.common.framework.result.Result;
import com.jakt.aiplatform.common.framework.template.BizTemplate;
import com.jakt.aiplatform.common.framework.template.TransactionTemplate;
import com.jakt.aiplatform.core.repository.AiChatSessionRepository;
import com.jakt.aiplatform.core.service.AiChatMessageService;
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

    /** AI会话消息服务：删会话时连带删消息（跨表多写由事务模板统一包裹）。 */
    private final AiChatMessageService aiChatMessageService;

    /** 事务模板：删会话（连同消息）跨表多写统一走事务。 */
    private final TransactionTemplate transactionTemplate;

    public AiChatSessionServiceImpl(AiChatSessionRepository aiChatSessionRepository,
                                    AiChatMessageService aiChatMessageService,
                                    TransactionTemplate transactionTemplate) {
        this.aiChatSessionRepository = aiChatSessionRepository;
        this.aiChatMessageService = aiChatMessageService;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public AiChatSession createAiChatSession(AiChatSession aiChatSession) {
        return aiChatSessionRepository.insert(aiChatSession);
    }

    @Override
    public void updateByCondition(AiChatSession aiChatSession) {
        int affected = aiChatSessionRepository.updateByCondition(aiChatSession);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteAiChatSession(Long id) {
        Result<Void> result = BizTemplate.executeWithoutResult(transactionTemplate,
                () -> {
                    aiChatMessageService.deleteBySessionId(id);
                    aiChatSessionRepository.deleteById(id);
                });
        if (!result.isSuccess()) {
            throw AiPlatformException.ofThrow(result.getErrorCode(), result.getErrorMessage());
        }
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
