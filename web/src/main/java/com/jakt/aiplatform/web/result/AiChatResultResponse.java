package com.jakt.aiplatform.web.result;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
/**
 * AI 对话结果响应 DTO（与前端 chat.js 约定对齐）。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class AiChatResultResponse {
    /** 会话ID（首次对话后端自动建会话时返回）。 */
    private Long sessionId;
    /** 会话名称。 */
    private String sessionName;
    /** 用户消息ID（失败重试时前端据此标记）。 */
    private Long userMessageId;
    /** AI 回答内容。 */
    private String reply;
    /** 是否失败（模型调用失败时 true）。 */
    private Boolean failed;
    /** 失败原因。 */
    private String error;
}
