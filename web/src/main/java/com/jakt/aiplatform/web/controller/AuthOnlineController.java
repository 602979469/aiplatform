package com.jakt.aiplatform.web.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.biz.service.AuthOnlineManager;
import com.jakt.aiplatform.core.model.constant.AiPlatformConstant;
import com.jakt.aiplatform.core.model.dto.AuthOnlineInfo;
import com.jakt.aiplatform.core.model.param.AuthOnlineQueryParam;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.web.assembler.AuthOnlineAssembler;
import com.jakt.aiplatform.web.checker.AuthOnlineParamChecker;
import com.jakt.aiplatform.web.param.AuthOnlineQueryRequest;
import com.jakt.aiplatform.web.param.AuthOnlineDisableRequest;
import com.jakt.aiplatform.web.param.AuthOnlineKickoutRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.AuthOnlineResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 在线会话管理接口：在线列表、踢人、强制注销、封禁。
 */
@RestController
@RequestMapping("/auth/online")
@Tag(name = "在线会话管理")
public class AuthOnlineController {

    private final AuthOnlineManager authOnlineManager;

    public AuthOnlineController(AuthOnlineManager authOnlineManager) {
        this.authOnlineManager = authOnlineManager;
    }

    /**
     * 在线用户分页。
     *
     * @return 在线用户分页
     */
    @GetMapping("/list")
    @SaCheckPermission("auth:online:list")
    public ApiResult<PageResult<AuthOnlineResponse>> listOnline(AuthOnlineQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<AuthOnlineQueryRequest, PageResult<AuthOnlineResponse>>() {

            @Override
            public void beforeService(AuthOnlineQueryRequest param) {
                AuthOnlineParamChecker.checkOnlineQuery(param);
            }

            @Override
            public PageResult<AuthOnlineResponse> execute(AuthOnlineQueryRequest param) {
                AuthOnlineQueryParam query = AuthOnlineAssembler.toQueryParam(param);
                PageResult<AuthOnlineInfo> page = authOnlineManager.listOnline(query);
                return ConvertUtil.mapPage(page, AuthOnlineAssembler::toOnlineResponse);
            }
        });
    }

    /**
     * 踢人下线。
     *
     * @param request 踢人请求
     * @return 统一返回体
     */
    @PostMapping("/kickout")
    @SaCheckPermission("auth:online:kickout")
    public ApiResult<Void> kickout(@RequestBody AuthOnlineKickoutRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AuthOnlineKickoutRequest>() {

                    @Override
                    public void beforeService(AuthOnlineKickoutRequest param) {
                        AuthOnlineParamChecker.checkOnlineKickout(param);
                    }

                    @Override
                    public void execute(AuthOnlineKickoutRequest param) {
                        authOnlineManager.kickout(param.getUserId());
                    }
                });
    }

    /**
     * 强制注销。
     *
     * @param request 注销请求
     * @return 统一返回体
     */
    @PostMapping("/logout")
    @SaCheckPermission("auth:online:logout")
    public ApiResult<Void> forceLogout(@RequestBody AuthOnlineKickoutRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AuthOnlineKickoutRequest>() {

                    @Override
                    public void beforeService(AuthOnlineKickoutRequest param) {
                        AuthOnlineParamChecker.checkOnlineKickout(param);
                    }

                    @Override
                    public void execute(AuthOnlineKickoutRequest param) {
                        authOnlineManager.forceLogout(param.getUserId());
                    }
                });
    }

    /**
     * 封禁账号并立即掉线。
     *
     * @param request 封禁请求
     * @return 统一返回体
     */
    @PostMapping("/disable")
    @SaCheckPermission("auth:online:disable")
    public ApiResult<Void> disable(@RequestBody AuthOnlineDisableRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AuthOnlineDisableRequest>() {

                    @Override
                    public void beforeService(AuthOnlineDisableRequest param) {
                        AuthOnlineParamChecker.checkOnlineDisable(param);
                    }

                    @Override
                    public void execute(AuthOnlineDisableRequest param) {
                        long seconds = param.getSeconds() == null ? AiPlatformConstant.DEFAULT_DISABLE_SECONDS : param.getSeconds();
                        authOnlineManager.disable(param.getUserId(), seconds);
                    }
                });
    }

    /**
     * 解封账号。
     *
     * @param request 解封请求
     * @return 统一返回体
     */
    @PostMapping("/untieDisable")
    @SaCheckPermission("auth:online:disable")
    public ApiResult<Void> untieDisable(@RequestBody AuthOnlineDisableRequest request) {
        return ApiTemplate.executeWithoutResult(request,
                new ApiTemplate.CallbackWithoutResult<AuthOnlineDisableRequest>() {

                    @Override
                    public void beforeService(AuthOnlineDisableRequest param) {
                        AuthOnlineParamChecker.checkOnlineDisable(param);
                    }

                    @Override
                    public void execute(AuthOnlineDisableRequest param) {
                        authOnlineManager.untieDisable(param.getUserId());
                    }
                });
    }
}
