package com.jakt.aiplatform.core.model.domain;


import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 系统AI会话领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiSystemSession extends BaseModel {
    /** 主键。 */
    private Long sessionId;

    /** 能力ID。 */
    private Long capabilityId;

    /** 场景码。 */
    private String sceneCode;

    /** 能力编码。 */
    private String capabilityCode;

    /** 会话名称。 */
    private String sessionName;

    /** 会话状态（0正常 1停用）。 */
    private EnableStatusEnum status;

    /** 备注。 */
    private String remark;

}
