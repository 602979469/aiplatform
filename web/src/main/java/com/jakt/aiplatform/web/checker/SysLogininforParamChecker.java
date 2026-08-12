package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.SysLogininforCreateRequest;
import com.jakt.aiplatform.web.param.SysLogininforQueryRequest;
import com.jakt.aiplatform.web.param.SysLogininforUpdateRequest;

/**
 * 登录日志参数检查器
 */
public class SysLogininforParamChecker {

    private SysLogininforParamChecker() {
    }

    /**
     * 检查登录日志创建参数。
     *
     * @param request 登录日志创建请求
     */
    public static void checkSysLogininforCreateRequest(SysLogininforCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查登录日志更新参数。
     *
     * @param request 登录日志更新请求
     */
    public static void checkSysLogininforUpdateRequest(SysLogininforUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查登录日志 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 登录日志 ID
     */
    public static void checkId(Long id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "登录日志ID不能为空");
    }

    /**
     * 检查登录日志查询参数
     *
     * @param request 登录日志查询请求，可为 null
     */
    public static void checkSysLogininforQueryRequest(SysLogininforQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
