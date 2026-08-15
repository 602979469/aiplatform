package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.AiChatMessageDO;
import com.jakt.aiplatform.common.dal.mapper.AiChatMessageMapper;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.AiChatMessage;
import com.jakt.aiplatform.core.model.enums.AiChatMessageStatusEnum;
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
        AiChatMessageDO messageDO = aiChatMessageMapper.selectById(id);
        return AiChatMessageConvertor.toModel(messageDO);
    }

    @Override
    public AiChatMessage insert(AiChatMessage aiChatMessage) {
        AiChatMessageDO aiChatMessageDO = AiChatMessageConvertor.toDO(aiChatMessage);
        aiChatMessageMapper.insert(aiChatMessageDO);
        return AiChatMessageConvertor.toModel(aiChatMessageDO);
    }

    @Override
    public int updateStatus(Long messageId, AiChatMessageStatusEnum status) {
        return aiChatMessageMapper.updateStatusById(messageId, status.getCode());
    }

    @Override
    public int deleteBySessionId(Long sessionId) {
        return aiChatMessageMapper.deleteBySessionId(sessionId);
    }

    @Override
    public List<AiChatMessage> findBySessionAsc(Long sessionId) {
        List<AiChatMessageDO> doList = aiChatMessageMapper.selectBySessionAsc(sessionId);
        return ConvertUtil.map(doList, AiChatMessageConvertor::toModel);
    }
}
