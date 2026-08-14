package com.jakt.aiplatform.web.assembler;

import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.AiChatMessage;
import com.jakt.aiplatform.core.model.domain.AiChatResult;
import com.jakt.aiplatform.core.model.domain.AiChatSession;
import com.jakt.aiplatform.web.result.AiChatMessageResponse;
import com.jakt.aiplatform.web.result.AiChatResultResponse;
import com.jakt.aiplatform.web.result.AiChatSessionResponse;

import java.util.List;

/**
 * AI 对话 DTO 组装器：Model ↔ Response。
 */
public final class AiChatAssembler {

    private AiChatAssembler() {
    }

    /**
     * 会话 Model → 会话响应。
     *
     * @param source 会话模型
     * @return 会话响应；入参为空返回 null
     */
    public static AiChatSessionResponse toSessionResponse(AiChatSession source) {
        if (source == null) {
            return null;
        }
        AiChatSessionResponse target = new AiChatSessionResponse();
        target.setSessionId(source.getSessionId());
        target.setSessionName(source.getSessionName());
        target.setUserId(source.getUserId());
        target.setUserName(source.getUserName());
        target.setStatus(source.getStatus());
        target.setRemark(source.getRemark());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 消息 Model → 消息响应。
     *
     * @param source 消息模型
     * @return 消息响应；入参为空返回 null
     */
    public static AiChatMessageResponse toMessageResponse(AiChatMessage source) {
        if (source == null) {
            return null;
        }
        AiChatMessageResponse target = new AiChatMessageResponse();
        target.setMessageId(source.getMessageId());
        target.setSessionId(source.getSessionId());
        target.setUserId(source.getUserId());
        target.setRole(source.getRole());
        target.setContent(source.getContent());
        target.setStatus(source.getStatus());
        target.setCreateTime(source.getCreateTime());
        target.setUpdateTime(source.getUpdateTime());
        return target;
    }

    /**
     * 对话结果 Model → 响应。
     *
     * @param source 对话结果模型
     * @return 对话结果响应；入参为空返回 null
     */
    public static AiChatResultResponse toResultResponse(AiChatResult source) {
        if (source == null) {
            return null;
        }
        AiChatResultResponse response = new AiChatResultResponse();
        response.setSessionId(source.getSessionId());
        response.setSessionName(source.getSessionName());
        response.setUserMessageId(source.getUserMessageId());
        response.setReply(source.getReply());
        response.setFailed(source.getFailed());
        response.setError(source.getError());
        return response;
    }
}
