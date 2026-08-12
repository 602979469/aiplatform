package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色菜单关联 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleMenuDO extends BaseDO {
    /** 主键。 */
    private Long id;

    /** 角色ID。 */
    private Long roleId;

    /** 菜单ID。 */
    private Long menuId;

}
