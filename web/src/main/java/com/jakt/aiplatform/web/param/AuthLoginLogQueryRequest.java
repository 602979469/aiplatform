package com.jakt.aiplatform.web.param;

import com.jakt.aiplatform.core.model.enums.LoginLogStatusEnum;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录记录分页查询请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthLoginLogQueryRequest extends PageQueryRequest {

    /** 用户名。 */
    private String username;

    /** 结果状态。 */
    private LoginLogStatusEnum status;
}
