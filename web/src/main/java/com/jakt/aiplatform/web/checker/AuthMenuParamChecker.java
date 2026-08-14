package com.jakt.aiplatform.web.checker;

import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ParamValidator;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.param.AuthMenuQueryRequest;
import com.jakt.aiplatform.web.param.AuthMenuRequest;

/**
 * 菜单参数检查器（对应 AuthMenuController）。
 */
public final class AuthMenuParamChecker {

    private AuthMenuParamChecker() {
    }

    /** 检查菜单查询参数（放宽：为空跳过）。 */
    public static void checkMenuQuery(AuthMenuQueryRequest request) {
        if (request == null) {
            return;
        }
        ParamValidator.validate(request);
    }

    /** 检查菜单参数。 */
    public static void checkMenu(AuthMenuRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "菜单参数不能为空");
        ParamValidator.validate(request);
    }

    /** 检查菜单ID路径参数。 */
    public static void checkMenuId(Long menuId) {
        AssertUtil.throwErrWhenNull(menuId, ErrorCodeEnum.PARAM_INVALID, "菜单ID不能为空");
    }
}
