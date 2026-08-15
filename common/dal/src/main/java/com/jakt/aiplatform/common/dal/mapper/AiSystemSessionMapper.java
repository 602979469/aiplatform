package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.AiSystemSessionDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统AI会话 Mapper。SQL 全部在 resources/mapper/AiSystemSessionMapper.xml 中。
 */
@Mapper
public interface AiSystemSessionMapper {

    /**
     * 新增，自增主键回填到 {@code aiSystemSessionDO.sessionId}。
     *
     * @param aiSystemSessionDO 数据对象
     * @return 受影响行数
     */
    int insert(AiSystemSessionDO aiSystemSessionDO);
}
