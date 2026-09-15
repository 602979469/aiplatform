package com.jakt.aiplatform.web.checker;

import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.web.param.CdcMappingSaveRequest;
import com.jakt.aiplatform.web.param.CdcSqlFormatRequest;
import com.jakt.aiplatform.web.param.CdcTaskRequest;

/**
 * ES 同步配置参数校验器。
 */
public final class CdcSyncParamChecker {

    private CdcSyncParamChecker() {
    }

    /**
     * 校验保存请求。
     *
     * @param request 保存请求
     */
    public static void checkSave(CdcMappingSaveRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "同步配置不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 校验任务操作请求。
     *
     * @param request 任务操作请求
     */
    public static void checkTask(CdcTaskRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "任务参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 校验 SQL 格式化请求。
     *
     * @param request 格式化请求
     */
    public static void checkFormat(CdcSqlFormatRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "SQL 不能为空");
        ParamValidator.validate(request);
    }
}
