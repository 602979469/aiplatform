package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.AuthUserManager;
import com.jakt.aiplatform.core.model.domain.AuthUser;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.param.AuthUserQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;
import com.jakt.aiplatform.core.service.AuthUserAdminService;
import org.springframework.stereotype.Service;

import java.util.List;

    /** 用户管理用例编排实现：业务委托用户管理领域服务。 */
@Service
public class AuthUserManagerImpl implements AuthUserManager {

    private final AuthUserAdminService authUserAdminService;

    public AuthUserManagerImpl(AuthUserAdminService authUserAdminService) {
        this.authUserAdminService = authUserAdminService;
    }

    @Override
    public PageResult<AuthUser> pageUser(AuthUserQueryParam query) {
        return authUserAdminService.pageUser(query);
    }

    @Override
    public AuthUser getUser(Long userId) {
        return authUserAdminService.getUser(userId);
    }

    @Override
    public AuthUser createUser(AuthUser user, List<Long> roleIds) {
        return authUserAdminService.createUser(user, roleIds);
    }

    @Override
    public void updateUser(AuthUser user) {
        authUserAdminService.updateUser(user);
    }

    @Override
    public void changeUserStatus(Long userId, EnableStatusEnum status) {
        authUserAdminService.changeUserStatus(userId, status);
    }

    @Override
    public void resetPassword(Long userId, String password) {
        authUserAdminService.resetPassword(userId, password);
    }

    @Override
    public void assignUserRoles(Long userId, List<Long> roleIds) {
        authUserAdminService.assignUserRoles(userId, roleIds);
    }

    @Override
    public void deleteUser(Long userId) {
        authUserAdminService.deleteUser(userId);
    }
}
