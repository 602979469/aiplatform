package com.jakt.aiplatform.web.checker;

import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.AuthLoginLogQueryRequest;

/**
 * 登录记录参数检查器（对应 AuthLoginLogController）。
 */
public final class AuthLoginLogParamChecker {

    private AuthLoginLogParamChecker() {
    }

    /** 检查登录记录分页查询参数（放宽：为空跳过）。 */
    public static void checkLoginLogQuery(AuthLoginLogQueryRequest request) {
        if (request == null) {
            return;
        }
        ParamValidator.validate(request);
    }

    /** 检查日志ID路径参数。 */
    public static void checkLogId(Long logId) {
        AssertUtil.throwErrWhenNull(logId, ErrorCodeEnum.PARAM_INVALID, "日志ID不能为空");
    }
}
