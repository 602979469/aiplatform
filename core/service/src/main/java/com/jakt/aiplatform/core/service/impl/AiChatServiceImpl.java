package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.integration.deepseek.DeepSeekClient;
import com.jakt.aiplatform.common.integration.deepseek.model.DeepSeekChatMessage;
import com.jakt.aiplatform.common.util.config.AiChatProperties;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.AiChatMessage;
import com.jakt.aiplatform.core.model.domain.AiChatResult;
import com.jakt.aiplatform.core.model.domain.AiChatSession;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.result.Result;
import com.jakt.aiplatform.core.model.template.AiPlatformTransactionTemplate;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.AiChatMessageService;
import com.jakt.aiplatform.core.service.AiChatService;
import com.jakt.aiplatform.core.service.AiChatSessionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AI 对话领域服务实现：会话解析、上下文组装、模型调用、失败重试标记。
 * 多写流程（用户消息 + 回答 + 会话更新）统一走事务模板。
 */
@Service
public class AiChatServiceImpl implements AiChatService {

    private static final String DEFAULT_SESSION_NAME = "新会话";

    private static final String SYSTEM_PROMPT = "你是一个友好、专业的AI助手，请用简洁准确的中文回答用户的问题。";

    /** 最多携带的上下文消息条数（超出后只携带最近的消息）。 */
    private static final int MAX_CONTEXT_MESSAGES = 30;

    private final AiChatSessionService aiChatSessionService;

    private final AiChatMessageService aiChatMessageService;

    private final DeepSeekClient deepSeekClient;

    private final AiChatProperties aiChatProperties;

    private final AiPlatformTransactionTemplate aiPlatformTransactionTemplate;

    public AiChatServiceImpl(AiChatSessionService aiChatSessionService,
                             AiChatMessageService aiChatMessageService,
                             DeepSeekClient deepSeekClient,
                             AiChatProperties aiChatProperties,
                             AiPlatformTransactionTemplate aiPlatformTransactionTemplate) {
        this.aiChatSessionService = aiChatSessionService;
        this.aiChatMessageService = aiChatMessageService;
        this.deepSeekClient = deepSeekClient;
        this.aiChatProperties = aiChatProperties;
        this.aiPlatformTransactionTemplate = aiPlatformTransactionTemplate;
    }

    @Override
    public AiChatResult chat(Long sessionId, Long messageId, String content, Long userId, String userName) {
        Result<AiChatResult> result = aiPlatformTransactionTemplate.execute(
                () -> doChat(sessionId, messageId, content, userId, userName));
        AiPlatformInvoker.throwErrWhenTrue(!result.isSuccess(), result.getErrorCodeEnum(), result.getErrorMessage());
        return result.getData();
    }

    /**
     * 事务内对话主流程：模型调用失败时保留提问并标记失败（失败结果正常提交，供前端重试）。
     */
    private AiChatResult doChat(Long sessionId, Long messageId, String content, Long userId, String userName) {
        AiChatSession session = resolveSession(sessionId, userId, userName);

        // 用户消息：重试时复用失败消息，否则新增一条提问
        AiChatMessage userMessage;
        boolean reused = false;
        if (messageId != null) {
            userMessage = aiChatMessageService.getAiChatMessage(messageId);
            AiPlatformInvoker.throwErrWhenTrue(userMessage == null
                            || !Objects.equals(session.getSessionId(), userMessage.getSessionId())
                            || !"user".equals(userMessage.getRole()),
                    ErrorCodeEnum.BIZ_ERROR, "待重试的消息不存在或无权访问");
            reused = true;
        } else {
            userMessage = new AiChatMessage();
            userMessage.setSessionId(session.getSessionId());
            userMessage.setUserId(userId);
            userMessage.setRole("user");
            userMessage.setContent(content);
            userMessage.setStatus("0");
            // 返回模型回填 messageId，后续失败标记依赖它
            userMessage = aiChatMessageService.createAiChatMessage(userMessage);
        }

        // 模拟失败/超时（测试用，配置 ai.chat.simulation）
        String simulation = aiChatProperties.getSimulation();
        if ("fail".equals(simulation) || "timeout".equals(simulation)) {
            if ("timeout".equals(simulation)) {
                sleepQuietly(aiChatProperties.getTimeoutSeconds() * 1000L);
            }
            return failResult(session, userMessage,
                    "模拟" + ("timeout".equals(simulation) ? "超时" : "失败") + "：模型暂未响应，请点击重试");
        }

        String reply;
        try {
            List<DeepSeekChatMessage> context = buildContext(session.getSessionId(), userMessage.getContent(), reused);
            reply = deepSeekClient.chat(context);
        } catch (Exception e) {
            AiPlatformLoggerUtil.error(LogFileEnum.COMMON_ERROR, "对话调用失败: {}", e.getMessage());
            return failResult(session, userMessage, "模型调用失败：" + e.getMessage());
        }

        // 保存 AI 回答
        AiChatMessage assistantMessage = new AiChatMessage();
        assistantMessage.setSessionId(session.getSessionId());
        assistantMessage.setUserId(userId);
        assistantMessage.setRole("assistant");
        assistantMessage.setContent(reply);
        assistantMessage.setStatus("0");
        aiChatMessageService.createAiChatMessage(assistantMessage);

        // 重试成功，恢复提问状态
        if (reused && "1".equals(userMessage.getStatus())) {
            aiChatMessageService.updateStatus(userMessage.getMessageId(), "0");
        }

        // 首次对话时用问题重命名会话
        String sessionName = session.getSessionName();
        if (StrUtil.isBlank(sessionName) || DEFAULT_SESSION_NAME.equals(sessionName)) {
            sessionName = truncate(content, 30);
            AiChatSession update = new AiChatSession();
            update.setSessionId(session.getSessionId());
            update.setSessionName(sessionName);
            aiChatSessionService.updateByCondition(update);
        }

        AiChatResult result = new AiChatResult();
        result.setSessionId(session.getSessionId());
        result.setSessionName(sessionName);
        result.setUserMessageId(userMessage.getMessageId());
        result.setReply(reply);
        return result;
    }

