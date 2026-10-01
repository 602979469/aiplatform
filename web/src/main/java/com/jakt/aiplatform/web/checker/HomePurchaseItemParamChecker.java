package com.jakt.aiplatform.web.checker;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.web.param.HomePurchaseItemCreateRequest;
import com.jakt.aiplatform.web.param.HomePurchaseItemQueryRequest;
import com.jakt.aiplatform.web.param.HomePurchaseItemRecommendRequest;
import com.jakt.aiplatform.web.param.HomePurchaseItemParseRequest;
import com.jakt.aiplatform.web.param.HomePurchaseItemUpdateRequest;

/**
 * 家庭装修采购项参数检查器。
 *
 * <p>只做参数形态校验；类型是否在配置内、预算格式是否合法由 core-service 校验。
 */
public final class HomePurchaseItemParamChecker {

    private HomePurchaseItemParamChecker() {
    }

    /**
     * 检查创建参数。
     *
     * @param request 创建请求
     */
    public static void checkHomePurchaseItemCreateRequest(HomePurchaseItemCreateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "创建参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查更新参数。
     *
     * @param request 更新请求
     */
    public static void checkHomePurchaseItemUpdateRequest(HomePurchaseItemUpdateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "更新参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查主键参数（查询/更新/删除共用）。
     *
     * @param id 主键
     */
    public static void checkId(Long id) {
        AssertUtil.throwErrWhenNull(id, ErrorCodeEnum.PARAM_INVALID, "采购项ID不能为空");
    }

    /**
     * 检查查询参数。
     *
     * @param request 查询请求，可为 null（缺省分页）
     */
    public static void checkHomePurchaseItemQueryRequest(HomePurchaseItemQueryRequest request) {
        if (ObjectUtil.isNull(request)) {
            return;
        }
        ParamValidator.validate(request);
    }

    /**
     * 检查 AI 推荐参数。
     *
     * @param request 推荐请求
     */
    public static void checkHomePurchaseItemRecommendRequest(HomePurchaseItemRecommendRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "推荐参数不能为空");
        ParamValidator.validate(request);
    }

    /**
     * 检查一句话录入参数。
     *
     * @param request 一句话录入请求
     */
    public static void checkHomePurchaseItemParseRequest(HomePurchaseItemParseRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "参数不能为空");
        ParamValidator.validate(request);
    }
}
