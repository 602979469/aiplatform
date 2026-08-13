package com.jakt.aiplatform.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.biz.service.AiChatManager;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.context.UserContext;
import com.jakt.aiplatform.core.model.domain.AiChatMessage;
import com.jakt.aiplatform.core.model.domain.AiChatResult;
import com.jakt.aiplatform.core.model.domain.AiChatSession;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.AiChatSessionQueryParam;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.service.AiChatMessageService;
import com.jakt.aiplatform.core.service.AiChatService;
import com.jakt.aiplatform.core.service.AiChatSessionService;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Objects;

/**
 * AI 对话管理实现：用例编排，只依赖 core-service 与 core-model，不触碰仓储。
 */
@Service
public class AiChatManagerImpl implements AiChatManager {

    private final AiChatSessionService aiChatSessionService;

    private final AiChatMessageService aiChatMessageService;

    private final AiChatService aiChatService;

    public AiChatManagerImpl(AiChatSessionService aiChatSessionService,
                             AiChatMessageService aiChatMessageService,
                             AiChatService aiChatService) {
        this.aiChatSessionService = aiChatSessionService;
        this.aiChatMessageService = aiChatMessageService;
        this.aiChatService = aiChatService;
    }

    @Override
    public List<AiChatSession> listSessions() {
        AiChatSessionQueryParam query = new AiChatSessionQueryParam();
        query.setUserId(UserContext.getUserId());
        return aiChatSessionService.findList(query);
    }

    @Override
    public AiChatSession createSession() {
        AiChatSession session = new AiChatSession();
        session.setSessionName("新会话");
        session.setStatus("0");
        session.setUserId(UserContext.getUserId());
        session.setUserName(UserContext.getUserName());
        AiChatSession created = aiChatSessionService.createAiChatSession(session);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "新建AI会话 sessionId={} userId={}",
                created.getSessionId(), UserContext.getUserId());
        return created;
    }

    @Override
    public void renameSession(Long sessionId, String sessionName) {
        checkOwner(sessionId);
        AiPlatformInvoker.throwErrWhenBlank(sessionName, ErrorCodeEnum.PARAM_INVALID, "会话标题不能为空");
        AiChatSession update = new AiChatSession();
        update.setSessionId(sessionId);
        update.setSessionName(StrUtil.maxLength(sessionName, 100));
        aiChatSessionService.updateByCondition(update);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "修改AI会话标题 sessionId={}", sessionId);
    }

    @Override
    public void deleteSession(Long sessionId) {
        checkOwner(sessionId);
        aiChatSessionService.deleteAiChatSession(sessionId);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除AI会话 sessionId={}", sessionId);
    }

    @Override
    public List<AiChatMessage> listMessages(Long sessionId) {
        checkOwner(sessionId);
        return aiChatMessageService.findBySessionAsc(sessionId);
    }

    @Override
    public AiChatResult chat(Long sessionId, Long messageId, String content) {
        return aiChatService.chat(sessionId, messageId, content,
                UserContext.getUserId(), UserContext.getUserName());
    }

    /**
     * 校验会话归属当前用户。
     */
    private AiChatSession checkOwner(Long sessionId) {
        AiChatSession session = aiChatSessionService.getAiChatSession(sessionId);
        AiPlatformInvoker.throwErrWhenTrue(session == null
                        || !Objects.equals(UserContext.getUserId(), session.getUserId()),
                ErrorCodeEnum.BIZ_ERROR, "会话不存在或无权访问");
        return session;
    }
}
