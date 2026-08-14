package com.jakt.aiplatform.web.checker;

import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.AuthResetPasswordRequest;
import com.jakt.aiplatform.web.param.AuthUserCreateRequest;
import com.jakt.aiplatform.web.param.AuthUserQueryRequest;
import com.jakt.aiplatform.web.param.AuthUserRoleRequest;
import com.jakt.aiplatform.web.param.AuthUserStatusRequest;
import com.jakt.aiplatform.web.param.AuthUserUpdateRequest;

/**
 * 用户参数检查器（对应 AuthUserController）。
 */
public final class AuthUserParamChecker {

    private AuthUserParamChecker() {
    }

    /** 检查用户分页查询参数（放宽：为空跳过，分页缺省走默认值）。 */
    public static void checkUserQuery(AuthUserQueryRequest request) {
        if (request == null) {
            return;
        }
        ParamValidator.validate(request);
    }

    /** 检查新增用户参数。 */
    public static void checkUserCreate(AuthUserCreateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "用户参数不能为空");
        ParamValidator.validate(request);
    }

    /** 检查修改用户参数。 */
    public static void checkUserUpdate(AuthUserUpdateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "用户参数不能为空");
        ParamValidator.validate(request);
    }

    /** 检查用户启停参数。 */
    public static void checkUserStatus(AuthUserStatusRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "启停参数不能为空");
        ParamValidator.validate(request);
    }

    /** 检查重置密码参数。 */
    public static void checkResetPassword(AuthResetPasswordRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "重置密码参数不能为空");
        ParamValidator.validate(request);
    }

    /** 检查分配角色参数。 */
    public static void checkUserRole(AuthUserRoleRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "分配角色参数不能为空");
        ParamValidator.validate(request);
    }

    /** 检查用户ID路径参数。 */
    public static void checkUserId(Long userId) {
        AssertUtil.throwErrWhenNull(userId, ErrorCodeEnum.PARAM_INVALID, "用户ID不能为空");
    }
}
