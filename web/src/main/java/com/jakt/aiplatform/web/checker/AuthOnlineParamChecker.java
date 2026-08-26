package com.jakt.aiplatform.web.checker;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;

import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.web.param.AuthOnlineDisableRequest;
import com.jakt.aiplatform.web.param.AuthOnlineKickoutRequest;
import com.jakt.aiplatform.web.param.AuthOnlineQueryRequest;

/**
 * 在线会话参数检查器（对应 AuthOnlineController）。
 */
public final class AuthOnlineParamChecker {

    private AuthOnlineParamChecker() {
    }

    /** 检查在线分页查询参数（放宽：为空跳过）。 */
    public static void checkOnlineQuery(AuthOnlineQueryRequest request) {
        if (request == null) {
            return;
        }
        ParamValidator.validate(request);
    }

    /** 检查踢人/注销参数。 */
    public static void checkOnlineKickout(AuthOnlineKickoutRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "参数不能为空");
        ParamValidator.validate(request);
    }

    /** 检查封禁/解封参数。 */
    public static void checkOnlineDisable(AuthOnlineDisableRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "参数不能为空");
        ParamValidator.validate(request);
    }
}
