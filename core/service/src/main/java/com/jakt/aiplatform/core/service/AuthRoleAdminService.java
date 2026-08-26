package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.AuthRole;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.param.AuthRoleQueryParam;
import com.jakt.aiplatform.common.framework.result.PageResult;

import java.util.List;

/**
 * 角色管理领域服务：角色 CRUD、菜单分配。
 */
public interface AuthRoleAdminService {

    /**
     * 分页查询角色。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<AuthRole> pageRole(AuthRoleQueryParam query);

    /**
     * 查询角色详情。
     *
     * @param roleId 角色ID
     * @return 角色
     */
    AuthRole getRole(Long roleId);

    /**
     * 新增角色。
     *
     * @param role 角色
     * @return 新增后的角色
     */
    AuthRole createRole(AuthRole role);

    /**
     * 修改角色。
     *
     * @param role 角色（含 roleId）
     */
    void updateRole(AuthRole role);

    /**
     * 修改角色状态。
     *
     * @param roleId 角色ID
     * @param status 新状态
     */
    void changeRoleStatus(Long roleId, EnableStatusEnum status);

    /**
     * 分配角色菜单。
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表
     */
    void assignRoleMenus(Long roleId, List<Long> menuIds);

    /**
     * 查询角色已分配菜单ID。
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> getRoleMenuIds(Long roleId);

    /**
     * 删除角色（清理角色菜单/用户角色关联）。
     *
     * @param roleId 角色ID
     */
    void deleteRole(Long roleId);
}
