package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.AiChatMessage;
import com.jakt.aiplatform.core.model.domain.AiChatResult;
import com.jakt.aiplatform.core.model.domain.AiChatSession;
import com.jakt.aiplatform.core.model.enums.AiChatMessageStatusEnum;
import com.jakt.aiplatform.core.model.param.AiChatSessionQueryParam;

import java.util.List;

/**
 * AI 对话领域服务：承载 AI 会话聚合根（会话 + 消息）的全部能力与对话用例。
 *
 * <p>会话与消息由仓储多 Mapper 一次性组装（主表 Model 含副表 {@code List<AiChatMessage>}），
 * 不按表拆分服务，避免表与 service 一一对应。
 */
public interface AiChatService {

    /**
     * 发起一次对话：保存用户消息，调用模型，返回完整回答。
     *
     * @param sessionId 会话ID（为空自动新建）
     * @param messageId 重试时携带的失败用户消息ID（为空新增提问）
     * @param content   用户输入
     * @param userId    当前用户ID
     * @param userName  当前用户名
     * @return 对话结果
     */
    AiChatResult chat(Long sessionId, Long messageId, String content, Long userId, String userName);

    /**
     * 按条件列表查询（当前用户会话列表）。
     *
     * @param query 查询参数
     * @return 会话列表
     */
    List<AiChatSession> findList(AiChatSessionQueryParam query);

    /**
     * 创建会话。
     *
     * @param aiChatSession 会话（主键由仓储回填）
     * @return 创建后的会话
     */
    AiChatSession createAiChatSession(AiChatSession aiChatSession);

    /**
     * 按条件更新会话（只更新传入的非空字段，改标题等场景）。
     *
     * @param aiChatSession 会话（至少含主键）
     */
    void updateByCondition(AiChatSession aiChatSession);

    /**
     * 删除会话（连同消息，事务内执行）。
     *
     * @param id 会话ID
     */
    void deleteAiChatSession(Long id);

    /**
     * 按 ID 获取会话。
     *
     * @param id 会话ID
     * @return 会话
     */
    AiChatSession getAiChatSession(Long id);

    /**
     * 按 ID 获取会话消息（重试时校验消息归属）。
     *
     * @param id 消息ID
     * @return 会话消息
     */
    AiChatMessage getAiChatMessage(Long id);

    /**
     * 创建会话消息。
     *
     * @param aiChatMessage 消息（主键由仓储回填）
     * @return 创建后的消息
     */
    AiChatMessage createAiChatMessage(AiChatMessage aiChatMessage);

    /**
     * 更新消息状态（失败重试标记）。
     *
     * @param messageId 消息ID
     * @param status    目标状态
     */
    void updateMessageStatus(Long messageId, AiChatMessageStatusEnum status);

    /**
     * 按会话查询消息（时间正序，聊天记录展示/上下文组装用）。
     *
     * @param sessionId 会话ID
     * @return 消息列表
     */
    List<AiChatMessage> findMessagesBySessionAsc(Long sessionId);
}
