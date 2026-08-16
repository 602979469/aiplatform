package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.AuthMenu;

import java.util.List;

/**
 * 菜单领域服务：按用户组装路由树。
 */
public interface AuthMenuService {

    /**
     * 获取用户可见菜单树（M目录/C菜单）。
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    List<AuthMenu> getRouters(Long userId);
}
