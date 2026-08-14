package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户角色关联表 DO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUserRoleDO extends BaseDO {

    /** 用户ID。 */
    private Long userId;

    /** 角色ID。 */
    private Long roleId;
}
