package com.jakt.aiplatform.web.param;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 角色分页查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRoleQueryRequest extends PageQueryRequest {

    /** 角色名称。 */
    private String roleName;

    /** 角色标识。 */
    private String roleKey;

    /** 状态。 */
    private EnableStatusEnum status;
}
