package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.GenTableCreateRequest;
import com.jakt.aiplatform.web.param.GenTableQueryRequest;
import com.jakt.aiplatform.web.param.GenTableUpdateRequest;

/**
 * 代码生成参数检查器
 */
public class GenTableParamChecker {

    private GenTableParamChecker() {
    }

    /**
     * 检查代码生成创建参数。
     *
     * @param request 代码生成创建请求
     */
    public static void checkGenTableCreateRequest(GenTableCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查代码生成更新参数。
     *
     * @param request 代码生成更新请求
     */
    public static void checkGenTableUpdateRequest(GenTableUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查代码生成 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 代码生成 ID
     */
    public static void checkId(Long id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "代码生成ID不能为空");
    }

    /**
     * 检查代码生成查询参数
     *
     * @param request 代码生成查询请求，可为 null
     */
    public static void checkGenTableQueryRequest(GenTableQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
