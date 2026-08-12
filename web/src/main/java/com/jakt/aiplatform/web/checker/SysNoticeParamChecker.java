package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.SysNoticeCreateRequest;
import com.jakt.aiplatform.web.param.SysNoticeQueryRequest;
import com.jakt.aiplatform.web.param.SysNoticeUpdateRequest;

/**
 * 通知公告参数检查器
 */
public class SysNoticeParamChecker {

    private SysNoticeParamChecker() {
    }

    /**
     * 检查通知公告创建参数。
     *
     * @param request 通知公告创建请求
     */
    public static void checkSysNoticeCreateRequest(SysNoticeCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查通知公告更新参数。
     *
     * @param request 通知公告更新请求
     */
    public static void checkSysNoticeUpdateRequest(SysNoticeUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查通知公告 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 通知公告 ID
     */
    public static void checkId(Long id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "通知公告ID不能为空");
    }

    /**
     * 检查通知公告查询参数
     *
     * @param request 通知公告查询请求，可为 null
     */
    public static void checkSysNoticeQueryRequest(SysNoticeQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
