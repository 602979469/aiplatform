package com.jakt.aiplatform.web.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.biz.service.AuthUserManager;
import com.jakt.aiplatform.core.model.domain.AuthUser;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.param.AuthUserQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;
import com.jakt.aiplatform.web.assembler.AuthUserAssembler;
import com.jakt.aiplatform.web.checker.AuthUserParamChecker;
import com.jakt.aiplatform.web.param.AuthUserQueryRequest;
import com.jakt.aiplatform.web.param.AuthPasswordRequest;
import com.jakt.aiplatform.web.param.AuthProfileRequest;
import com.jakt.aiplatform.web.param.AuthResetPasswordRequest;
import com.jakt.aiplatform.web.param.AuthUserCreateRequest;
import com.jakt.aiplatform.web.param.AuthUserRoleRequest;
import com.jakt.aiplatform.web.param.AuthUserStatusRequest;
import com.jakt.aiplatform.web.param.AuthUserUpdateRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.AuthUserResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import com.jakt.aiplatform.web.util.MultipartFileUtil;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

/**
 * 用户管理接口。
 */
@RestController
@RequestMapping("/auth/user")
@Tag(name = "用户管理")
public class AuthUserController {

    private final AuthUserManager authUserManager;

    public AuthUserController(AuthUserManager authUserManager) {
        this.authUserManager = authUserManager;
    }

