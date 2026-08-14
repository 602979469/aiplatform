package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.AiChatSession;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.exception.AiPlatformException;
import com.jakt.aiplatform.core.model.param.AiChatSessionQueryParam;
import com.jakt.aiplatform.common.util.result.Result;
import com.jakt.aiplatform.common.util.template.BizTemplate;
import com.jakt.aiplatform.common.util.template.TransactionTemplate;
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
    private final TransactionTemplate transactionTemplate;

    public AiChatSessionServiceImpl(AiChatSessionRepository aiChatSessionRepository,
                                    TransactionTemplate transactionTemplate) {
        this.aiChatSessionRepository = aiChatSessionRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public AiChatSession createAiChatSession(AiChatSession aiChatSession) {
        return aiChatSessionRepository.insert(aiChatSession);
    }

    @Override
    public void updateByCondition(AiChatSession aiChatSession) {
        int affected = aiChatSessionRepository.updateByCondition(aiChatSession);
        AssertUtil.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteAiChatSession(Long id) {
        Result<Void> result = BizTemplate.executeWithoutResult(transactionTemplate,
                () -> aiChatSessionRepository.deleteWithMessages(id));
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
