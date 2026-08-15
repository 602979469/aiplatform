package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.AiSystemMessageDO;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统AI会话消息 Mapper。SQL 全部在 resources/mapper/AiSystemMessageMapper.xml 中。
 */
@Mapper
public interface AiSystemMessageMapper {

    /**
     * 新增，自增主键回填到 {@code aiSystemMessageDO.messageId}。
     *
     * @param aiSystemMessageDO 数据对象
     * @return 受影响行数
     */
    int insert(AiSystemMessageDO aiSystemMessageDO);
}
