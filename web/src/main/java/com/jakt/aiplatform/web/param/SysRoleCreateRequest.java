package com.jakt.aiplatform.web.param;
import jakarta.validation.constraints.Size;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import com.jakt.aiplatform.core.model.enums.DataScopeEnum;
import com.jakt.aiplatform.core.model.enums.RoleStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 创建角色请求 DTO。
 *
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class SysRoleCreateRequest extends BaseRequest {

    /** 角色名称。 */
    @NotBlank(message = "角色名称不能为空")
    @Size(max = 30, message = "角色名称长度不能超过 30")
    private String roleName;

    /** 角色权限字符串。 */
    @NotBlank(message = "角色权限字符串不能为空")
    @Size(max = 100, message = "角色权限字符串长度不能超过 100")
    private String roleKey;

    /** 显示顺序。 */
    @NotBlank(message = "显示顺序不能为空")
    private String roleSort;

    /** 数据范围（1：全部数据权限 2：自定数据权限 3：本部门数据权限 4：本部门及以下数据权限）。 */
    private DataScopeEnum dataScope;

    /** 角色状态（0正常 1停用）。 */
    @NotNull(message = "角色状态（0正常 1停用）不能为空")
    private RoleStatusEnum status;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过 500")
    private String remark;

}
