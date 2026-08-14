package com.jakt.aiplatform.web.controller;

import com.jakt.aiplatform.biz.service.AuthManager;
import com.jakt.aiplatform.core.model.dto.AuthLoginInfo;
import com.jakt.aiplatform.core.model.dto.AuthUserInfo;
import com.jakt.aiplatform.web.assembler.AuthAssembler;
import com.jakt.aiplatform.web.checker.AuthParamChecker;
import com.jakt.aiplatform.web.param.AuthLoginRequest;
import com.jakt.aiplatform.web.param.AuthRegisterRequest;
import com.jakt.aiplatform.web.result.AuthLoginResponse;
import com.jakt.aiplatform.web.result.AuthUserInfoResponse;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.template.ApiTemplate;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 认证接口：登录、注册、登出、当前用户信息。
 */
@RestController
@RequestMapping("/auth")
@Tag(name = "认证")
public class AuthController {

    private final AuthManager authManager;

    public AuthController(AuthManager authManager) {
        this.authManager = authManager;
    }

    /**
     * 登录。
     *
     * @param request 登录请求
     * @return token 凭证
     */
    @PostMapping("/login")
    public ApiResult<AuthLoginResponse> login(@RequestBody AuthLoginRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<AuthLoginRequest, AuthLoginResponse>() {

            @Override
            public void beforeService(AuthLoginRequest param) {
                AuthParamChecker.checkLogin(param);
            }

            @Override
            public AuthLoginResponse execute(AuthLoginRequest param) {
                AuthLoginInfo loginInfo = authManager.login(param.getUsername(), param.getPassword());
                return AuthAssembler.toLoginResponse(loginInfo);
            }
        });
    }

    /**
     * 注册（成功后自动登录）。
     *
     * @param request 注册请求
     * @return token 凭证
     */
    @PostMapping("/register")
    public ApiResult<AuthLoginResponse> register(@RequestBody AuthRegisterRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<AuthRegisterRequest, AuthLoginResponse>() {

            @Override
            public void beforeService(AuthRegisterRequest param) {
                AuthParamChecker.checkRegister(param);
            }

            @Override
            public AuthLoginResponse execute(AuthRegisterRequest param) {
                AuthLoginInfo loginInfo = authManager.register(
                        param.getUsername(), param.getPassword(), param.getNickname(), param.getEmail());
                return AuthAssembler.toLoginResponse(loginInfo);
            }
        });
    }

    /**
     * 登出。
     *
     * @return 统一返回体
     */
    @PostMapping("/logout")
    public ApiResult<Void> logout() {
        return ApiTemplate.executeWithoutResult(null,
                new ApiTemplate.CallbackWithoutResult<Object>() {

                    @Override
                    public void execute(Object param) {
                        authManager.logout();
                    }
                });
    }

    /**
     * 当前用户信息（含角色与权限码）。
     *
     * @return 用户信息
     */
    @GetMapping("/info")
    public ApiResult<AuthUserInfoResponse> info() {
        return ApiTemplate.execute(null, new ApiTemplate.Callback<Object, AuthUserInfoResponse>() {

            @Override
            public AuthUserInfoResponse execute(Object param) {
                AuthUserInfo info = authManager.getInfo();
                return AuthAssembler.toUserInfoResponse(info);
            }
        });
    }
}
