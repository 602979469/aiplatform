package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.common.util.tools.AiPlatformParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.SysNoticeReadCreateRequest;
import com.jakt.aiplatform.web.param.SysNoticeReadQueryRequest;
import com.jakt.aiplatform.web.param.SysNoticeReadUpdateRequest;

/**
 * 公告已读记录参数检查器
 */
public class SysNoticeReadParamChecker {

    private SysNoticeReadParamChecker() {
    }

    /**
     * 检查公告已读记录创建参数。
     *
     * @param request 公告已读记录创建请求
     */
    public static void checkSysNoticeReadCreateRequest(SysNoticeReadCreateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查公告已读记录更新参数。
     *
     * @param request 公告已读记录更新请求
     */
    public static void checkSysNoticeReadUpdateRequest(SysNoticeReadUpdateRequest request) {
        AiPlatformInvoker.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        AiPlatformParamValidator.validate(request);
    }

    /**
     * 检查公告已读记录 ID 参数（按 ID 查询/删除共用）。
     *
     * @param id 公告已读记录 ID
     */
    public static void checkId(Long id) {
        AiPlatformInvoker.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "公告已读记录ID不能为空");
    }

    /**
     * 检查公告已读记录查询参数
     *
     * @param request 公告已读记录查询请求，可为 null
     */
    public static void checkSysNoticeReadQueryRequest(SysNoticeReadQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        AiPlatformParamValidator.validate(request);
    }
}
