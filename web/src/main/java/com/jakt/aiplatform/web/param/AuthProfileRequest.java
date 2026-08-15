package com.jakt.aiplatform.web.param;

import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 当前用户资料修改请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthProfileRequest extends BaseRequest {

    /** 昵称。 */
    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    /** 邮箱。 */
    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;
}
