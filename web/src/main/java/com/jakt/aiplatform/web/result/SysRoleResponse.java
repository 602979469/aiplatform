package com.jakt.aiplatform.web.result;

import com.jakt.aiplatform.core.model.enums.DataScopeEnum;
import com.jakt.aiplatform.core.model.enums.RoleStatusEnum;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

/**
 * 角色响应 DTO。
 */
@Data
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class SysRoleResponse extends BaseResult {
    /** 主键。 */
    private Long roleId;

    /** 角色名称。 */
    private String roleName;

    /** 角色权限字符串。 */
    private String roleKey;

    /** 显示顺序。 */
    private String roleSort;

    /** 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）。 */
    private DataScopeEnum dataScope;

    /** 角色状态（0正常 1停用）。 */
    private RoleStatusEnum status;

    /** 备注。 */
    private String remark;

}