    /**
     * 分页查询用户。
     *
     * @return 用户分页
     */
    @GetMapping("/page")
    @SaCheckPermission("auth:user:list")
    public ApiResult<PageResult<AuthUserResponse>> pageUser(AuthUserQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<AuthUserQueryRequest, PageResult<AuthUserResponse>>() {

            @Override
            public void beforeService(AuthUserQueryRequest param) {
                AuthUserParamChecker.checkUserQuery(param);
            }

            @Override
            public PageResult<AuthUserResponse> execute(AuthUserQueryRequest param) {
                AuthUserQueryParam query = AuthUserAssembler.toQueryParam(param);
                PageResult<AuthUser> page = authUserManager.pageUser(query);
                return ConvertUtil.mapPage(page, AuthUserAssembler::toUserResponse);
            }
        });
    }

    /**
     * 查询用户详情。
     *
     * @param userId 用户ID
     * @return 用户详情
     */
    @GetMapping("/{userId}")
    @SaCheckPermission("auth:user:query")
    public ApiResult<AuthUserResponse> getUser(@PathVariable Long userId) {
        return ApiTemplate.execute(userId, new ApiTemplate.Callback<Long, AuthUserResponse>() {

            @Override
            public void beforeService(Long param) {
                AuthUserParamChecker.checkUserId(param);
            }

            @Override
            public AuthUserResponse execute(Long param) {
                AuthUser user = authUserManager.getUser(param);
                return AuthUserAssembler.toUserResponse(user);
            }
        });
    }

    /**
     * 查询用户已分配角色ID（编辑回显）。
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    @GetMapping("/{userId}/role")
    @SaCheckPermission("auth:user:query")
    public ApiResult<List<Long>> getUserRoleIds(@PathVariable Long userId) {
        return ApiTemplate.execute(userId, new ApiTemplate.Callback<Long, List<Long>>() {

            @Override
            public void beforeService(Long param) {
                AuthUserParamChecker.checkUserId(param);
            }

            @Override
            public List<Long> execute(Long param) {
                return authUserManager.getUserRoleIds(param);
            }
        });
    }

    /**
     * 新增用户。
     *
     * @param request 新增用户请求
     * @return 新增后的用户
     */
    @PostMapping
    @SaCheckPermission("auth:user:add")
    public ApiResult<AuthUserResponse> createUser(@RequestBody AuthUserCreateRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<AuthUserCreateRequest, AuthUserResponse>() {

            @Override
            public void beforeService(AuthUserCreateRequest param) {
                AuthUserParamChecker.checkUserCreate(param);
            }

            @Override
            public AuthUserResponse execute(AuthUserCreateRequest param) {
                AuthUser user = AuthUserAssembler.toUser(param);
                AuthUser created = authUserManager.createUser(user, param.getRoleIds());
                return AuthUserAssembler.toUserResponse(created);
            }
        });
    }

    /**
     * 修改用户。
     *
     * @param request 修改用户请求
     * @return 统一返回体
     */
    @PutMapping
    @SaCheckPermission("auth:user:edit")
    public ApiResult<Void> updateUser(@RequestBody AuthUserUpdateRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AuthUserUpdateRequest>() {

                    @Override
                    public void beforeService(AuthUserUpdateRequest param) {
                        AuthUserParamChecker.checkUserUpdate(param);
                    }

                    @Override
                    public void execute(AuthUserUpdateRequest param) {
                        AuthUser user = AuthUserAssembler.toUser(param);
                        authUserManager.updateUser(user);
                    }
                });
    }

    /**
     * 用户启停。
     *
     * @param userId  用户ID
     * @param request 启停请求
     * @return 统一返回体
     */
    @PutMapping("/{userId}/status")
    @SaCheckPermission("auth:user:edit")
    public ApiResult<Void> changeUserStatus(@PathVariable Long userId, @RequestBody AuthUserStatusRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AuthUserStatusRequest>() {

                    @Override
                    public void beforeService(AuthUserStatusRequest param) {
                        AuthUserParamChecker.checkUserStatus(param);
                    }

                    @Override
                    public void execute(AuthUserStatusRequest param) {
                        authUserManager.changeUserStatus(userId, param.getStatus());
                    }
                });
    }

    /**
     * 重置密码。
     *
     * @param userId  用户ID
     * @param request 重置密码请求
     * @return 统一返回体
     */
    @PutMapping("/{userId}/password")
    @SaCheckPermission("auth:user:resetPwd")
    public ApiResult<Void> resetPassword(@PathVariable Long userId, @RequestBody AuthResetPasswordRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AuthResetPasswordRequest>() {

                    @Override
                    public void beforeService(AuthResetPasswordRequest param) {
                        AuthUserParamChecker.checkResetPassword(param);
                    }

                    @Override
                    public void execute(AuthResetPasswordRequest param) {
                        authUserManager.resetPassword(userId, param.getPassword());
                    }
                });
    }

    /**
     * 分配用户角色。
     *
     * @param userId  用户ID
     * @param request 角色分配请求
     * @return 统一返回体
     */
    @PutMapping("/{userId}/role")
    @SaCheckPermission("auth:user:role")
    public ApiResult<Void> assignUserRoles(@PathVariable Long userId, @RequestBody AuthUserRoleRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AuthUserRoleRequest>() {

                    @Override
                    public void beforeService(AuthUserRoleRequest param) {
                        AuthUserParamChecker.checkUserRole(param);
                    }

                    @Override
                    public void execute(AuthUserRoleRequest param) {
                        authUserManager.assignUserRoles(userId, param.getRoleIds());
                    }
                });
    }

    /**
     * 删除用户。
     *
     * @param userId 用户ID
     * @return 统一返回体
     */
    @DeleteMapping("/{userId}")
    @SaCheckPermission("auth:user:remove")
    public ApiResult<Void> deleteUser(@PathVariable Long userId) {
        return ApiTemplate.executeWithoutResult(userId,
                new ApiTemplate.CallbackWithoutResult<Long>() {

                    @Override
                    public void beforeService(Long param) {
                        AuthUserParamChecker.checkUserId(param);
                    }

                    @Override
                    public void execute(Long param) {
                        authUserManager.deleteUser(param);
                    }
                });
    }

    // ============ 个人中心（当前登录用户） ============

    /**
     * 查询当前用户个人信息。
     *
     * @return 当前用户信息
     */
    @GetMapping("/profile")
    public ApiResult<AuthUserResponse> getProfile() {
        return ApiTemplate.execute(null, new ApiTemplate.Callback<Object, AuthUserResponse>() {

            @Override
            public AuthUserResponse execute(Object param) {
                return AuthUserAssembler.toUserResponse(
                        authUserManager.getUser(StpUtil.getLoginIdAsLong()));
            }
        });
    }

    /**
     * 修改当前用户个人信息。
     *
     * @param request 资料修改请求
     * @return 统一返回体
     */
    @PutMapping("/profile")
    public ApiResult<Void> updateProfile(@RequestBody AuthProfileRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AuthProfileRequest>() {

                    @Override
                    public void beforeService(AuthProfileRequest param) {
                        AuthUserParamChecker.checkProfile(param);
                    }

                    @Override
                    public void execute(AuthProfileRequest param) {
                        authUserManager.updateProfile(StpUtil.getLoginIdAsLong(),
                                param.getNickname(), param.getEmail());
                    }
                });
    }

    /**
     * 修改当前用户密码。
     *
     * @param request 修改密码请求
     * @return 统一返回体
     */
    @PutMapping("/profile/password")
    public ApiResult<Void> updatePassword(@RequestBody AuthPasswordRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AuthPasswordRequest>() {

                    @Override
                    public void beforeService(AuthPasswordRequest param) {
                        AuthUserParamChecker.checkPassword(param);
                    }

                    @Override
                    public void execute(AuthPasswordRequest param) {
                        authUserManager.updatePassword(StpUtil.getLoginIdAsLong(),
                                param.getOldPassword(), param.getNewPassword());
                    }
                });
    }

    /**
     * 修改当前用户头像。
     *
     * @param avatarfile 头像文件
     * @return 统一返回体
     */
    @PutMapping("/profile/avatar")
    public ApiResult<String> updateAvatar(@RequestParam("avatarfile") MultipartFile avatarfile) {
        return ApiTemplate.execute(avatarfile, new ApiTemplate.Callback<MultipartFile, String>() {

            @Override
            public void beforeService(MultipartFile param) {
                AuthUserParamChecker.checkAvatar(param);
            }

            @Override
            public String execute(MultipartFile param) {
                byte[] imageBytes = MultipartFileUtil.readBytes(param);
                return authUserManager.updateAvatar(StpUtil.getLoginIdAsLong(),
                        imageBytes, param.getOriginalFilename());
            }
        });
    }
}
