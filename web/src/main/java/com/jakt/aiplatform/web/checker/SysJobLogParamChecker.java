package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.SysJobLogCreateRequest;
import com.jakt.aiplatform.web.param.SysJobLogQueryRequest;
import com.jakt.aiplatform.web.param.SysJobLogUpdateRequest;

/**
 * 定时任务日志参数检查器
 */
public class SysJobLogParamChecker {

    private SysJobLogParamChecker() {
    }

    /**
     * 检查定时任务日志创建参数。
     *
     * @param request 定时任务日志创建请求
     */
    public static void checkSysJobLogCreateRequest(SysJobLogCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查定时任务日志更新参数。
     *
     * @param request 定时任务日志更新请求
     */
    public static void checkSysJobLogUpdateRequest(SysJobLogUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查定时任务日志 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 定时任务日志 ID
     */
    public static void checkId(Long id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "定时任务日志ID不能为空");
    }

    /**
     * 检查定时任务日志查询参数
     *
     * @param request 定时任务日志查询请求，可为 null
     */
    public static void checkSysJobLogQueryRequest(SysJobLogQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
