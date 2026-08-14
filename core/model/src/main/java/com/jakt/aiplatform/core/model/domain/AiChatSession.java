package com.jakt.aiplatform.core.model.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;
import com.jakt.aiplatform.core.model.enums.AiChatSessionStatusEnum;

/**
 * 用户AI会话领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiChatSession extends BaseModel {
    /** 主键。 */
    private Long sessionId;

    /** 会话名称。 */
    private String sessionName;

    /** 用户ID。 */
    private Long userId;

    /** 用户名。 */
    private String userName;

    /** 会话状态（0正常 1停用）。 */
    private AiChatSessionStatusEnum status;

    /** 备注。 */
    private String remark;

}
