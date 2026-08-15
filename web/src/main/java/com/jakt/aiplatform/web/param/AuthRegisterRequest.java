package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 注册请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthRegisterRequest extends BaseRequest {

    /** 登录账号。 */
    @NotBlank(message = "登录账号不能为空")
    @Size(max = 50, message = "登录账号长度不能超过50")
    private String username;

    /** 密码明文。 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度需在6-64位之间")
    private String password;

    /** 昵称。 */
    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    /** 邮箱。 */
    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;
}
