package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysUserRoleDO extends BaseDO {
    /** 主键。 */
    private Long id;

    /** 用户ID。 */
    private Long userId;

    /** 角色ID。 */
    private Long roleId;

}
