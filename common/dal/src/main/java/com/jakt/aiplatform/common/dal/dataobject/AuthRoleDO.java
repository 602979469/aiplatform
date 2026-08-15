package com.jakt.aiplatform.common.dal.dataobject;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表 DO对象
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRoleDO extends BaseDO {
    /** 主键。 */
    private Long roleId;

    /** 角色名称。 */
    private String roleName;

    /** 角色权限字符串。 */
    private String roleKey;

    /** 显示顺序。 */
    private Integer roleSort;

    /** 状态（0正常 1停用）。 */
    private String status;

    /** 备注。 */
    private String remark;

}
