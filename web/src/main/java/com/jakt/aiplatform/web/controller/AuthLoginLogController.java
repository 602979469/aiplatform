package com.jakt.aiplatform.web.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.biz.service.AuthLoginLogManager;
import com.jakt.aiplatform.core.model.domain.AuthLoginLog;
import com.jakt.aiplatform.core.model.param.AuthLoginLogQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;
import com.jakt.aiplatform.web.assembler.AuthLoginLogAssembler;
import com.jakt.aiplatform.web.checker.AuthLoginLogParamChecker;
import com.jakt.aiplatform.web.param.AuthLoginLogQueryRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.AuthLoginLogResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 登录记录接口。
 */
@RestController
@RequestMapping("/auth/login-log")
@Tag(name = "登录记录")
public class AuthLoginLogController {

    private final AuthLoginLogManager authLoginLogManager;

    public AuthLoginLogController(AuthLoginLogManager authLoginLogManager) {
        this.authLoginLogManager = authLoginLogManager;
    }

    /**
     * 分页查询登录记录。
     *
     * @return 登录记录分页
     */
    @GetMapping("/page")
    @SaCheckPermission("auth:loginlog:list")
    public ApiResult<PageResult<AuthLoginLogResponse>> pageLoginLog(AuthLoginLogQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<AuthLoginLogQueryRequest, PageResult<AuthLoginLogResponse>>() {

            @Override
            public void beforeService(AuthLoginLogQueryRequest param) {
                AuthLoginLogParamChecker.checkLoginLogQuery(param);
            }

            @Override
            public PageResult<AuthLoginLogResponse> execute(AuthLoginLogQueryRequest param) {
                AuthLoginLogQueryParam query = AuthLoginLogAssembler.toQueryParam(param);
                PageResult<AuthLoginLog> page = authLoginLogManager.pageLoginLog(query);
                return ConvertUtil.mapPage(page, AuthLoginLogAssembler::toLoginLogResponse);
            }
        });
    }

    /**
     * 删除登录记录。
     *
     * @param logId 日志ID
     * @return 统一返回体
     */
    @DeleteMapping("/{logId}")
    @SaCheckPermission("auth:loginlog:remove")
    public ApiResult<Void> deleteLoginLog(@PathVariable Long logId) {
        return ApiTemplate.executeWithoutResult(logId,
                new ApiTemplate.CallbackWithoutResult<Long>() {

                    @Override
                    public void beforeService(Long param) {
                        AuthLoginLogParamChecker.checkLogId(param);
                    }

                    @Override
                    public void execute(Long param) {
                        authLoginLogManager.deleteLoginLog(param);
                    }
                });
    }
}
