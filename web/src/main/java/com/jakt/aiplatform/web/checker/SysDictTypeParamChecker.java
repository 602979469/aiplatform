package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.SysDictTypeCreateRequest;
import com.jakt.aiplatform.web.param.SysDictTypeQueryRequest;
import com.jakt.aiplatform.web.param.SysDictTypeUpdateRequest;

/**
 * 字典类型参数检查器
 */
public class SysDictTypeParamChecker {

    private SysDictTypeParamChecker() {
    }

    /**
     * 检查字典类型创建参数。
     *
     * @param request 字典类型创建请求
     */
    public static void checkSysDictTypeCreateRequest(SysDictTypeCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查字典类型更新参数。
     *
     * @param request 字典类型更新请求
     */
    public static void checkSysDictTypeUpdateRequest(SysDictTypeUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查字典类型 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 字典类型 ID
     */
    public static void checkId(Long id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "字典类型ID不能为空");
    }

    /**
     * 检查字典类型查询参数
     *
     * @param request 字典类型查询请求，可为 null
     */
    public static void checkSysDictTypeQueryRequest(SysDictTypeQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
