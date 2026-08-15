package com.jakt.aiplatform.web.param;

import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import lombok.EqualsAndHashCode;

/**
 * 用户启停请求。
 */
@Data
@EqualsAndHashCode(callSuper = true)
public class AuthUserStatusRequest extends BaseRequest {

    /** 状态（0启用 1停用）。 */
    @NotNull(message = "状态不能为空")
    private EnableStatusEnum status;
}
