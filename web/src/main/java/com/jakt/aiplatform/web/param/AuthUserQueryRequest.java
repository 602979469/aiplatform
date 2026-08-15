package com.jakt.aiplatform.web.param;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户分页查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUserQueryRequest extends PageQueryRequest {

    /** 用户名（模糊筛选）。 */
    private String username;

    /** 状态。 */
    private EnableStatusEnum status;
}
