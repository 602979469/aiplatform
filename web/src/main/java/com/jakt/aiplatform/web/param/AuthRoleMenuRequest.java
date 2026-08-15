package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 分配角色菜单请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRoleMenuRequest extends BaseRequest {

    /** 菜单ID列表。 */
    private List<Long> menuIds;
}
