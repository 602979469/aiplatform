package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 登录请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthLoginRequest extends BaseRequest {

    /** 登录账号。 */
    @NotBlank(message = "登录账号不能为空")
    private String username;

    /** 密码明文。 */
    @NotBlank(message = "密码不能为空")
    private String password;
}
