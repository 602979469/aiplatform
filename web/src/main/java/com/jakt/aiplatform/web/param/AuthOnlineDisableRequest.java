package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 封禁/解封请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthOnlineDisableRequest extends BaseRequest {

    /** 用户ID。 */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 封禁时长（秒），解封时忽略。 */
    private Long seconds;
}
