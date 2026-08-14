package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 踢人/强制注销请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthOnlineKickoutRequest extends BaseRequest {

    /** 用户ID。 */
    @NotNull(message = "用户ID不能为空")
    private Long userId;
}
