package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.common.dal.dataobject.AiChatSessionDO;
import com.jakt.aiplatform.common.dal.mapper.AiChatSessionMapper;
import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.AiChatSession;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.util.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.AiChatSessionQueryParam;
import com.jakt.aiplatform.common.util.tools.LoggerUtil;
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
        AiChatSessionDO sessionDO = aiChatSessionMapper.selectById(id);
        return AiChatSessionConvertor.toModel(sessionDO);
    }

    @Override
    public List<AiChatSession> findList(AiChatSessionQueryParam query) {
        List<AiChatSessionDO> sourceList = aiChatSessionMapper.selectList(AiChatSessionConvertor.toDalQuery(query));
        return ConvertUtil.map(sourceList, AiChatSessionConvertor::toModel);
    }

    @Override
    public AiChatSession insert(AiChatSession aiChatSession) {
        AiChatSessionDO aiChatSessionDO = AiChatSessionConvertor.toDO(aiChatSession);
        aiChatSessionMapper.insert(aiChatSessionDO);
        return AiChatSessionConvertor.toModel(aiChatSessionDO);
    }

    @Override
    public int updateByCondition(AiChatSession aiChatSession) {
        return aiChatSessionMapper.updateByCondition(AiChatSessionConvertor.toDO(aiChatSession));
    }

    @Override
    public int deleteById(Long id) {
        return aiChatSessionMapper.deleteById(id);
    }

    @Override
    public void deleteWithMessages(Long sessionId) {
        aiChatMessageRepository.deleteBySessionId(sessionId);
        aiChatSessionMapper.deleteById(sessionId);
    }
}
