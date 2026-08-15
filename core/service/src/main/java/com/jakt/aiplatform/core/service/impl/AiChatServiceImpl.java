package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.integration.deepseek.DeepSeekClient;
import com.jakt.aiplatform.common.integration.deepseek.model.DeepSeekChatMessage;
import com.jakt.aiplatform.common.util.enums.LogFileEnum;
import com.jakt.aiplatform.common.util.result.Result;
import com.jakt.aiplatform.common.util.template.BizTemplate;
import com.jakt.aiplatform.common.util.template.TransactionTemplate;
import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.LoggerUtil;
import com.jakt.aiplatform.core.model.constant.AiPlatformConstant;
import com.jakt.aiplatform.core.model.domain.AiChatMessage;
import com.jakt.aiplatform.core.model.domain.AiChatResult;
import com.jakt.aiplatform.core.model.domain.AiChatSession;
import com.jakt.aiplatform.core.model.enums.AiChatMessageStatusEnum;
import com.jakt.aiplatform.core.model.enums.ChatRoleEnum;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.exception.AiPlatformException;
import com.jakt.aiplatform.core.model.param.AiChatSessionQueryParam;
import com.jakt.aiplatform.core.repository.AiChatMessageRepository;
import com.jakt.aiplatform.core.repository.AiChatSessionRepository;
import com.jakt.aiplatform.core.service.AiChatService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

/**
 * AI 对话领域服务实现：承载 AI 会话聚合根（会话 + 消息）的全部能力与对话用例。
 *
 * <p>会话与消息由本服务编排两个仓储组装（聚合根 Model 含副表 {@code List<AiChatMessage>}）；
 * 多写流程（用户消息 + 回答 + 会话更新）统一走事务模板。
 */
@Service
public class AiChatServiceImpl implements AiChatService {

    private static final String SYSTEM_PROMPT = "你是一个友好、专业的AI助手，请用简洁准确的中文回答用户的问题。";

    /** 最多携带的上下文消息条数（超出后只携带最近的消息）。 */
    private static final int MAX_CONTEXT_MESSAGES = 30;

    /** AI会话表仓储。 */
    private final AiChatSessionRepository aiChatSessionRepository;

    /** AI会话消息表仓储。 */
    private final AiChatMessageRepository aiChatMessageRepository;

    private final DeepSeekClient deepSeekClient;

    private final TransactionTemplate transactionTemplate;

    public AiChatServiceImpl(AiChatSessionRepository aiChatSessionRepository,
                             AiChatMessageRepository aiChatMessageRepository,
                             DeepSeekClient deepSeekClient,
                             TransactionTemplate transactionTemplate) {
        this.aiChatSessionRepository = aiChatSessionRepository;
        this.aiChatMessageRepository = aiChatMessageRepository;
        this.deepSeekClient = deepSeekClient;
        this.transactionTemplate = transactionTemplate;
    }

    // ==================== 会话管理 ====================

    @Override
    public List<AiChatSession> findList(AiChatSessionQueryParam query) {
        return aiChatSessionRepository.findList(query);
    }

    @Override
    public AiChatSession createAiChatSession(AiChatSession aiChatSession) {
        return aiChatSessionRepository.insert(aiChatSession);
    }

