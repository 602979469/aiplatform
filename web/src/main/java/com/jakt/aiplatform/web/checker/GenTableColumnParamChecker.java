package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.GenTableColumnCreateRequest;
import com.jakt.aiplatform.web.param.GenTableColumnQueryRequest;
import com.jakt.aiplatform.web.param.GenTableColumnUpdateRequest;

/**
 * 代码生成字段参数检查器
 */
public class GenTableColumnParamChecker {

    private GenTableColumnParamChecker() {
    }

    /**
     * 检查代码生成字段创建参数。
     *
     * @param request 代码生成字段创建请求
     */
    public static void checkGenTableColumnCreateRequest(GenTableColumnCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查代码生成字段更新参数。
     *
     * @param request 代码生成字段更新请求
     */
    public static void checkGenTableColumnUpdateRequest(GenTableColumnUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查代码生成字段 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 代码生成字段 ID
     */
    public static void checkId(Long id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "代码生成字段ID不能为空");
    }

    /**
     * 检查代码生成字段查询参数
     *
     * @param request 代码生成字段查询请求，可为 null
     */
    public static void checkGenTableColumnQueryRequest(GenTableColumnQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
