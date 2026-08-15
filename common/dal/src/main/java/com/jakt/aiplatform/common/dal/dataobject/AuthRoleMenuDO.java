package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色菜单关联表 DO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRoleMenuDO extends BaseDO {

    /** 角色ID。 */
    private Long roleId;

    /** 菜单ID。 */
    private Long menuId;
}
