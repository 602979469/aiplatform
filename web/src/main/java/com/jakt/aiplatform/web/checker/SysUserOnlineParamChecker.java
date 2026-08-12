package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.SysUserOnlineCreateRequest;
import com.jakt.aiplatform.web.param.SysUserOnlineQueryRequest;
import com.jakt.aiplatform.web.param.SysUserOnlineUpdateRequest;

/**
 * 在线用户参数检查器
 */
public class SysUserOnlineParamChecker {

    private SysUserOnlineParamChecker() {
    }

    /**
     * 检查在线用户创建参数。
     *
     * @param request 在线用户创建请求
     */
    public static void checkSysUserOnlineCreateRequest(SysUserOnlineCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查在线用户更新参数。
     *
     * @param request 在线用户更新请求
     */
    public static void checkSysUserOnlineUpdateRequest(SysUserOnlineUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查在线用户 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 在线用户 ID
     */
    public static void checkId(String id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "在线用户ID不能为空");
    }

    /**
     * 检查在线用户查询参数
     *
     * @param request 在线用户查询请求，可为 null
     */
    public static void checkSysUserOnlineQueryRequest(SysUserOnlineQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
