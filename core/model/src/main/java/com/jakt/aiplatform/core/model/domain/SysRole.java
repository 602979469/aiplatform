package com.jakt.aiplatform.core.model.domain;

import java.util.Set;
import com.jakt.aiplatform.core.model.enums.DataScopeEnum;
import com.jakt.aiplatform.core.model.enums.RoleStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色领域模型（RuoYi 结构：继承 BaseEntity，含组装字段 menuIds/deptIds/permissions）。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRole extends BaseEntity {

    /** 用户是否存在此角色标识（组装字段，默认 false）。 */
    private boolean flag = false;

    /**
     * 是否管理员角色。
     *
     * @return 是否管理员角色
     */
    public boolean isAdmin() {
        return isAdmin(this.roleId);
    }

    /**
     * 是否管理员角色（roleId=1）。
     *
     * @param roleId 角色ID
     * @return 是否管理员角色
     */
    public static boolean isAdmin(Long roleId) {
        return roleId != null && 1L == roleId;
    }

    /** 角色ID。 */
    private Long roleId;

    /** 数据范围（1全部 2自定 3本部门 4本部门及以下）。 */
    private DataScopeEnum dataScope;

    /** 角色名称。 */
    private String roleName;

    /** 角色权限。 */
    private String roleKey;

    /** 显示顺序。 */
    private String roleSort;

    /** 角色状态（0正常 1停用）。 */
    private RoleStatusEnum status;

    /** 删除标志（0代表存在 2代表删除）。 */
    private String delFlag;

    /** 菜单组（组装字段）。 */
    private Long[] menuIds;

    /** 部门组（组装字段）。 */
    private Long[] deptIds;

    /** 权限标识集合（组装字段）。 */
    private Set<String> permissions;
}
