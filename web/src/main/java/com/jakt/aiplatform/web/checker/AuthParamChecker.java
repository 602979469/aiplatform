package com.jakt.aiplatform.web.checker;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;

import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.web.param.AuthLoginRequest;
import com.jakt.aiplatform.web.param.AuthRegisterRequest;

/**
 * 认证参数检查器（对应 AuthController）：登录/注册入参校验。
 */
public final class AuthParamChecker {

    private AuthParamChecker() {
    }

    /** 检查登录参数。 */
    public static void checkLogin(AuthLoginRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "登录参数不能为空");
        ParamValidator.validate(request);
    }

    /** 检查注册参数。 */
    public static void checkRegister(AuthRegisterRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "注册参数不能为空");
        ParamValidator.validate(request);
    }
}
