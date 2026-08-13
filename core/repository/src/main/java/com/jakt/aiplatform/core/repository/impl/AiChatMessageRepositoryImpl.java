package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.mapper.AiChatMessageMapper;
import com.jakt.aiplatform.core.model.domain.AiChatMessage;
import com.jakt.aiplatform.core.repository.AiChatMessageRepository;
import com.jakt.aiplatform.core.repository.convertor.AiChatMessageConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * AI会话消息表仓储：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class AiChatMessageRepositoryImpl implements AiChatMessageRepository {

    /** AI会话消息表 Mapper。 */
    private final AiChatMessageMapper aiChatMessageMapper;

    public AiChatMessageRepositoryImpl(AiChatMessageMapper aiChatMessageMapper) {
        this.aiChatMessageMapper = aiChatMessageMapper;
    }

    @Override
    public AiChatMessage findById(Long id) {
        return AiChatMessageConvertor.toModel(aiChatMessageMapper.selectById(id));
    }

    @Override
    public AiChatMessage insert(AiChatMessage aiChatMessage) {
        var aiChatMessageDO = AiChatMessageConvertor.toDO(aiChatMessage);
        aiChatMessageMapper.insert(aiChatMessageDO);
        return AiChatMessageConvertor.toModel(aiChatMessageDO);
    }

    @Override
    public void updateStatus(Long messageId, String status) {
        aiChatMessageMapper.updateStatusById(messageId, status);
    }

    @Override
    public void deleteBySessionId(Long sessionId) {
        aiChatMessageMapper.deleteBySessionId(sessionId);
    }

    @Override
    public List<AiChatMessage> findBySessionAsc(Long sessionId) {
        return aiChatMessageMapper.selectBySessionAsc(sessionId).stream()
                .map(AiChatMessageConvertor::toModel).toList();
    }
}
