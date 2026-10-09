package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.integration.deepseek.DeepSeekClient;
import com.jakt.aiplatform.common.integration.deepseek.model.DeepSeekChatMessage;
import com.jakt.aiplatform.common.integration.dify.DifyWorkflowClient;
import com.jakt.aiplatform.common.util.tools.JsonUtil;
import com.jakt.aiplatform.core.model.domain.AiCapability;
import com.jakt.aiplatform.core.model.domain.AiSystemMessage;
import com.jakt.aiplatform.core.model.domain.AiSystemSession;
import com.jakt.aiplatform.core.model.dto.DifyCapabilityConfig;
import com.jakt.aiplatform.core.model.enums.AiCapabilityProviderEnum;
import com.jakt.aiplatform.core.model.enums.AiChatMessageStatusEnum;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.ChatRoleEnum;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.repository.AiCapabilityRepository;
import com.jakt.aiplatform.core.service.AiCapabilityService;
import com.jakt.aiplatform.core.service.AiSystemMessageService;
import com.jakt.aiplatform.core.service.AiSystemSessionService;
import org.springframework.stereotype.Service;

import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * AI能力领域服务实现：按能力表配置的 provider 分发（DEEPSEEK 直连 / DIFY 工作流），
 * 每次调用独立上下文（system规则 + user输入 + assistant回答落库）。
 *
 * <p>业务侧只认 {@code sceneCode + capabilityCode}，换提供方不用改业务代码。
 */
@Service
public class AiCapabilityServiceImpl implements AiCapabilityService {

    /** AI能力仓储。 */
    private final AiCapabilityRepository aiCapabilityRepository;

    /** 系统AI会话服务。 */
    private final AiSystemSessionService aiSystemSessionService;

    /** 系统AI消息服务。 */
    private final AiSystemMessageService aiSystemMessageService;

    /** DeepSeek 客户端（provider=DEEPSEEK）。 */
    private final DeepSeekClient deepSeekClient;

    /** Dify 工作流客户端（provider=DIFY）。 */
    private final DifyWorkflowClient difyWorkflowClient;

    public AiCapabilityServiceImpl(AiCapabilityRepository aiCapabilityRepository,
                                   AiSystemSessionService aiSystemSessionService,
                                   AiSystemMessageService aiSystemMessageService,
                                   DeepSeekClient deepSeekClient,
                                   DifyWorkflowClient difyWorkflowClient) {
        this.aiCapabilityRepository = aiCapabilityRepository;
        this.aiSystemSessionService = aiSystemSessionService;
        this.aiSystemMessageService = aiSystemMessageService;
        this.deepSeekClient = deepSeekClient;
        this.difyWorkflowClient = difyWorkflowClient;
    }

    @Override
    public String invoke(String sceneCode, String capabilityCode, String input) {
        AiCapability capability = aiCapabilityRepository.getBySceneAndCode(sceneCode, capabilityCode);
        AssertUtil.throwErrWhenNull(capability, BizErrorCodeEnum.RESOURCE_NOT_FOUND,
                "AI能力不存在或已停用: " + sceneCode + "/" + capabilityCode);

        // 每次调用独立上下文：新建系统会话并落库（system规则 + user输入 + assistant回答）
        AiSystemSession session = new AiSystemSession();
        session.setCapabilityId(capability.getCapabilityId());
        session.setSceneCode(sceneCode);
        session.setCapabilityCode(capabilityCode);
        session.setSessionName(capability.getCapabilityName());
        session.setStatus(EnableStatusEnum.ENABLE);
        // 仓储 insert 返回回填主键后的新对象，必须接收返回值，否则 sessionId 为 null
        session = aiSystemSessionService.createAiSystemSession(session);

        insertMessage(session.getSessionId(), ChatRoleEnum.SYSTEM, resolveSystemContent(capability));
        insertMessage(session.getSessionId(), ChatRoleEnum.USER, input);

        String reply = AiCapabilityProviderEnum.DIFY == capability.getProvider()
                ? invokeDify(capability, input)
                : invokeDeepSeek(capability, input);
        insertMessage(session.getSessionId(), ChatRoleEnum.ASSISTANT, reply);
        return reply;
    }

