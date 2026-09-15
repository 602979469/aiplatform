package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.KbExamPaperCreateRequest;
import com.jakt.aiplatform.web.param.KbExamPaperQueryRequest;
import com.jakt.aiplatform.web.param.KbExamPaperUpdateRequest;

/**
 * 考试试卷参数检查器。
 */
public final class KbExamPaperParamChecker {

    private KbExamPaperParamChecker() {
    }

    /**
     * 检查考试试卷创建参数。
     *
     * @param request 考试试卷创建请求
     */
    public static void checkKbExamPaperCreateRequest(KbExamPaperCreateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查考试试卷更新参数。
     *
     * @param request 考试试卷更新请求
     */
    public static void checkKbExamPaperUpdateRequest(KbExamPaperUpdateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查考试试卷主键参数（按主键查询/更新/删除共用）。
     *
     * @param id 考试试卷主键
     */
    public static void checkId(Long id) {
        AssertUtil.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "考试试卷ID不能为空");
    }

    /**
     * 检查考试试卷查询参数。
     *
     * @param request 考试试卷查询请求，可为 null（缺省分页）
     */
    public static void checkKbExamPaperQueryRequest(KbExamPaperQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        ParamValidator.validate(request);
    }
}
