package com.jakt.aiplatform.web.checker;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;

import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.ParamValidator;
import com.jakt.aiplatform.web.param.AuthRoleCreateRequest;
import com.jakt.aiplatform.web.param.AuthRoleMenuRequest;
import com.jakt.aiplatform.web.param.AuthRoleQueryRequest;
import com.jakt.aiplatform.web.param.AuthRoleStatusRequest;
import com.jakt.aiplatform.web.param.AuthRoleUpdateRequest;

/**
 * 角色参数检查器（对应 AuthRoleController）。
 */
public final class AuthRoleParamChecker {

    private AuthRoleParamChecker() {
    }

    /** 检查角色分页查询参数（放宽：为空跳过）。 */
    public static void checkRoleQuery(AuthRoleQueryRequest request) {
        if (request == null) {
            return;
        }
        ParamValidator.validate(request);
    }

    /** 检查新增角色参数。 */
    public static void checkRoleCreate(AuthRoleCreateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "角色参数不能为空");
        ParamValidator.validate(request);
    }

    /** 检查修改角色参数。 */
    public static void checkRoleUpdate(AuthRoleUpdateRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "角色参数不能为空");
        ParamValidator.validate(request);
    }

    /** 检查分配菜单参数。 */
    public static void checkRoleMenu(AuthRoleMenuRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "分配菜单参数不能为空");
        ParamValidator.validate(request);
    }

    /** 检查角色启停参数。 */
    public static void checkRoleStatus(AuthRoleStatusRequest request) {
        AssertUtil.throwErrWhenNull(request, ErrorCodeEnum.PARAM_INVALID, "启停参数不能为空");
        ParamValidator.validate(request);
    }

    /** 检查角色ID路径参数。 */
    public static void checkRoleId(Long roleId) {
        AssertUtil.throwErrWhenNull(roleId, ErrorCodeEnum.PARAM_INVALID, "角色ID不能为空");
    }
}
