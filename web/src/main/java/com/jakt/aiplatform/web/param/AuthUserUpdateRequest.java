package com.jakt.aiplatform.web.param;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 修改用户请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUserUpdateRequest extends BaseRequest {

    /** 用户ID。 */
    @NotNull(message = "用户ID不能为空")
    private Long userId;

    /** 昵称。 */
    @Size(max = 50, message = "昵称长度不能超过50")
    private String nickname;

    /** 邮箱。 */
    @Size(max = 100, message = "邮箱长度不能超过100")
    private String email;

    /** 状态（0启用 1停用）。 */
    private EnableStatusEnum status;

    /** 备注。 */
    @Size(max = 500, message = "备注长度不能超过500")
    private String remark;
}
