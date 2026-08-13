package com.jakt.aiplatform.core.model.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户AI会话消息领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiChatMessage extends BaseModel {
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
