package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.KbExamPaperQuestionCreateRequest;
import com.jakt.aiplatform.web.param.KbExamPaperQuestionQueryRequest;
import com.jakt.aiplatform.web.param.KbExamPaperQuestionUpdateRequest;

/**
 * 试卷题目快照与作答参数检查器。
 */
public final class KbExamPaperQuestionParamChecker {

    private KbExamPaperQuestionParamChecker() {
    }

    /**
     * 检查试卷题目快照与作答创建参数。
     *
     * @param request 试卷题目快照与作答创建请求
     */
    public static void checkKbExamPaperQuestionCreateRequest(KbExamPaperQuestionCreateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查试卷题目快照与作答更新参数。
     *
     * @param request 试卷题目快照与作答更新请求
     */
    public static void checkKbExamPaperQuestionUpdateRequest(KbExamPaperQuestionUpdateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查试卷题目快照与作答主键参数（按主键查询/更新/删除共用）。
     *
     * @param id 试卷题目快照与作答主键
     */
    public static void checkId(Long id) {
        AssertUtil.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "试卷题目快照与作答ID不能为空");
    }

    /**
     * 检查试卷题目快照与作答查询参数。
     *
     * @param request 试卷题目快照与作答查询请求，可为 null（缺省分页）
     */
    public static void checkKbExamPaperQuestionQueryRequest(KbExamPaperQuestionQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        ParamValidator.validate(request);
    }
}
