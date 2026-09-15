package com.jakt.aiplatform.web.checker;

import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.web.param.KbQuestionQueryRequest;
import com.jakt.aiplatform.web.param.KbQuestionSaveRequest;

/**
 * 题库管理参数校验器。
 */
public final class KbQuestionAdminParamChecker {

    private KbQuestionAdminParamChecker() {
    }

    /**
     * 校验分页查询请求。
     *
     * @param request 查询请求
     */
    public static void checkQuery(KbQuestionQueryRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "查询参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 校验新增/修改请求。
     *
     * @param request 保存请求
     */
    public static void checkSave(KbQuestionSaveRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "题目参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 校验题目ID。
     *
     * @param id 题目ID
     */
    public static void checkId(Long id) {
        AssertUtil.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "题目ID不能为空");
    }
}
