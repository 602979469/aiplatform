package com.jakt.aiplatform.web.param;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.util.List;

/**
 * 新增用户请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUserCreateRequest extends BaseRequest {

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

    /** 状态（0启用 1停用）。 */
    private EnableStatusEnum status;

    /** 角色ID列表。 */
    private List<Long> roleIds;
}