    /**
     * 组装失败结果：标记提问为失败（保留供重试）。
     */
    private AiChatResult failResult(AiChatSession session, AiChatMessage userMessage, String error) {
        aiChatMessageService.updateStatus(userMessage.getMessageId(), "1");
        AiChatResult result = new AiChatResult();
        result.setSessionId(session.getSessionId());
        result.setSessionName(session.getSessionName());
        result.setUserMessageId(userMessage.getMessageId());
        result.setFailed(true);
        result.setError(error);
        return result;
    }

    /**
     * 校验会话归属，不存在时自动新建。
     */
    private AiChatSession resolveSession(Long sessionId, Long userId, String userName) {
        if (sessionId == null) {
            AiChatSession session = new AiChatSession();
            session.setSessionName(DEFAULT_SESSION_NAME);
            session.setStatus("0");
            session.setUserId(userId);
            session.setUserName(userName);
            return aiChatSessionService.createAiChatSession(session);
        }
        AiChatSession session = aiChatSessionService.getAiChatSession(sessionId);
        AiPlatformInvoker.throwErrWhenTrue(session == null || !Objects.equals(userId, session.getUserId()),
                ErrorCodeEnum.BIZ_ERROR, "会话不存在或无权访问");
        return session;
    }

    /**
     * 组装发送给模型的上下文（系统提示 + 最近若干条历史消息）。
     */
    private List<DeepSeekChatMessage> buildContext(Long sessionId, String currentContent, boolean forceAppendCurrent) {
        List<AiChatMessage> history = aiChatMessageService.findBySessionAsc(sessionId).stream()
                .filter(message -> "0".equals(message.getStatus()) && StrUtil.isNotBlank(message.getContent()))
                .toList();
        List<DeepSeekChatMessage> context = new ArrayList<>();
        context.add(new DeepSeekChatMessage("system", SYSTEM_PROMPT));
        int start = Math.max(0, history.size() - MAX_CONTEXT_MESSAGES);
        for (int i = start; i < history.size(); i++) {
            AiChatMessage message = history.get(i);
            context.add(new DeepSeekChatMessage(message.getRole(), message.getContent()));
        }
        // 重试场景下提问已是失败状态被过滤，需显式携带；新提问已包含在历史中
        if (forceAppendCurrent) {
            context.add(new DeepSeekChatMessage("user", currentContent));
        }
        return context;
    }

    /**
     * 截断字符串，用于会话命名。
     */
    private String truncate(String text, int maxLength) {
        if (text == null) {
            return "";
        }
        String oneLine = text.replaceAll("\\s+", " ").trim();
        return oneLine.length() <= maxLength ? oneLine : oneLine.substring(0, maxLength);
    }

    private void sleepQuietly(long millis) {
        try {
            Thread.sleep(millis);
        } catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }
}
