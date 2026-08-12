package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色部门关联 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleDeptDO extends BaseDO {
    /** 主键。 */
    private Long id;

    /** 角色ID。 */
    private Long roleId;

    /** 部门ID。 */
    private Long deptId;

}
