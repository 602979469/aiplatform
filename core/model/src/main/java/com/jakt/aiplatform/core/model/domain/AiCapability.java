package com.jakt.aiplatform.core.model.domain;
import com.jakt.aiplatform.common.framework.model.BaseModel;


import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * AI能力领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AiCapability extends BaseModel {
    /** 主键。 */
    private Long capabilityId;

    /** 场景码。 */
    private String sceneCode;

    /** 能力编码。 */
    private String capabilityCode;

    /** 能力名称。 */
    private String capabilityName;

    /** 能力描述。 */
    private String description;

    /** 能力约束规则（system提示词）。 */
    private String skillRules;

    /** 状态（0正常 1停用）。 */
    private EnableStatusEnum status;

    /** 备注。 */
    private String remark;

}
