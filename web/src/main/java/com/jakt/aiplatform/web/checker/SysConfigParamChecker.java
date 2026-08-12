package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.SysConfigCreateRequest;
import com.jakt.aiplatform.web.param.SysConfigQueryRequest;
import com.jakt.aiplatform.web.param.SysConfigUpdateRequest;

/**
 * 参数配置参数检查器
 */
public class SysConfigParamChecker {

    private SysConfigParamChecker() {
    }

    /**
     * 检查参数配置创建参数。
     *
     * @param request 参数配置创建请求
     */
    public static void checkSysConfigCreateRequest(SysConfigCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查参数配置更新参数。
     *
     * @param request 参数配置更新请求
     */
    public static void checkSysConfigUpdateRequest(SysConfigUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查参数配置 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 参数配置 ID
     */
    public static void checkId(Long id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "参数配置ID不能为空");
    }

    /**
     * 检查参数配置查询参数
     *
     * @param request 参数配置查询请求，可为 null
     */
    public static void checkSysConfigQueryRequest(SysConfigQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
