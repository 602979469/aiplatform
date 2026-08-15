package com.jakt.aiplatform.biz.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import com.jakt.aiplatform.biz.service.AuthManager;
import com.jakt.aiplatform.core.model.dto.AuthLoginInfo;
import com.jakt.aiplatform.core.model.dto.AuthUserInfo;
import com.jakt.aiplatform.core.service.AuthService;
import org.springframework.stereotype.Service;

/**
 * 认证管理实现：当前用户角色/权限取自 Sa-Token（StpInterface 实时加载）。
 */
@Service
public class AuthManagerImpl implements AuthManager {

    private final AuthService authService;

    public AuthManagerImpl(AuthService authService) {
        this.authService = authService;
    }

    @Override
    public AuthLoginInfo login(String username, String password) {
        return authService.login(username, password);
    }

    @Override
    public AuthLoginInfo register(String username, String password, String nickname, String email) {
        return authService.register(username, password, nickname, email);
    }

    @Override
    public void logout() {
        authService.logout();
    }

    @Override
    public AuthUserInfo getInfo() {
        AuthUserInfo info = new AuthUserInfo();
        info.setUser(authService.getCurrentUser());
        info.setRoles(StpUtil.getRoleList());
        info.setPerms(StpUtil.getPermissionList());
        return info;
    }

}
