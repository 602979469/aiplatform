package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.AiCapability;

/**
 * AI能力仓储：封装 Mapper，对外只暴露领域模型。
 */
public interface AiCapabilityRepository {

    /**
     * 按场景码 + 能力码查询启用中的能力。
     *
     * @param sceneCode      场景码
     * @param capabilityCode 能力编码
     * @return AI能力领域模型；未找到返回 null
     */
    AiCapability getBySceneAndCode(String sceneCode, String capabilityCode);
}