    /**
     * 会话里记录的 system 内容：DeepSeek 用提示词，Dify 只记工作流标识（提示词在 Dify 侧）。
     */
    private String resolveSystemContent(AiCapability capability) {
        if (AiCapabilityProviderEnum.DIFY == capability.getProvider()) {
            return "[DIFY 工作流] " + capability.getCapabilityName() + "（配置见 provider_config）";
        }
        AssertUtil.throwErrWhenBlank(capability.getSkillRules(), BizErrorCodeEnum.RESOURCE_NOT_FOUND,
                "AI能力未配置约束规则: " + capability.getCapabilityCode());
        return capability.getSkillRules();
    }

    /**
     * DeepSeek 直连：system 提示词 + 用户输入，一次对话拿到答案。
     */
    private String invokeDeepSeek(AiCapability capability, String input) {
        List<DeepSeekChatMessage> messages = List.of(
                new DeepSeekChatMessage(ChatRoleEnum.SYSTEM.getCode(), capability.getSkillRules()),
                new DeepSeekChatMessage(ChatRoleEnum.USER.getCode(), input));
        return deepSeekClient.chat(messages);
    }

    /**
     * Dify 工作流：能力入参映射成工作流输入变量，取指定输出变量作为返回值。
     */
    private String invokeDify(AiCapability capability, String input) {
        DifyCapabilityConfig config = parseDifyConfig(capability);
        Map<String, Object> inputs = new LinkedHashMap<>();
        if (CollUtil.isNotEmpty(config.getFixedInputs())) {
            inputs.putAll(config.getFixedInputs());
        }
        inputs.put(StrUtil.blankToDefault(config.getInputVariable(), "input"), input);

        Map<String, Object> outputs = difyWorkflowClient.runBlocking(config.getApiKey(), inputs,
                capability.getCapabilityCode());
        AssertUtil.throwErrWhenTrue(CollUtil.isEmpty(outputs), BizErrorCodeEnum.EXTERNAL_ERROR,
                "Dify 工作流没有返回任何输出: " + capability.getCapabilityCode());

        Object value = StrUtil.isNotBlank(config.getOutputVariable())
                ? outputs.get(config.getOutputVariable())
                : outputs.values().iterator().next();
        AssertUtil.throwErrWhenNull(value, BizErrorCodeEnum.EXTERNAL_ERROR,
                "Dify 输出里没有这个变量: " + config.getOutputVariable());
        return value instanceof String ? (String) value : JsonUtil.toJson(value);
    }

    /**
     * 解析 DIFY 能力的 provider_config。
     */
    private DifyCapabilityConfig parseDifyConfig(AiCapability capability) {
        AssertUtil.throwErrWhenBlank(capability.getProviderConfig(), BizErrorCodeEnum.RESOURCE_NOT_FOUND,
                "DIFY 能力未配置 provider_config: " + capability.getCapabilityCode());
        try {
            DifyCapabilityConfig config = JsonUtil.parseObject(capability.getProviderConfig(),
                    DifyCapabilityConfig.class);
            return ObjectUtil.isNull(config) ? new DifyCapabilityConfig() : config;
        } catch (Exception e) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID,
                    "DIFY 能力配置不是合法 JSON: " + capability.getCapabilityCode());
        }
    }

    /**
     * 插入一条会话消息。
     */
    private void insertMessage(Long sessionId, ChatRoleEnum role, String content) {
        AiSystemMessage message = new AiSystemMessage();
        message.setSessionId(sessionId);
        message.setRole(role);
        message.setContent(content);
        message.setStatus(AiChatMessageStatusEnum.NORMAL);
        aiSystemMessageService.createAiSystemMessage(message);
    }
}
