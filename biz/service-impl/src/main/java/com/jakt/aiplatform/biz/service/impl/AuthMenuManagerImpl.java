package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.AuthMenuManager;
import com.jakt.aiplatform.core.model.context.UserContext;
import com.jakt.aiplatform.core.model.domain.AuthMenu;
import com.jakt.aiplatform.core.model.param.AuthMenuQueryParam;
import com.jakt.aiplatform.core.service.AuthMenuAdminService;
import com.jakt.aiplatform.core.service.AuthMenuService;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜单管理用例编排实现：路由树走菜单领域服务，管理操作走菜单管理领域服务。
 */
@Service
public class AuthMenuManagerImpl implements AuthMenuManager {

    private final AuthMenuService authMenuService;

    private final AuthMenuAdminService authMenuAdminService;

    public AuthMenuManagerImpl(AuthMenuService authMenuService, AuthMenuAdminService authMenuAdminService) {
        this.authMenuService = authMenuService;
        this.authMenuAdminService = authMenuAdminService;
    }

    @Override
    public AuthMenu getMenu(Long menuId) {
        return authMenuAdminService.getMenu(menuId);
    }

    @Override
    public List<AuthMenu> getRouters() {
        Long userId = UserContext.getUserId();
        return authMenuService.getRouters(userId);
    }

    @Override
    public List<AuthMenu> menuTree() {
        return authMenuAdminService.menuTree();
    }

    @Override
    public List<AuthMenu> menuList(AuthMenuQueryParam query) {
        return authMenuAdminService.menuList(query);
    }

    @Override
    public AuthMenu createMenu(AuthMenu menu) {
        return authMenuAdminService.createMenu(menu);
    }

    @Override
    public void updateMenu(AuthMenu menu) {
        authMenuAdminService.updateMenu(menu);
    }

    @Override
    public void deleteMenu(Long menuId) {
        authMenuAdminService.deleteMenu(menuId);
    }
}
