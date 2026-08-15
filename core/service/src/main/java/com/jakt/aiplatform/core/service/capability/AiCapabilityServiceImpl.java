package com.jakt.aiplatform.core.service.capability;

import com.jakt.aiplatform.common.integration.deepseek.DeepSeekClient;
import com.jakt.aiplatform.common.integration.deepseek.model.DeepSeekChatMessage;
import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.AiCapability;
import com.jakt.aiplatform.core.model.domain.AiSystemMessage;
import com.jakt.aiplatform.core.model.domain.AiSystemSession;
import com.jakt.aiplatform.core.model.enums.AiChatMessageStatusEnum;
import com.jakt.aiplatform.core.model.enums.ChatRoleEnum;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.repository.AiCapabilityRepository;
import com.jakt.aiplatform.core.repository.AiSystemMessageRepository;
import com.jakt.aiplatform.core.repository.AiSystemSessionRepository;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI能力领域服务实现：每次调用独立上下文（system规则 + user输入 + assistant回答落库）。
 * 系统会话/消息为能力调用的内部上下文，作为私有能力收敛在本服务内。
 */
@Service
public class AiCapabilityServiceImpl implements AiCapabilityService {

    /** AI能力仓储。 */
    private final AiCapabilityRepository aiCapabilityRepository;

    /** 系统AI会话仓储（能力调用的独立上下文）。 */
    private final AiSystemSessionRepository aiSystemSessionRepository;

    /** 系统AI消息仓储。 */
    private final AiSystemMessageRepository aiSystemMessageRepository;

    /** DeepSeek 客户端。 */
    private final DeepSeekClient deepSeekClient;

    public AiCapabilityServiceImpl(AiCapabilityRepository aiCapabilityRepository,
                                   AiSystemSessionRepository aiSystemSessionRepository,
                                   AiSystemMessageRepository aiSystemMessageRepository,
                                   DeepSeekClient deepSeekClient) {
        this.aiCapabilityRepository = aiCapabilityRepository;
        this.aiSystemSessionRepository = aiSystemSessionRepository;
        this.aiSystemMessageRepository = aiSystemMessageRepository;
        this.deepSeekClient = deepSeekClient;
    }

    @Override
    public String invoke(String sceneCode, String capabilityCode, String input) {
        AiCapability capability = aiCapabilityRepository.getBySceneAndCode(sceneCode, capabilityCode);
        AssertUtil.throwErrWhenNull(capability, ErrorCodeEnum.RESOURCE_NOT_FOUND,
                "AI能力不存在或已停用: " + sceneCode + "/" + capabilityCode);
        AssertUtil.throwErrWhenBlank(capability.getSkillRules(), ErrorCodeEnum.RESOURCE_NOT_FOUND,
                "AI能力未配置约束规则: " + capabilityCode);

        // 每次调用独立上下文：新建系统会话并落库（system规则 + user输入 + assistant回答）
        AiSystemSession session = createSystemSession(capability, sceneCode, capabilityCode);
        insertMessage(session.getSessionId(), ChatRoleEnum.SYSTEM, capability.getSkillRules());
        insertMessage(session.getSessionId(), ChatRoleEnum.USER, input);

        List<DeepSeekChatMessage> messages = List.of(
                new DeepSeekChatMessage(ChatRoleEnum.SYSTEM.getCode(), capability.getSkillRules()),
                new DeepSeekChatMessage(ChatRoleEnum.USER.getCode(), input));
        String reply = deepSeekClient.chat(messages);
        insertMessage(session.getSessionId(), ChatRoleEnum.ASSISTANT, reply);
        return reply;
    }

    /**
     * 创建系统会话（能力调用时创建独立上下文）。
     * 仓储 insert 返回回填主键后的新对象，必须接收返回值，否则 sessionId 为 null。
     */
    private AiSystemSession createSystemSession(AiCapability capability, String sceneCode, String capabilityCode) {
        AiSystemSession session = new AiSystemSession();
        session.setCapabilityId(capability.getCapabilityId());
        session.setSceneCode(sceneCode);
        session.setCapabilityCode(capabilityCode);
        session.setSessionName(capability.getCapabilityName());
        session.setStatus(EnableStatusEnum.ENABLE);
        return aiSystemSessionRepository.insert(session);
    }

    /**
     * 插入一条系统会话消息。
     */
    private void insertMessage(Long sessionId, ChatRoleEnum role, String content) {
        AiSystemMessage message = new AiSystemMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setStatus(AiChatMessageStatusEnum.NORMAL);
        aiSystemMessageRepository.insert(message);
    }
}
