package com.jakt.aiplatform.core.model.domain;


import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 用户AI会话领域模型（聚合根：含会话消息列表，由仓储多 Mapper 一次性组装）。
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

    /** 会话消息列表（时间正序，由仓储组装，主表为空时不查副表）。 */
    private List<AiChatMessage> messages;

}
