package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.mapper.AiChatSessionMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.AiChatSession;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.AiChatSessionQueryParam;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.AiChatMessageRepository;
import com.jakt.aiplatform.core.repository.AiChatSessionRepository;
import com.jakt.aiplatform.core.repository.convertor.AiChatSessionConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI会话表仓储：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class AiChatSessionRepositoryImpl implements AiChatSessionRepository {

    /** AI会话表 Mapper。 */
    private final AiChatSessionMapper aiChatSessionMapper;

    /** AI会话消息表仓储（组合：删会话时连带删消息）。 */
    private final AiChatMessageRepository aiChatMessageRepository;

    public AiChatSessionRepositoryImpl(AiChatSessionMapper aiChatSessionMapper,
                                       AiChatMessageRepository aiChatMessageRepository) {
        this.aiChatSessionMapper = aiChatSessionMapper;
        this.aiChatMessageRepository = aiChatMessageRepository;
    }

    @Override
    public AiChatSession findById(Long id) {
        return AiChatSessionConvertor.toModel(aiChatSessionMapper.selectById(id));
    }

    @Override
    public List<AiChatSession> findList(AiChatSessionQueryParam query) {
        return aiChatSessionMapper.selectList(query).stream().map(AiChatSessionConvertor::toModel).toList();
    }

    @Override
    public AiChatSession insert(AiChatSession aiChatSession) {
        var aiChatSessionDO = AiChatSessionConvertor.toDO(aiChatSession);
        aiChatSessionMapper.insert(aiChatSessionDO);
        return AiChatSessionConvertor.toModel(aiChatSessionDO);
    }

    @Override
    public void updateByCondition(AiChatSession aiChatSession) {
        int affected = aiChatSessionMapper.updateByCondition(AiChatSessionConvertor.toDO(aiChatSession));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                "AiChatSessionRepository.updateByCondition sessionId={} 影响行数={}",
                aiChatSession.getSessionId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = aiChatSessionMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE,
                "AiChatSessionRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }

    @Override
    public void deleteWithMessages(Long sessionId) {
        aiChatMessageRepository.deleteBySessionId(sessionId);
        aiChatSessionMapper.deleteById(sessionId);
    }
}
