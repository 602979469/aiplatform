package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.AiChatSessionDO;
import com.jakt.aiplatform.common.dal.query.AiChatSessionDalQuery;
import org.apache.ibatis.annotations.Mapper;

import java.util.List;

/**
 * 用户AI会话 Mapper。SQL 全部在 resources/mapper/AiChatSessionMapper.xml 中。
 */
@Mapper
public interface AiChatSessionMapper {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 用户AI会话数据对象
     */
    AiChatSessionDO selectById(Long id);

    /**
     * 按条件列表查询（当前用户会话列表）。
     *
     * @param query 查询参数
     * @return 会话列表
     */
    List<AiChatSessionDO> selectList(AiChatSessionDalQuery query);

    /**
     * 新增，自增主键回填到 {@code aiChatSessionDO.sessionId}。
     *
     * @param aiChatSessionDO 数据对象
     * @return 受影响行数
     */
    int insert(AiChatSessionDO aiChatSessionDO);

    /**
     * 按条件更新：只更新传入的非空字段（改会话标题等场景）。
     *
     * @param aiChatSessionDO 数据对象（至少含主键）
     * @return 受影响行数
     */
    int updateByCondition(AiChatSessionDO aiChatSessionDO);

    /**
     * 按主键逻辑删除（del_flag 0→2）。
     *
     * @param id 主键
     * @return 受影响行数
     */
    int deleteById(Long id);
}
