package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.SysPostCreateRequest;
import com.jakt.aiplatform.web.param.SysPostQueryRequest;
import com.jakt.aiplatform.web.param.SysPostUpdateRequest;

/**
 * 岗位参数检查器
 */
public class SysPostParamChecker {

    private SysPostParamChecker() {
    }

    /**
     * 检查岗位创建参数。
     *
     * @param request 岗位创建请求
     */
    public static void checkSysPostCreateRequest(SysPostCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查岗位更新参数。
     *
     * @param request 岗位更新请求
     */
    public static void checkSysPostUpdateRequest(SysPostUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查岗位 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 岗位 ID
     */
    public static void checkId(Long id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "岗位ID不能为空");
    }

    /**
     * 检查岗位查询参数
     *
     * @param request 岗位查询请求，可为 null
     */
    public static void checkSysPostQueryRequest(SysPostQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
