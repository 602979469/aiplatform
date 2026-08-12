package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.SysRoleCreateRequest;
import com.jakt.aiplatform.web.param.SysRoleQueryRequest;
import com.jakt.aiplatform.web.param.SysRoleUpdateRequest;

/**
 * 角色参数检查器
 */
public class SysRoleParamChecker {

    private SysRoleParamChecker() {
    }

    /**
     * 检查角色创建参数。
     *
     * @param request 角色创建请求
     */
    public static void checkSysRoleCreateRequest(SysRoleCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查角色更新参数。
     *
     * @param request 角色更新请求
     */
    public static void checkSysRoleUpdateRequest(SysRoleUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查角色 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 角色 ID
     */
    public static void checkId(Long id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "角色ID不能为空");
    }

    /**
     * 检查角色查询参数
     *
     * @param request 角色查询请求，可为 null
     */
    public static void checkSysRoleQueryRequest(SysRoleQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
