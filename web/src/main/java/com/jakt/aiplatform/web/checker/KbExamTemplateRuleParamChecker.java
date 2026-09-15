package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.KbExamTemplateRuleCreateRequest;
import com.jakt.aiplatform.web.param.KbExamTemplateRuleQueryRequest;
import com.jakt.aiplatform.web.param.KbExamTemplateRuleUpdateRequest;

/**
 * 试卷模板知识点规则参数检查器。
 */
public final class KbExamTemplateRuleParamChecker {

    private KbExamTemplateRuleParamChecker() {
    }

    /**
     * 检查试卷模板知识点规则创建参数。
     *
     * @param request 试卷模板知识点规则创建请求
     */
    public static void checkKbExamTemplateRuleCreateRequest(KbExamTemplateRuleCreateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查试卷模板知识点规则更新参数。
     *
     * @param request 试卷模板知识点规则更新请求
     */
    public static void checkKbExamTemplateRuleUpdateRequest(KbExamTemplateRuleUpdateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查试卷模板知识点规则主键参数（按主键查询/更新/删除共用）。
     *
     * @param id 试卷模板知识点规则主键
     */
    public static void checkId(Long id) {
        AssertUtil.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "试卷模板知识点规则ID不能为空");
    }

    /**
     * 检查试卷模板知识点规则查询参数。
     *
     * @param request 试卷模板知识点规则查询请求，可为 null（缺省分页）
     */
    public static void checkKbExamTemplateRuleQueryRequest(KbExamTemplateRuleQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        ParamValidator.validate(request);
    }
}
