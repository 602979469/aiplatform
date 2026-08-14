package com.jakt.aiplatform.web.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.biz.service.AuthMenuManager;
import com.jakt.aiplatform.core.model.domain.AuthMenu;
import com.jakt.aiplatform.core.model.param.AuthMenuQueryParam;
import com.jakt.aiplatform.web.assembler.AuthMenuAssembler;
import com.jakt.aiplatform.web.checker.AuthMenuParamChecker;
import com.jakt.aiplatform.web.param.AuthMenuQueryRequest;
import com.jakt.aiplatform.web.param.AuthMenuRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.AuthMenuResponse;
import com.jakt.aiplatform.web.result.MenuRouteResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * 菜单管理接口：菜单 CRUD、全量树、当前用户路由树。
 */
@RestController
@RequestMapping("/auth/menu")
@Tag(name = "菜单管理")
public class AuthMenuController {

    private final AuthMenuManager authMenuManager;

    public AuthMenuController(AuthMenuManager authMenuManager) {
        this.authMenuManager = authMenuManager;
    }

    /**
     * 当前用户菜单路由树。
     *
     * @return 路由树
     */
    @GetMapping("/routers")
    public ApiResult<List<MenuRouteResponse>> routers() {
        return ApiTemplate.execute(null, new ApiTemplate.Callback<Object, List<MenuRouteResponse>>() {

            @Override
            public List<MenuRouteResponse> execute(Object param) {
                List<AuthMenu> menus = authMenuManager.getRouters();
                return ConvertUtil.map(menus, AuthMenuAssembler::toRouteResponse);
            }
        });
    }

    /**
     * 全量菜单树。
     *
     * @return 菜单树
     */
    @GetMapping("/tree")
    @SaCheckPermission("auth:menu:list")
    public ApiResult<List<AuthMenuResponse>> menuTree() {
        return ApiTemplate.execute(null, new ApiTemplate.Callback<Object, List<AuthMenuResponse>>() {

            @Override
            public List<AuthMenuResponse> execute(Object param) {
                List<AuthMenu> menus = authMenuManager.menuTree();
                return ConvertUtil.map(menus, AuthMenuAssembler::toMenuResponse);
            }
        });
    }

    /**
     * 菜单列表（扁平）。
     *
     * @return 菜单列表
     */
    @GetMapping("/list")
    @SaCheckPermission("auth:menu:list")
    public ApiResult<List<AuthMenuResponse>> menuList(AuthMenuQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<AuthMenuQueryRequest, List<AuthMenuResponse>>() {

            @Override
            public void beforeService(AuthMenuQueryRequest param) {
                AuthMenuParamChecker.checkMenuQuery(param);
            }

            @Override
            public List<AuthMenuResponse> execute(AuthMenuQueryRequest param) {
                AuthMenuQueryParam query = AuthMenuAssembler.toQueryParam(param);
                List<AuthMenu> menus = authMenuManager.menuList(query);
                return ConvertUtil.map(menus, AuthMenuAssembler::toMenuResponse);
            }
        });
    }

    /**
     * 新增菜单。
     *
     * @param request 菜单请求
     * @return 新增后的菜单
     */
    @PostMapping
    @SaCheckPermission("auth:menu:add")
    public ApiResult<AuthMenuResponse> createMenu(@RequestBody AuthMenuRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<AuthMenuRequest, AuthMenuResponse>() {

            @Override
            public void beforeService(AuthMenuRequest param) {
                AuthMenuParamChecker.checkMenu(param);
            }

            @Override
            public AuthMenuResponse execute(AuthMenuRequest param) {
                AuthMenu menu = AuthMenuAssembler.toMenu(param);
                AuthMenu created = authMenuManager.createMenu(menu);
                return AuthMenuAssembler.toMenuResponse(created);
            }
        });
    }

    /**
     * 修改菜单。
     *
     * @param request 菜单请求
     * @return 统一返回体
     */
    @PutMapping
    @SaCheckPermission("auth:menu:edit")
    public ApiResult<Void> updateMenu(@RequestBody AuthMenuRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AuthMenuRequest>() {

                    @Override
                    public void beforeService(AuthMenuRequest param) {
                        AuthMenuParamChecker.checkMenu(param);
                    }

                    @Override
                    public void execute(AuthMenuRequest param) {
                        AuthMenu menu = AuthMenuAssembler.toMenuUpdate(param);
                        authMenuManager.updateMenu(menu);
                    }
                });
    }

    /**
     * 删除菜单。
     *
     * @param menuId 菜单ID
     * @return 统一返回体
     */
    @DeleteMapping("/{menuId}")
    @SaCheckPermission("auth:menu:remove")
    public ApiResult<Void> deleteMenu(@PathVariable Long menuId) {
        return ApiTemplate.executeWithoutResult(menuId,
                new ApiTemplate.CallbackWithoutResult<Long>() {

                    @Override
                    public void beforeService(Long param) {
                        AuthMenuParamChecker.checkMenuId(param);
                    }

                    @Override
                    public void execute(Long param) {
                        authMenuManager.deleteMenu(param);
                    }
                });
    }
}
