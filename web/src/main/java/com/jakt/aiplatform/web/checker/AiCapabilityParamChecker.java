package com.jakt.aiplatform.web.checker;

import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.web.param.AiCapabilityInvokeRequest;

/**
 * AI 能力参数检查器。
 */
public final class AiCapabilityParamChecker {

    private AiCapabilityParamChecker() {
    }

    /**
     * 检查能力调用参数。
     *
     * @param request 调用请求
     */
    public static void checkInvokeRequest(AiCapabilityInvokeRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "参数不能为空");
        ParamValidator.validate(request);
    }
}
