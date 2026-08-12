package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.SysMenuCreateRequest;
import com.jakt.aiplatform.web.param.SysMenuQueryRequest;
import com.jakt.aiplatform.web.param.SysMenuUpdateRequest;

/**
 * 菜单参数检查器
 */
public class SysMenuParamChecker {

    private SysMenuParamChecker() {
    }

    /**
     * 检查菜单创建参数。
     *
     * @param request 菜单创建请求
     */
    public static void checkSysMenuCreateRequest(SysMenuCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查菜单更新参数。
     *
     * @param request 菜单更新请求
     */
    public static void checkSysMenuUpdateRequest(SysMenuUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查菜单 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 菜单 ID
     */
    public static void checkId(Long id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "菜单ID不能为空");
    }

    /**
     * 检查菜单查询参数
     *
     * @param request 菜单查询请求，可为 null
     */
    public static void checkSysMenuQueryRequest(SysMenuQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
