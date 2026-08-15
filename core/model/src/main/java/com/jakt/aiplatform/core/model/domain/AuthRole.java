package com.jakt.aiplatform.core.model.domain;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRole extends BaseModel {
    /** 主键。 */
    private Long roleId;

    /** 角色名称。 */
    private String roleName;

    /** 角色权限字符串。 */
    private String roleKey;

    /** 显示顺序。 */
    private Integer roleSort;

    /** 状态（0启用 1停用）。 */
    private EnableStatusEnum status;

    /** 备注。 */
    private String remark;

}
