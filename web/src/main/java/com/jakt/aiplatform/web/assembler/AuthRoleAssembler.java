package com.jakt.aiplatform.web.assembler;

import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.AuthRole;
import com.jakt.aiplatform.core.model.param.AuthRoleQueryParam;
import com.jakt.aiplatform.web.param.AuthRoleQueryRequest;
import com.jakt.aiplatform.web.param.AuthRoleCreateRequest;
import com.jakt.aiplatform.web.param.AuthRoleUpdateRequest;
import com.jakt.aiplatform.web.result.AuthRoleResponse;

import java.util.List;

/**
 * 角色域组装器：DTO↔Model 与角色响应转换。
 */
public final class AuthRoleAssembler {

    private AuthRoleAssembler() {
    }

    /**
     * 角色 → 角色响应。
     *
     * @param role 角色；为空返回 null
     * @return 角色响应
     */
    public static AuthRoleResponse toRoleResponse(AuthRole role) {
        if (role == null) {
            return null;
        }
        AuthRoleResponse response = new AuthRoleResponse();
        response.setRoleId(role.getRoleId());
        response.setRoleName(role.getRoleName());
        response.setRoleKey(role.getRoleKey());
        response.setRoleSort(role.getRoleSort());
        response.setStatus(role.getStatus());
        response.setRemark(role.getRemark());
        response.setCreateTime(role.getCreateTime());
        return response;
    }

    /**
     * 新增角色请求 → 角色领域模型。
     *
     * @param request 新增角色请求
     * @return 角色领域模型
     */
    public static AuthRole toRole(AuthRoleCreateRequest request) {
        if (request == null) {
            return null;
        }
        AuthRole role = new AuthRole();
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setRoleSort(request.getRoleSort());
        role.setStatus(request.getStatus());
        role.setRemark(request.getRemark());
        return role;
    }

    /**
     * 修改角色请求 → 角色领域模型（部分更新）。
     *
     * @param request 修改角色请求
     * @return 角色领域模型
     */
    public static AuthRole toRole(AuthRoleUpdateRequest request) {
        if (request == null) {
            return null;
        }
        AuthRole role = new AuthRole();
        role.setRoleId(request.getRoleId());
        role.setRoleName(request.getRoleName());
        role.setRoleKey(request.getRoleKey());
        role.setRoleSort(request.getRoleSort());
        role.setStatus(request.getStatus());
        role.setRemark(request.getRemark());
        return role;
    }

    /**
     * 角色查询请求 → 查询参数（分页缺省走 PageParam 默认值）。
     *
     * @param request 查询请求；为空返回空查询参数
     * @return 查询参数
     */
    public static AuthRoleQueryParam toQueryParam(AuthRoleQueryRequest request) {
        if (request == null) {
            return new AuthRoleQueryParam();
        }
        AuthRoleQueryParam query = new AuthRoleQueryParam();
        if (request.getPageNum() != null) {
            query.setPageNum(request.getPageNum());
        }
        if (request.getPageSize() != null) {
            query.setPageSize(request.getPageSize());
        }
        query.setRoleName(request.getRoleName());
        query.setRoleKey(request.getRoleKey());
        query.setStatus(request.getStatus());
        return query;
    }
}
