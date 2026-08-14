package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.AiSystemSessionDO;
import com.jakt.aiplatform.common.dal.mapper.AiSystemSessionMapper;
import com.jakt.aiplatform.core.model.domain.AiSystemSession;
import com.jakt.aiplatform.core.repository.AiSystemSessionRepository;
import com.jakt.aiplatform.core.repository.convertor.AiSystemSessionConvertor;
import org.springframework.stereotype.Repository;

/**
 * 系统AI会话仓储：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class AiSystemSessionRepositoryImpl implements AiSystemSessionRepository {

    /** 系统AI会话 Mapper。 */
    private final AiSystemSessionMapper aiSystemSessionMapper;

    public AiSystemSessionRepositoryImpl(AiSystemSessionMapper aiSystemSessionMapper) {
        this.aiSystemSessionMapper = aiSystemSessionMapper;
    }

    @Override
    public AiSystemSession insert(AiSystemSession aiSystemSession) {
        AiSystemSessionDO aiSystemSessionDO = AiSystemSessionConvertor.toDO(aiSystemSession);
        aiSystemSessionMapper.insert(aiSystemSessionDO);
        return AiSystemSessionConvertor.toModel(aiSystemSessionDO);
    }
}
