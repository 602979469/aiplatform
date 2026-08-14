package com.jakt.aiplatform.core.service.impl;

import cn.dev33.satoken.stp.StpInterface;
import cn.hutool.core.convert.Convert;
import com.jakt.aiplatform.core.repository.AuthMenuRepository;
import com.jakt.aiplatform.core.repository.AuthRoleRepository;
import org.springframework.stereotype.Component;

import java.util.List;

/**
 * Sa-Token 权限数据源：每次校验实时查库，角色含 admin 时返回上帝权限 "*"。
 */
@Component
public class AuthStpInterfaceImpl implements StpInterface {

    private final AuthRoleRepository authRoleRepository;

    private final AuthMenuRepository authMenuRepository;

    public AuthStpInterfaceImpl(AuthRoleRepository authRoleRepository, AuthMenuRepository authMenuRepository) {
        this.authRoleRepository = authRoleRepository;
        this.authMenuRepository = authMenuRepository;
    }

    @Override
    public List<String> getRoleList(Object loginId, String loginType) {
        return authRoleRepository.findRoleKeysByUserId(Convert.toLong(loginId));
    }

    @Override
    public List<String> getPermissionList(Object loginId, String loginType) {
        List<String> roles = getRoleList(loginId, loginType);
        if (roles.contains("admin")) {
            return List.of("*");
        }
        return authMenuRepository.findPermsByUserId(Convert.toLong(loginId));
    }
}
