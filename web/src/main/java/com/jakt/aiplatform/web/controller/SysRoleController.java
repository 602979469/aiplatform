package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.SysRoleManager;
import com.jakt.aiplatform.web.assembler.SysRoleAssembler;
import com.jakt.aiplatform.web.checker.SysRoleParamChecker;
import com.jakt.aiplatform.web.param.SysRoleCreateRequest;
import com.jakt.aiplatform.web.param.SysRoleQueryRequest;
import com.jakt.aiplatform.web.param.SysRoleUpdateRequest;
import com.jakt.aiplatform.web.result.SysRoleResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysRole;
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
 * 角色管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/sysRoles")
@Tag(name = "角色管理")
public class SysRoleController {

    /** 角色 Manager。 */
    private final SysRoleManager sysRoleManager;

    public SysRoleController(SysRoleManager sysRoleManager) {
        this.sysRoleManager = sysRoleManager;
    }

    /**
     * 创建角色。
     *
     * @param request 创建角色请求体
     * @return 创建成功后的角色信息
     */
    @PostMapping
    public AiPlatformResult<SysRoleResponse> create(@RequestBody SysRoleCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysRoleCreateRequest param) {
                SysRoleParamChecker.checkSysRoleCreateRequest(param);
            }

            @Override
            public SysRoleResponse execute(SysRoleCreateRequest param) {
                // TODO 接入 SysRoleManager（RuoYi 移植过渡）
                return null;
            }

            @Override
            public void afterService(SysRoleCreateRequest param, SysRoleResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询角色。
     *
     * @param id 角色 ID
     * @return 角色信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<SysRoleResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                SysRoleParamChecker.checkId(param);
            }

            @Override
            public SysRoleResponse execute(Long param) {
                // TODO 接入 SysRoleManager（RuoYi 移植过渡）
                return null;
            }

            @Override
            public void afterService(Long param, SysRoleResponse result) {
            }
        });
    }

    /**
     * 分页查询角色。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<SysRoleResponse>> page(SysRoleQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysRoleQueryRequest param) {
                SysRoleParamChecker.checkSysRoleQueryRequest(param);
            }

            @Override
            public PageResult<SysRoleResponse> execute(SysRoleQueryRequest param) {
                // TODO 接入 SysRoleManager（RuoYi 移植过渡）
                return null;
            }
        });
    }

    /**
     * 更新角色（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      角色 ID
     * @param request 更新内容
     * @return 更新后的角色信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody SysRoleUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(SysRoleUpdateRequest param) {
                SysRoleParamChecker.checkId(id);
                SysRoleParamChecker.checkSysRoleUpdateRequest(param);
            }

            @Override
            public void execute(SysRoleUpdateRequest param) {
                // TODO 接入 SysRoleManager（RuoYi 移植过渡）
            }
        });
    }

    /**
     * 删除角色。
     *
     * @param id 角色 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                SysRoleParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                // TODO 接入 SysRoleManager（RuoYi 移植过渡）
            }
        });
    }
}
