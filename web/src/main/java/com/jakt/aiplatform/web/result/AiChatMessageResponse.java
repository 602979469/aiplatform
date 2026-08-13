package com.jakt.aiplatform.web.result;


import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 用户AI会话消息响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AiChatMessageResponse extends BaseResult {
    /** 主键。 */
    private Long messageId;

    /** 会话ID。 */
    private Long sessionId;

    /** 用户ID。 */
    private Long userId;

    /** 角色（user用户 assistant助手）。 */
    private String role;

    /** 消息内容。 */
    private String content;

    /** 消息状态（0正常 1失败）。 */
    private String status;

}
