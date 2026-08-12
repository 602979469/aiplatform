package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.SysUserManager;
import com.jakt.aiplatform.web.assembler.SysUserAssembler;
import com.jakt.aiplatform.web.checker.SysUserParamChecker;
import com.jakt.aiplatform.web.param.SysUserCreateRequest;
import com.jakt.aiplatform.web.param.SysUserQueryRequest;
import com.jakt.aiplatform.web.param.SysUserUpdateRequest;
import com.jakt.aiplatform.web.result.SysUserResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysUser;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.result.PageResult;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 用户管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/sysUsers")
@Tag(name = "用户管理")
public class SysUserController {

    /** 用户 Manager。 */
    private final SysUserManager sysUserManager;

    public SysUserController(SysUserManager sysUserManager) {
        this.sysUserManager = sysUserManager;
    }

    /**
     * 创建用户。
     *
     * @param request 创建用户请求体
     * @return 创建成功后的用户信息
     */
    @PostMapping
    public AiPlatformResult<SysUserResponse> create(@RequestBody SysUserCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysUserCreateRequest param) {
                SysUserParamChecker.checkSysUserCreateRequest(param);
            }

            @Override
            public SysUserResponse execute(SysUserCreateRequest param) {
                SysUser sysUser = sysUserManager.createSysUser(SysUserAssembler.toModel(param));
                return SysUserAssembler.toResponse(sysUser);
            }

            @Override
            public void afterService(SysUserCreateRequest param, SysUserResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询用户。
     *
     * @param id 用户 ID
     * @return 用户信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<SysUserResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                SysUserParamChecker.checkId(param);
            }

            @Override
            public SysUserResponse execute(Long param) {
                SysUser sysUser = sysUserManager.getSysUser(param);
                AiPlatformInvoker.throwErrWhenNull(sysUser, ErrorCodeEnum.RESOURCE_NOT_FOUND, "用户不存在");
                return SysUserAssembler.toResponse(sysUser);
            }

            @Override
            public void afterService(Long param, SysUserResponse result) {
            }
        });
    }

    /**
     * 分页查询用户。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<SysUserResponse>> page(SysUserQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysUserQueryRequest param) {
                SysUserParamChecker.checkSysUserQueryRequest(param);
            }

            @Override
            public PageResult<SysUserResponse> execute(SysUserQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new SysUserQueryRequest());
                PageResult<SysUser> page = sysUserManager.pageSysUsers(SysUserAssembler.toQueryParam(param));
                return new PageResult<>(page.getTotal(), param.getPageNum(), param.getPageSize(),
                        page.getDataList().stream().map(SysUserAssembler::toResponse).toList());
            }
        });
    }

    /**
     * 更新用户（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      用户 ID
     * @param request 更新内容
     * @return 更新后的用户信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody SysUserUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(SysUserUpdateRequest param) {
                SysUserParamChecker.checkId(id);
                SysUserParamChecker.checkSysUserUpdateRequest(param);
            }

            @Override
            public void execute(SysUserUpdateRequest param) {
                sysUserManager.updateSysUser(SysUserAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除用户。
     *
     * @param id 用户 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                SysUserParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                sysUserManager.deleteSysUser(id);
            }
        });
    }
}
