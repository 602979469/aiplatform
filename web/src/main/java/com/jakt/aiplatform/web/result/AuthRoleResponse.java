package com.jakt.aiplatform.web.result;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;
/**
 * 角色响应。
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class AuthRoleResponse extends BaseResult {
    /** 角色ID。 */
    private Long roleId;
    /** 角色名称。 */
    private String roleName;
    /** 角色标识。 */
    private String roleKey;
    /** 显示顺序。 */
    private Integer roleSort;
    /** 状态（0启用 1停用）。 */
    private EnableStatusEnum status;
    /** 备注。 */
    private String remark;
    /** 创建时间。 */
    private LocalDateTime createTime;
    /** 是否超级管理员角色：为 true 时前端禁用修改/删除/停用，后端同样拒绝变更。 */
    private Boolean superAdmin;
}
