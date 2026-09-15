package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.KbExamTemplateCreateRequest;
import com.jakt.aiplatform.web.param.KbExamTemplateQueryRequest;
import com.jakt.aiplatform.web.param.KbExamTemplateUpdateRequest;

/**
 * 试卷模板参数检查器。
 */
public final class KbExamTemplateParamChecker {

    private KbExamTemplateParamChecker() {
    }

    /**
     * 检查试卷模板创建参数。
     *
     * @param request 试卷模板创建请求
     */
    public static void checkKbExamTemplateCreateRequest(KbExamTemplateCreateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查试卷模板更新参数。
     *
     * @param request 试卷模板更新请求
     */
    public static void checkKbExamTemplateUpdateRequest(KbExamTemplateUpdateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查试卷模板主键参数（按主键查询/更新/删除共用）。
     *
     * @param id 试卷模板主键
     */
    public static void checkId(Long id) {
        AssertUtil.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "试卷模板ID不能为空");
    }

    /**
     * 检查试卷模板查询参数。
     *
     * @param request 试卷模板查询请求，可为 null（缺省分页）
     */
    public static void checkKbExamTemplateQueryRequest(KbExamTemplateQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        ParamValidator.validate(request);
    }
}
