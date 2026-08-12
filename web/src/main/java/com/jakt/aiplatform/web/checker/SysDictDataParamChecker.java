package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.SysDictDataCreateRequest;
import com.jakt.aiplatform.web.param.SysDictDataQueryRequest;
import com.jakt.aiplatform.web.param.SysDictDataUpdateRequest;

/**
 * 字典数据参数检查器
 */
public class SysDictDataParamChecker {

    private SysDictDataParamChecker() {
    }

    /**
     * 检查字典数据创建参数。
     *
     * @param request 字典数据创建请求
     */
    public static void checkSysDictDataCreateRequest(SysDictDataCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查字典数据更新参数。
     *
     * @param request 字典数据更新请求
     */
    public static void checkSysDictDataUpdateRequest(SysDictDataUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查字典数据 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 字典数据 ID
     */
    public static void checkId(Long id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "字典数据ID不能为空");
    }

    /**
     * 检查字典数据查询参数
     *
     * @param request 字典数据查询请求，可为 null
     */
    public static void checkSysDictDataQueryRequest(SysDictDataQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
