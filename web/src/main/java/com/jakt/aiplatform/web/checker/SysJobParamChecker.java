package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.SysJobCreateRequest;
import com.jakt.aiplatform.web.param.SysJobQueryRequest;
import com.jakt.aiplatform.web.param.SysJobUpdateRequest;

/**
 * 定时任务参数检查器
 */
public class SysJobParamChecker {

    private SysJobParamChecker() {
    }

    /**
     * 检查定时任务创建参数。
     *
     * @param request 定时任务创建请求
     */
    public static void checkSysJobCreateRequest(SysJobCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查定时任务更新参数。
     *
     * @param request 定时任务更新请求
     */
    public static void checkSysJobUpdateRequest(SysJobUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查定时任务 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 定时任务 ID
     */
    public static void checkId(Long id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "定时任务ID不能为空");
    }

    /**
     * 检查定时任务查询参数
     *
     * @param request 定时任务查询请求，可为 null
     */
    public static void checkSysJobQueryRequest(SysJobQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
