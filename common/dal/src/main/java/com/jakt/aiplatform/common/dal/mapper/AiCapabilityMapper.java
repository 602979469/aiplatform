package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.AiCapabilityDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

/**
 * AI能力 Mapper。SQL 全部在 resources/mapper/AiCapabilityMapper.xml 中。
 */
@Mapper
public interface AiCapabilityMapper {

    /**
     * 按场景码 + 能力码查询启用中的能力（对应唯一键 uk_scene_cap）。
     *
     * @param sceneCode      场景码
     * @param capabilityCode 能力编码
     * @return AI能力数据对象；未找到返回 null
     */
    AiCapabilityDO selectBySceneAndCode(@Param("sceneCode") String sceneCode,
                                        @Param("capabilityCode") String capabilityCode);
}
