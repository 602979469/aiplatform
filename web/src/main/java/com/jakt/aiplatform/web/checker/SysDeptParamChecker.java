package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.SysDeptCreateRequest;
import com.jakt.aiplatform.web.param.SysDeptQueryRequest;
import com.jakt.aiplatform.web.param.SysDeptUpdateRequest;

/**
 * 部门参数检查器
 */
public class SysDeptParamChecker {

    private SysDeptParamChecker() {
    }

    /**
     * 检查部门创建参数。
     *
     * @param request 部门创建请求
     */
    public static void checkSysDeptCreateRequest(SysDeptCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查部门更新参数。
     *
     * @param request 部门更新请求
     */
    public static void checkSysDeptUpdateRequest(SysDeptUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查部门 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 部门 ID
     */
    public static void checkId(Long id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "部门ID不能为空");
    }

    /**
     * 检查部门查询参数
     *
     * @param request 部门查询请求，可为 null
     */
    public static void checkSysDeptQueryRequest(SysDeptQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
