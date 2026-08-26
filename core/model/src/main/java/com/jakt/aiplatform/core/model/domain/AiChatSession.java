package com.jakt.aiplatform.core.model.domain;
import com.jakt.aiplatform.common.framework.model.BaseModel;


import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

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
    private EnableStatusEnum status;

    /** 备注。 */
    private String remark;

}
