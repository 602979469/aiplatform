package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 当前用户修改密码请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthPasswordRequest extends BaseRequest {

    /** 旧密码。 */
    @NotBlank(message = "旧密码不能为空")
    private String oldPassword;

    /** 新密码。 */
    @NotBlank(message = "新密码不能为空")
    @Size(min = 6, max = 64, message = "新密码长度需在6-64位之间")
    private String newPassword;
}
