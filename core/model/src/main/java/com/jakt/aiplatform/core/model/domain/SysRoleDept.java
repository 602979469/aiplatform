package com.jakt.aiplatform.core.model.domain;


import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色部门关联领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleDept extends BaseModel {
    /** 主键。 */
    private Long id;

    /** 角色ID。 */
    private Long roleId;

    /** 部门ID。 */
    private Long deptId;

}
