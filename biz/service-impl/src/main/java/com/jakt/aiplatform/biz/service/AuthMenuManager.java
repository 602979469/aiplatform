package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.AuthMenu;
import com.jakt.aiplatform.core.model.param.AuthMenuQueryParam;

import java.util.List;

/**
 * 菜单管理用例编排：菜单 CRUD、全量树、当前用户路由树。
 */
public interface AuthMenuManager {

    /**
     * 当前用户菜单路由树。
     *
     * @return 路由树
     */
    List<AuthMenu> getRouters();

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
     * 删除菜单。
     *
     * @param menuId 菜单ID
     */
    void deleteMenu(Long menuId);
}
