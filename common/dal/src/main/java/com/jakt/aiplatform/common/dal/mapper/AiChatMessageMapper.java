package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.AiChatMessageDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * AI会话消息 Mapper。SQL 全部在 resources/mapper/AiChatMessageMapper.xml 中。
 */
@Mapper
public interface AiChatMessageMapper {

    /**
     * 按主键查询（重试时校验消息归属）。
     *
     * @param id 主键
     * @return AI会话消息数据对象
     */
    AiChatMessageDO selectById(Long id);

    /**
     * 新增，自增主键回填到 {@code aiChatMessageDO.messageId}。
     *
     * @param aiChatMessageDO 数据对象
     * @return 受影响行数
     */
    int insert(AiChatMessageDO aiChatMessageDO);

    /**
     * 更新消息状态（失败重试标记：0正常 1失败）。
     *
     * @param messageId 消息ID
     * @param status    目标状态
     * @return 受影响行数
     */
    int updateStatusById(@Param("messageId") Long messageId, @Param("status") String status);

    /**
     * 按会话逻辑删除全部消息（del_flag 0→2）。
     *
     * @param sessionId 会话ID
     * @return 受影响行数
     */
    int deleteBySessionId(@Param("sessionId") Long sessionId);

    /**
     * 按会话查询全部消息（时间正序，聊天记录展示/上下文组装用）。
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<AiChatMessageDO> selectBySessionAsc(@Param("sessionId") Long sessionId);
}
