package com.jakt.aiplatform.web.checker;

import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.web.param.SysLogDetailRequest;
import com.jakt.aiplatform.web.param.SysLogQueryRequest;

public class SysLogParamChecker {

    /**
     * 检查系统日志查询请求参数
     * @param request 系统日志查询请求参数
     */
    public static void checkSysLogQueryRequest(SysLogQueryRequest request){
        if (request == null){
            return;
        }
        ParamValidator.validate(request);
    }

    /**
     * 检查系统日志详情请求参数
     * @param request 系统日志详情请求参数
     */
    public static void checkSysLogDetailRequest(SysLogDetailRequest request){
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "参数不能为空");
        String fileName = request.getFileName();
        AssertUtil.throwErrWhenBlank(fileName, ErrorCodeEnum.PARAM_INVALID, "文件名不能为空");
    }
}
