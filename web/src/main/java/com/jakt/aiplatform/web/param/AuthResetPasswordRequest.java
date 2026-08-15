package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 重置密码请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthResetPasswordRequest extends BaseRequest {

    /** 新密码明文。 */
    @NotBlank(message = "密码不能为空")
    @Size(min = 6, max = 64, message = "密码长度需在6-64位之间")
    private String password;
}
