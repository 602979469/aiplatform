package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.AuthRoleManager;
import com.jakt.aiplatform.core.model.domain.AuthRole;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.param.AuthRoleQueryParam;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.service.AuthRoleAdminService;
import org.springframework.stereotype.Service;

import java.util.List;

    /** 角色管理用例编排实现：业务委托角色管理领域服务。 */
@Service
public class AuthRoleManagerImpl implements AuthRoleManager {

    private final AuthRoleAdminService authRoleAdminService;

    public AuthRoleManagerImpl(AuthRoleAdminService authRoleAdminService) {
        this.authRoleAdminService = authRoleAdminService;
    }

    @Override
    public PageResult<AuthRole> pageRole(AuthRoleQueryParam query) {
        return authRoleAdminService.pageRole(query);
    }

    @Override
    public AuthRole getRole(Long roleId) {
        return authRoleAdminService.getRole(roleId);
    }

    @Override
    public AuthRole createRole(AuthRole role) {
        return authRoleAdminService.createRole(role);
    }

    @Override
    public void updateRole(AuthRole role) {
        authRoleAdminService.updateRole(role);
    }

    @Override
    public void changeRoleStatus(Long roleId, EnableStatusEnum status) {
        authRoleAdminService.changeRoleStatus(roleId, status);
    }

    @Override
    public void assignRoleMenus(Long roleId, List<Long> menuIds) {
        authRoleAdminService.assignRoleMenus(roleId, menuIds);
    }

    @Override
    public List<Long> getRoleMenuIds(Long roleId) {
        return authRoleAdminService.getRoleMenuIds(roleId);
    }

    @Override
    public void deleteRole(Long roleId) {
        authRoleAdminService.deleteRole(roleId);
    }
}
