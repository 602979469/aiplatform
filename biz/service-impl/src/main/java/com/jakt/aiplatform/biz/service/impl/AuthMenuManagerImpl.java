package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.AuthMenuManager;
import com.jakt.aiplatform.core.model.context.UserContext;
import com.jakt.aiplatform.core.model.domain.AuthMenu;
import com.jakt.aiplatform.core.model.param.AuthMenuQueryParam;
import com.jakt.aiplatform.core.service.AuthMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜单管理用例编排实现：只依赖菜单领域服务（菜单 CRUD + 路由树）。
 */
@Service
public class AuthMenuManagerImpl implements AuthMenuManager {

    private final AuthMenuService authMenuService;

    public AuthMenuManagerImpl(AuthMenuService authMenuService) {
        this.authMenuService = authMenuService;
    }

    @Override
    public AuthMenu getMenu(Long menuId) {
        return authMenuService.getMenu(menuId);
    }

    @Override
    public List<AuthMenu> getRouters() {
        Long userId = UserContext.getUserId();
        return authMenuService.getRouters(userId);
    }

    @Override
    public List<AuthMenu> menuTree() {
        return authMenuService.menuTree();
    }

    @Override
    public List<AuthMenu> menuList(AuthMenuQueryParam query) {
        return authMenuService.menuList(query);
    }

    @Override
    public AuthMenu createMenu(AuthMenu menu) {
        return authMenuService.createMenu(menu);
    }

    @Override
    public void updateMenu(AuthMenu menu) {
        authMenuService.updateMenu(menu);
    }

    @Override
    public void deleteMenu(Long menuId) {
        authMenuService.deleteMenu(menuId);
    }
}
