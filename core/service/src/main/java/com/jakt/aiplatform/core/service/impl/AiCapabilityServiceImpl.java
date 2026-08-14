package com.jakt.aiplatform.core.service.impl;

import com.jakt.aiplatform.common.integration.deepseek.DeepSeekClient;
import com.jakt.aiplatform.common.integration.deepseek.model.DeepSeekChatMessage;
import com.jakt.aiplatform.common.integration.deepseek.model.DeepSeekRoleEnum;
import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.core.model.domain.AiCapability;
import com.jakt.aiplatform.core.model.domain.AiSystemMessage;
import com.jakt.aiplatform.core.model.domain.AiSystemSession;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.repository.AiCapabilityRepository;
import com.jakt.aiplatform.core.service.AiCapabilityService;
import com.jakt.aiplatform.core.service.AiSystemMessageService;
import com.jakt.aiplatform.core.service.AiSystemSessionService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * AI能力领域服务实现：每次调用独立上下文（system规则 + user输入 + assistant回答落库）。
 */
@Service
public class AiCapabilityServiceImpl implements AiCapabilityService {

    /** AI能力仓储。 */
    private final AiCapabilityRepository aiCapabilityRepository;

    /** 系统AI会话服务。 */
    private final AiSystemSessionService aiSystemSessionService;

    /** 系统AI消息服务。 */
    private final AiSystemMessageService aiSystemMessageService;

    /** DeepSeek 客户端。 */
    private final DeepSeekClient deepSeekClient;

    public AiCapabilityServiceImpl(AiCapabilityRepository aiCapabilityRepository,
                                   AiSystemSessionService aiSystemSessionService,
                                   AiSystemMessageService aiSystemMessageService,
                                   DeepSeekClient deepSeekClient) {
        this.aiCapabilityRepository = aiCapabilityRepository;
        this.aiSystemSessionService = aiSystemSessionService;
        this.aiSystemMessageService = aiSystemMessageService;
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
        AiSystemSession session = new AiSystemSession();
        session.setCapabilityId(capability.getCapabilityId());
        session.setSceneCode(sceneCode);
        session.setCapabilityCode(capabilityCode);
        session.setSessionName(capability.getCapabilityName());
        session.setStatus("0");
        aiSystemSessionService.createAiSystemSession(session);

        insertMessage(session.getSessionId(), DeepSeekRoleEnum.SYSTEM, capability.getSkillRules());
        insertMessage(session.getSessionId(), DeepSeekRoleEnum.USER, input);

        List<DeepSeekChatMessage> messages = List.of(
                new DeepSeekChatMessage(DeepSeekRoleEnum.SYSTEM.getCode(), capability.getSkillRules()),
                new DeepSeekChatMessage(DeepSeekRoleEnum.USER.getCode(), input));
        String reply = deepSeekClient.chat(messages);
        insertMessage(session.getSessionId(), DeepSeekRoleEnum.ASSISTANT, reply);
        return reply;
    }

    /**
 * 插入一条会话消息。
     */
    private void insertMessage(Long sessionId, DeepSeekRoleEnum role, String content) {
        AiSystemMessage message = new AiSystemMessage();
        message.setSessionId(sessionId);
        message.setRole(role.getCode());
        message.setContent(content);
        message.setStatus("0");
        aiSystemMessageService.createAiSystemMessage(message);
    }
}
