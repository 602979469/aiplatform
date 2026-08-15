package com.jakt.aiplatform.web.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.biz.service.AuthRoleManager;
import com.jakt.aiplatform.core.model.domain.AuthRole;
import com.jakt.aiplatform.core.model.param.AuthRoleQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;
import com.jakt.aiplatform.web.assembler.AuthRoleAssembler;
import com.jakt.aiplatform.web.checker.AuthRoleParamChecker;
import com.jakt.aiplatform.web.param.AuthRoleQueryRequest;
import com.jakt.aiplatform.web.param.AuthRoleCreateRequest;
import com.jakt.aiplatform.web.param.AuthRoleMenuRequest;
import com.jakt.aiplatform.web.param.AuthRoleStatusRequest;
import com.jakt.aiplatform.web.param.AuthRoleUpdateRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.AuthRoleResponse;
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
 * 角色管理接口。
 */
@RestController
@RequestMapping("/auth/role")
@Tag(name = "角色管理")
public class AuthRoleController {

    private final AuthRoleManager authRoleManager;

    public AuthRoleController(AuthRoleManager authRoleManager) {
        this.authRoleManager = authRoleManager;
    }

    /**
     * 分页查询角色。
     *
     * @return 角色分页
     */
    @GetMapping("/page")
    @SaCheckPermission("auth:role:list")
    public ApiResult<PageResult<AuthRoleResponse>> pageRole(AuthRoleQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<AuthRoleQueryRequest, PageResult<AuthRoleResponse>>() {

            @Override
            public void beforeService(AuthRoleQueryRequest param) {
                AuthRoleParamChecker.checkRoleQuery(param);
            }

            @Override
            public PageResult<AuthRoleResponse> execute(AuthRoleQueryRequest param) {
                AuthRoleQueryParam query = AuthRoleAssembler.toQueryParam(param);
                PageResult<AuthRole> page = authRoleManager.pageRole(query);
                return ConvertUtil.mapPage(page, AuthRoleAssembler::toRoleResponse);
            }
        });
    }

    /**
     * 查询角色详情。
     *
     * @param roleId 角色ID
     * @return 角色详情
     */
    @GetMapping("/{roleId}")
    @SaCheckPermission("auth:role:query")
    public ApiResult<AuthRoleResponse> getRole(@PathVariable Long roleId) {
        return ApiTemplate.execute(roleId, new ApiTemplate.Callback<Long, AuthRoleResponse>() {

            @Override
            public void beforeService(Long param) {
                AuthRoleParamChecker.checkRoleId(param);
            }

            @Override
            public AuthRoleResponse execute(Long param) {
                AuthRole role = authRoleManager.getRole(param);
                return AuthRoleAssembler.toRoleResponse(role);
            }
        });
    }

    /**
     * 新增角色。
     *
     * @param request 新增角色请求
     * @return 新增后的角色
     */
    @PostMapping
    @SaCheckPermission("auth:role:add")
    public ApiResult<AuthRoleResponse> createRole(@RequestBody AuthRoleCreateRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<AuthRoleCreateRequest, AuthRoleResponse>() {

            @Override
            public void beforeService(AuthRoleCreateRequest param) {
                AuthRoleParamChecker.checkRoleCreate(param);
            }

            @Override
            public AuthRoleResponse execute(AuthRoleCreateRequest param) {
                AuthRole role = AuthRoleAssembler.toRole(param);
                AuthRole created = authRoleManager.createRole(role);
                return AuthRoleAssembler.toRoleResponse(created);
            }
        });
    }

    /**
     * 修改角色。
     *
     * @param request 修改角色请求
     * @return 统一返回体
     */
    @PutMapping
    @SaCheckPermission("auth:role:edit")
    public ApiResult<Void> updateRole(@RequestBody AuthRoleUpdateRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AuthRoleUpdateRequest>() {

                    @Override
                    public void beforeService(AuthRoleUpdateRequest param) {
                        AuthRoleParamChecker.checkRoleUpdate(param);
                    }

                    @Override
                    public void execute(AuthRoleUpdateRequest param) {
                        AuthRole role = AuthRoleAssembler.toRole(param);
                        authRoleManager.updateRole(role);
                    }
                });
    }

    /**
     * 修改角色状态。
     *
     * @param roleId  角色ID
     * @param request 启停请求
     * @return 统一返回体
     */
    @PutMapping("/{roleId}/status")
    @SaCheckPermission("auth:role:edit")
    public ApiResult<Void> changeRoleStatus(@PathVariable Long roleId, @RequestBody AuthRoleStatusRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AuthRoleStatusRequest>() {

                    @Override
                    public void beforeService(AuthRoleStatusRequest param) {
                        AuthRoleParamChecker.checkRoleStatus(param);
                    }

                    @Override
                    public void execute(AuthRoleStatusRequest param) {
                        authRoleManager.changeRoleStatus(roleId, param.getStatus());
                    }
                });
    }

    /**
     * 分配角色菜单。
     *
     * @param roleId  角色ID
     * @param request 菜单分配请求
     * @return 统一返回体
     */
    @PutMapping("/{roleId}/menu")
    @SaCheckPermission("auth:role:menu")
    public ApiResult<Void> assignRoleMenus(@PathVariable Long roleId, @RequestBody AuthRoleMenuRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AuthRoleMenuRequest>() {

                    @Override
                    public void beforeService(AuthRoleMenuRequest param) {
                        AuthRoleParamChecker.checkRoleMenu(param);
                    }

                    @Override
                    public void execute(AuthRoleMenuRequest param) {
                        authRoleManager.assignRoleMenus(roleId, param.getMenuIds());
                    }
                });
    }

    /**
     * 查询角色已分配菜单ID。
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    @GetMapping("/{roleId}/menu-ids")
    @SaCheckPermission("auth:role:query")
    public ApiResult<List<Long>> getRoleMenuIds(@PathVariable Long roleId) {
        return ApiTemplate.execute(roleId, new ApiTemplate.Callback<Long, List<Long>>() {

            @Override
            public void beforeService(Long param) {
                AuthRoleParamChecker.checkRoleId(param);
            }

            @Override
            public List<Long> execute(Long param) {
                return authRoleManager.getRoleMenuIds(param);
            }
        });
    }

    /**
     * 删除角色。
     *
     * @param roleId 角色ID
     * @return 统一返回体
     */
    @DeleteMapping("/{roleId}")
    @SaCheckPermission("auth:role:remove")
    public ApiResult<Void> deleteRole(@PathVariable Long roleId) {
        return ApiTemplate.executeWithoutResult(roleId,
                new ApiTemplate.CallbackWithoutResult<Long>() {

                    @Override
                    public void beforeService(Long param) {
                        AuthRoleParamChecker.checkRoleId(param);
                    }

                    @Override
                    public void execute(Long param) {
                        authRoleManager.deleteRole(param);
                    }
                });
    }
}
