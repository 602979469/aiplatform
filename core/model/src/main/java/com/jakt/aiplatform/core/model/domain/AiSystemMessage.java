package com.jakt.aiplatform.core.model.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统AI会话消息领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiSystemMessage extends BaseModel {
    /** 主键。 */
    private Long messageId;

    /** 会话ID。 */
    private Long sessionId;

    /** 角色（system/user/assistant）。 */
    private String role;

    /** 消息内容。 */
    private String content;

    /** 消息状态（0正常 1失败）。 */
    private String status;

}
