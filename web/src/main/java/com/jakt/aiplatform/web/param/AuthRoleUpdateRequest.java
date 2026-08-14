package com.jakt.aiplatform.web.param;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改角色请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRoleUpdateRequest extends BaseRequest {

    /** 角色ID。 */
    @NotNull(message = "角色ID不能为空")
    private Long roleId;

    /** 角色名称。 */
    @Size(max = 50, message = "角色名称长度不能超过50")
    private String roleName;

    /** 角色标识。 */
    @Size(max = 100, message = "角色标识长度不能超过100")
    private String roleKey;

    /** 显示顺序。 */
    private Integer roleSort;

    /** 状态（0启用 1停用）。 */
    private EnableStatusEnum status;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;
}
