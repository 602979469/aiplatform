package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.mapper.AiSystemMessageMapper;
import com.jakt.aiplatform.core.model.domain.AiSystemMessage;
import com.jakt.aiplatform.core.repository.AiSystemMessageRepository;
import com.jakt.aiplatform.core.repository.convertor.AiSystemMessageConvertor;
import org.springframework.stereotype.Repository;

/**
 * 系统AI会话消息仓储：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class AiSystemMessageRepositoryImpl implements AiSystemMessageRepository {

    /** 系统AI会话消息 Mapper。 */
    private final AiSystemMessageMapper aiSystemMessageMapper;

    public AiSystemMessageRepositoryImpl(AiSystemMessageMapper aiSystemMessageMapper) {
        this.aiSystemMessageMapper = aiSystemMessageMapper;
    }

    @Override
    public AiSystemMessage insert(AiSystemMessage aiSystemMessage) {
        var aiSystemMessageDO = AiSystemMessageConvertor.toDO(aiSystemMessage);
        aiSystemMessageMapper.insert(aiSystemMessageDO);
        return AiSystemMessageConvertor.toModel(aiSystemMessageDO);
    }
}
