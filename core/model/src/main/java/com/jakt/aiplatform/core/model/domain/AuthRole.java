package com.jakt.aiplatform.core.model.domain;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.model.BaseModel;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;

import java.util.Collection;

import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色表领域模型。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRole extends BaseModel {

    /** 超级管理员角色标识（role_key=admin）。 */
    public static final String SUPER_ADMIN_ROLE_KEY = "admin";

    /** 超级管理员角色名称（role_name=admin 时同样按超级管理员处理）。 */
    public static final String SUPER_ADMIN_ROLE_NAME = "admin";

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

    /**
     * 是否超级管理员角色：该角色不可变更（不可改、不可停用、不可删除），且拥有全部权限。
     *
     * @return 角色名称或权限字符串为 admin 时返回 true
     */
    public boolean isSuperAdmin() {
        return isSuperAdminIdentifier(roleName) || isSuperAdminIdentifier(roleKey);
    }

    /**
     * 角色标识集合中是否包含超级管理员（用户绑定多个角色时任一命中即视为超级管理员）。
     *
     * @param roleIdentifiers 角色名称或权限字符串集合
     * @return 是否包含超级管理员角色
     */
    public static boolean containsSuperAdmin(Collection<String> roleIdentifiers) {
        return ObjectUtil.isNotNull(roleIdentifiers) && roleIdentifiers.stream().anyMatch(AuthRole::isSuperAdminIdentifier);
    }

    /**
     * 单个角色名称/权限字符串是否超级管理员。
     *
     * @param identifier 角色名称或权限字符串
     * @return 是否超级管理员
     */
    private static boolean isSuperAdminIdentifier(String identifier) {
        return SUPER_ADMIN_ROLE_NAME.equalsIgnoreCase(identifier) || SUPER_ADMIN_ROLE_KEY.equalsIgnoreCase(identifier);
    }

}
