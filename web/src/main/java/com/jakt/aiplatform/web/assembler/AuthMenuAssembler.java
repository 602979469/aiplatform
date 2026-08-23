package com.jakt.aiplatform.web.assembler;

import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.AuthMenu;
import com.jakt.aiplatform.core.model.param.AuthMenuQueryParam;
import com.jakt.aiplatform.web.param.AuthMenuQueryRequest;
import com.jakt.aiplatform.web.param.AuthMenuRequest;
import com.jakt.aiplatform.web.result.AuthMenuResponse;
import com.jakt.aiplatform.web.result.MenuRouteResponse;

import java.util.List;

/**
 * 菜单域组装器：DTO↔Model、菜单响应与路由树转换。
 */
public final class AuthMenuAssembler {

    private AuthMenuAssembler() {
    }

    /**
     * 菜单 → 菜单响应（含子菜单树）。
     *
     * @param menu 菜单；为空返回 null
     * @return 菜单响应
     */
    public static AuthMenuResponse toMenuResponse(AuthMenu menu) {
        if (menu == null) {
            return null;
        }
        AuthMenuResponse response = new AuthMenuResponse();
        response.setMenuId(menu.getMenuId());
        response.setMenuName(menu.getMenuName());
        response.setParentId(menu.getParentId());
        response.setOrderNum(menu.getOrderNum());
        response.setPath(menu.getPath());
        response.setComponent(menu.getComponent());
        response.setIsFrame(menu.getIsFrame());
        response.setMenuType(menu.getMenuType());
        response.setVisible(menu.getVisible());
        response.setStatus(menu.getStatus());
        response.setPerms(menu.getPerms());
        response.setIcon(menu.getIcon());
        response.setChildren(ConvertUtil.map(menu.getChildren(), AuthMenuAssembler::toMenuResponse));
        response.setCreateTime(menu.getCreateTime());
        return response;
    }

    /**
     * 菜单 → 前端路由响应。
     *
     * @param menu 菜单；为空返回 null
     * @return 路由响应
     */
    public static MenuRouteResponse toRouteResponse(AuthMenu menu) {
        if (menu == null) {
            return null;
        }
        MenuRouteResponse response = new MenuRouteResponse();
        response.setMenuId(menu.getMenuId());
        response.setMenuName(menu.getMenuName());
        response.setPath(menu.getPath());
        response.setComponent(menu.getComponent());
        response.setIsFrame(menu.getIsFrame());
        response.setMenuType(menu.getMenuType());
        response.setIcon(menu.getIcon());
        response.setChildren(ConvertUtil.map(menu.getChildren(), AuthMenuAssembler::toRouteResponse));
        return response;
    }

    /**
     * 菜单请求 → 菜单领域模型（新增）。
     *
     * @param request 菜单请求
     * @return 菜单领域模型
     */
    public static AuthMenu toMenu(AuthMenuRequest request) {
        if (request == null) {
            return null;
        }
        return toMenu(request, new AuthMenu());
    }

    /**
     * 菜单请求 → 菜单领域模型（修改，含 menuId）。
     *
     * @param request 菜单请求
     * @return 菜单领域模型
     */
    public static AuthMenu toMenuUpdate(AuthMenuRequest request) {
        if (request == null) {
            return null;
        }
        AuthMenu menu = new AuthMenu();
        menu.setMenuId(request.getMenuId());
        return toMenu(request, menu);
    }

    /**
     * 菜单请求 + 已有菜单 → 合并后的菜单领域模型（多入参场景）。
     *
     * @param request 菜单请求
     * @param menu    已有菜单（新增时为空对象）
     * @return 合并后的菜单领域模型
     */
    public static AuthMenu toMenu(AuthMenuRequest request, AuthMenu menu) {
        if (request == null) {
            return menu;
        }
        menu.setMenuName(request.getMenuName());
        menu.setParentId(request.getParentId());
        menu.setOrderNum(request.getOrderNum());
        menu.setPath(request.getPath());
        menu.setComponent(request.getComponent());
        menu.setIsFrame(request.getIsFrame());
        menu.setMenuType(request.getMenuType());
        menu.setVisible(request.getVisible());
        menu.setStatus(request.getStatus());
        menu.setPerms(request.getPerms());
        menu.setIcon(request.getIcon());
        menu.setRemark(request.getRemark());
        return menu;
    }

    /**
     * 菜单查询请求 → 查询参数。
     *
     * @param request 查询请求；为空返回空查询参数
     * @return 查询参数
     */
    public static AuthMenuQueryParam toQueryParam(AuthMenuQueryRequest request) {
        if (request == null) {
            return new AuthMenuQueryParam();
        }
        AuthMenuQueryParam query = new AuthMenuQueryParam();
        query.setMenuName(request.getMenuName());
        query.setMenuType(request.getMenuType());
        query.setStatus(request.getStatus());
        return query;
    }
}
