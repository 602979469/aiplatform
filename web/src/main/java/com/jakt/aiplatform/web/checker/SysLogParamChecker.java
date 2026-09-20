package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.web.param.SysLogDetailRequest;
import com.jakt.aiplatform.web.param.SysLogQueryRequest;

/**
 * 系统日志请求参数检查器：只做请求级校验，不判断业务存在性。
 */
public final class SysLogParamChecker {

    private SysLogParamChecker() {
    }

    /**
     * 检查系统日志查询请求参数（查询类请求为空时跳过校验）。
     *
     * @param request 系统日志查询请求参数
     */
    public static void checkSysLogQueryRequest(SysLogQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        ParamValidator.validate(request);
    }

    /**
     * 检查系统日志详情请求参数（强校验：请求与文件名均必填）。
     *
     * @param request 系统日志详情请求参数
     */
    public static void checkSysLogDetailRequest(SysLogDetailRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "参数不能为空");
        String fileName = request.getFileName();
        AssertUtil.throwErrWhenBlank(fileName, ErrorCodeEnum.PARAM_INVALID, "文件名不能为空");
    }
}
