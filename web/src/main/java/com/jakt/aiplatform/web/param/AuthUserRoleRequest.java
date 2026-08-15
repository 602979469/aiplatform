package com.jakt.aiplatform.web.param;

import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 分配用户角色请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUserRoleRequest extends BaseRequest {

    /** 角色ID列表。 */
    private List<Long> roleIds;
}
