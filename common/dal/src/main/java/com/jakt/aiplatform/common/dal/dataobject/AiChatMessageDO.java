package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户AI会话消息 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiChatMessageDO extends BaseDO {
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
