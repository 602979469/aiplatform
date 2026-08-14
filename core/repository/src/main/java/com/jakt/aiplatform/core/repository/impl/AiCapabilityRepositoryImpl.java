package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.AiCapabilityDO;
import com.jakt.aiplatform.common.dal.mapper.AiCapabilityMapper;
import com.jakt.aiplatform.core.model.domain.AiCapability;
import com.jakt.aiplatform.core.repository.AiCapabilityRepository;
import com.jakt.aiplatform.core.repository.convertor.AiCapabilityConvertor;
import org.springframework.stereotype.Repository;

/**
 * AI能力仓储：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class AiCapabilityRepositoryImpl implements AiCapabilityRepository {

    /** AI能力 Mapper。 */
    private final AiCapabilityMapper aiCapabilityMapper;

    public AiCapabilityRepositoryImpl(AiCapabilityMapper aiCapabilityMapper) {
        this.aiCapabilityMapper = aiCapabilityMapper;
    }

    @Override
    public AiCapability getBySceneAndCode(String sceneCode, String capabilityCode) {
        AiCapabilityDO capabilityDO = aiCapabilityMapper.selectBySceneAndCode(sceneCode, capabilityCode);
        return AiCapabilityConvertor.toModel(capabilityDO);
    }
}