    @Override
    public void updateByCondition(AiChatSession aiChatSession) {
        int affected = aiChatSessionRepository.updateByCondition(aiChatSession);
        AssertUtil.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteAiChatSession(Long id) {
        Result<Void> result = BizTemplate.executeWithoutResult(transactionTemplate,
                () -> {
                    aiChatMessageRepository.deleteBySessionId(id);
                    aiChatSessionRepository.deleteById(id);
                });
        if (!result.isSuccess()) {
            throw AiPlatformException.ofThrow(result.getErrorCode(), result.getErrorMessage());
        }
    }

    @Override
    public AiChatSession getAiChatSession(Long id) {
        return aiChatSessionRepository.findById(id);
    }

    // ==================== 消息管理 ====================

    @Override
    public AiChatMessage getAiChatMessage(Long id) {
        return aiChatMessageRepository.findById(id);
    }

    @Override
    public AiChatMessage createAiChatMessage(AiChatMessage aiChatMessage) {
        return aiChatMessageRepository.insert(aiChatMessage);
    }

    @Override
    public void updateMessageStatus(Long messageId, AiChatMessageStatusEnum status) {
        aiChatMessageRepository.updateStatus(messageId, status);
    }

    @Override
    public List<AiChatMessage> findMessagesBySessionAsc(Long sessionId) {
        return aiChatMessageRepository.findBySessionAsc(sessionId);
    }

    // ==================== 对话用例 ====================

    @Override
    public AiChatResult chat(Long sessionId, Long messageId, String content, Long userId, String userName) {
        Result<AiChatResult> result = BizTemplate.execute(transactionTemplate,
                () -> doChat(sessionId, messageId, content, userId, userName));
        if (!result.isSuccess()) {
            throw AiPlatformException.ofThrow(result.getErrorCode(), result.getErrorMessage());
        }
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
        if (ObjectUtil.isNotNull(messageId)) {
            userMessage = aiChatMessageRepository.findById(messageId);
            AssertUtil.throwErrWhenTrue(ObjectUtil.isNull(userMessage)
                            || !Objects.equals(session.getSessionId(), userMessage.getSessionId())
                            || userMessage.getRole() != ChatRoleEnum.USER,
                    ErrorCodeEnum.CHAT_MESSAGE_NOT_RETRYABLE, "待重试的消息不存在或无权访问");
            reused = true;
        } else {
            userMessage = new AiChatMessage();
            userMessage.setSessionId(session.getSessionId());
            userMessage.setUserId(userId);
            userMessage.setRole(ChatRoleEnum.USER);
            userMessage.setContent(content);
            userMessage.setStatus(AiChatMessageStatusEnum.NORMAL);
            // 返回模型回填 messageId，后续失败标记依赖它
            userMessage = aiChatMessageRepository.insert(userMessage);
        }

        String reply;
        try {
            List<DeepSeekChatMessage> context = buildContext(session, userMessage.getContent(), reused);
            reply = deepSeekClient.chat(context);
        } catch (Exception e) {
            LoggerUtil.error(LogFileEnum.COMMON_ERROR, "对话调用失败: {}", e.getMessage());
            return failResult(session, userMessage, "模型调用失败：" + e.getMessage());
        }

        // 保存 AI 回答
        AiChatMessage assistantMessage = new AiChatMessage();
        assistantMessage.setSessionId(session.getSessionId());
        assistantMessage.setUserId(userId);
        assistantMessage.setRole(ChatRoleEnum.ASSISTANT);
        assistantMessage.setContent(reply);
        assistantMessage.setStatus(AiChatMessageStatusEnum.NORMAL);
        aiChatMessageRepository.insert(assistantMessage);

        // 重试成功，恢复提问状态
        if (reused && AiChatMessageStatusEnum.FAILED == userMessage.getStatus()) {
            aiChatMessageRepository.updateStatus(userMessage.getMessageId(), AiChatMessageStatusEnum.NORMAL);
        }

        // 首次对话时用问题重命名会话
        String sessionName = session.getSessionName();
        if (StrUtil.isBlank(sessionName) || AiPlatformConstant.DEFAULT_SESSION_NAME.equals(sessionName)) {
            sessionName = truncate(content, 30);
            AiChatSession update = new AiChatSession();
            update.setSessionId(session.getSessionId());
            update.setSessionName(sessionName);
            aiChatSessionRepository.updateByCondition(update);
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
        aiChatMessageRepository.updateStatus(userMessage.getMessageId(), AiChatMessageStatusEnum.FAILED);
        AiChatResult result = new AiChatResult();
        result.setSessionId(session.getSessionId());
        result.setSessionName(session.getSessionName());
        result.setUserMessageId(userMessage.getMessageId());
        result.setFailed(true);
        result.setError(error);
        return result;
    }

    /**
     * 校验会话归属，不存在时自动新建；存在时组装会话与消息（主表校验通过后才查副表）。
     */
    private AiChatSession resolveSession(Long sessionId, Long userId, String userName) {
        if (ObjectUtil.isNull(sessionId)) {
            AiChatSession session = new AiChatSession();
            session.setSessionName(AiPlatformConstant.DEFAULT_SESSION_NAME);
            session.setStatus(EnableStatusEnum.ENABLE);
            session.setUserId(userId);
            session.setUserName(userName);
            return aiChatSessionRepository.insert(session);
        }
        AiChatSession session = aiChatSessionRepository.findById(sessionId);
        AssertUtil.throwErrWhenTrue(ObjectUtil.isNull(session) || !Objects.equals(userId, session.getUserId()),
                ErrorCodeEnum.SESSION_ACCESS_DENIED, "会话不存在或无权访问");
        session.setMessages(aiChatMessageRepository.findBySessionAsc(sessionId));
        return session;
    }

    /**
     * 组装发送给模型的上下文（系统提示 + 最近若干条历史消息）。
     *
     * <p>会话快照在本次提问插入前加载：非重试时需把当前提问补进历史末尾，
     * 以还原「最新 N 条包含当前提问」的截断语义；重试时失败提问已被状态过滤，显式追加一次。
     */
    private List<DeepSeekChatMessage> buildContext(AiChatSession session, String currentContent,
                                                   boolean forceAppendCurrent) {
        List<AiChatMessage> history = new ArrayList<>(CollUtil.emptyIfNull(session.getMessages()).stream()
                .filter(message -> AiChatMessageStatusEnum.NORMAL == message.getStatus()
                        && StrUtil.isNotBlank(message.getContent()))
                .toList());
        if (!forceAppendCurrent) {
            AiChatMessage currentMessage = new AiChatMessage();
            currentMessage.setRole(ChatRoleEnum.USER);
            currentMessage.setContent(currentContent);
            currentMessage.setStatus(AiChatMessageStatusEnum.NORMAL);
            history.add(currentMessage);
        }
        List<DeepSeekChatMessage> context = new ArrayList<>();
        context.add(new DeepSeekChatMessage(ChatRoleEnum.SYSTEM.getCode(), SYSTEM_PROMPT));
        int start = Math.max(0, history.size() - MAX_CONTEXT_MESSAGES);
        for (int i = start; i < history.size(); i++) {
            AiChatMessage message = history.get(i);
            context.add(new DeepSeekChatMessage(message.getRole().getCode(), message.getContent()));
        }
        // 重试场景下提问已是失败状态被过滤，需显式携带
        if (forceAppendCurrent) {
            context.add(new DeepSeekChatMessage(ChatRoleEnum.USER.getCode(), currentContent));
        }
        return context;
    }

    /**
     * 截断字符串，用于会话命名。
     */
    private String truncate(String text, int maxLength) {
        if (ObjectUtil.isNull(text)) {
            return AiPlatformConstant.EMPTY_STRING;
        }
        String oneLine = text.replaceAll("\\s+", " ").trim();
        return oneLine.length() <= maxLength ? oneLine : oneLine.substring(0, maxLength);
    }

}
