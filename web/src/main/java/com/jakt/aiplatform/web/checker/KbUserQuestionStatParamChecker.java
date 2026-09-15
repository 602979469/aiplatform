package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.KbUserQuestionStatCreateRequest;
import com.jakt.aiplatform.web.param.KbUserQuestionStatQueryRequest;
import com.jakt.aiplatform.web.param.KbUserQuestionStatUpdateRequest;

/**
 * 用户题目掌握状态参数检查器。
 */
public final class KbUserQuestionStatParamChecker {

    private KbUserQuestionStatParamChecker() {
    }

    /**
     * 检查用户题目掌握状态创建参数。
     *
     * @param request 用户题目掌握状态创建请求
     */
    public static void checkKbUserQuestionStatCreateRequest(KbUserQuestionStatCreateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查用户题目掌握状态更新参数。
     *
     * @param request 用户题目掌握状态更新请求
     */
    public static void checkKbUserQuestionStatUpdateRequest(KbUserQuestionStatUpdateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查用户题目掌握状态主键参数（按主键查询/更新/删除共用）。
     *
     * @param id 用户题目掌握状态主键
     */
    public static void checkId(Long id) {
        AssertUtil.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "用户题目掌握状态ID不能为空");
    }

    /**
     * 检查用户题目掌握状态查询参数。
     *
     * @param request 用户题目掌握状态查询请求，可为 null（缺省分页）
     */
    public static void checkKbUserQuestionStatQueryRequest(KbUserQuestionStatQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        ParamValidator.validate(request);
    }
}
