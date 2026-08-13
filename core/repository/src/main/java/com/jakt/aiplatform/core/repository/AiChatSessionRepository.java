package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.AiChatSession;
import com.jakt.aiplatform.core.model.param.AiChatSessionQueryParam;

import java.util.List;

/**
 * AI会话表仓储：封装 Mapper，对外只暴露领域模型。
 */
public interface AiChatSessionRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return AI会话表领域模型
     */
    AiChatSession findById(Long id);

    /**
     * 按条件列表查询（当前用户会话列表）。
     *
     * @param query 查询参数
     * @return 会话列表
     */
    List<AiChatSession> findList(AiChatSessionQueryParam query);

    /**
     * 新增。
     *
     * @param aiChatSession AI会话表
     * @return 新增后的AI会话表（主键已回填）
     */
    AiChatSession insert(AiChatSession aiChatSession);

    /**
     * 按条件更新：只更新传入的非空字段（改会话标题等场景）。
     *
     * @param aiChatSession AI会话表（至少含主键）
     */
    void updateByCondition(AiChatSession aiChatSession);

    /**
     * 按主键逻辑删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);

    /**
     * 删除会话并连同消息逻辑删除（组合会话/消息仓储，无事务注解，由上层事务模板包裹）。
     *
     * @param sessionId 会话ID
     */
    void deleteWithMessages(Long sessionId);
}
