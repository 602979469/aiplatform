package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.AuthMenu;
import com.jakt.aiplatform.core.model.param.AuthMenuQueryParam;

import java.util.List;

/**
 * 菜单领域服务：菜单 CRUD、全量树与用户路由树（菜单领域模型独立，一个服务承载）。
 */
public interface AuthMenuService {

    /**
     * 获取用户可见菜单树（M目录/C菜单）。
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    List<AuthMenu> getRouters(Long userId);

    /**
     * 查询菜单详情。
     *
     * @param menuId 菜单ID
     * @return 菜单
     */
    AuthMenu getMenu(Long menuId);

    /**
     * 全量菜单树。
     *
     * @return 菜单树
     */
    List<AuthMenu> menuTree();

    /**
     * 菜单列表（扁平）。
     *
     * @param query 查询参数
     * @return 菜单列表
     */
    List<AuthMenu> menuList(AuthMenuQueryParam query);

    /**
     * 新增菜单。
     *
     * @param menu 菜单
     * @return 新增后的菜单
     */
    AuthMenu createMenu(AuthMenu menu);

    /**
     * 修改菜单。
     *
     * @param menu 菜单（含 menuId）
     */
    void updateMenu(AuthMenu menu);

    /**
     * 删除菜单（有子菜单禁止删除，清理角色菜单关联）。
     *
     * @param menuId 菜单ID
     */
    void deleteMenu(Long menuId);
}
