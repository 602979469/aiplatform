package com.jakt.aiplatform.web.assembler;

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
     * 会话列表 Model → 响应列表。
     *
     * @param list 会话模型列表
     * @return 会话响应列表
     */
    public static List<AiChatSessionResponse> toSessionResponseList(List<AiChatSession> list) {
        return list.stream().map(AiChatAssembler::toSessionResponse).toList();
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
     * 消息列表 Model → 响应列表。
     *
     * @param list 消息模型列表
     * @return 消息响应列表
     */
    public static List<AiChatMessageResponse> toMessageResponseList(List<AiChatMessage> list) {
        return list.stream().map(AiChatAssembler::toMessageResponse).toList();
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
        return AiChatResultResponse.builder()
                .sessionId(source.getSessionId())
                .sessionName(source.getSessionName())
                .userMessageId(source.getUserMessageId())
                .reply(source.getReply())
                .failed(source.getFailed())
                .error(source.getError())
                .build();
    }
}
