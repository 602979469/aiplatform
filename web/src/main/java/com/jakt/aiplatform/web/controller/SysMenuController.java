package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.SysMenuManager;
import com.jakt.aiplatform.web.assembler.SysMenuAssembler;
import com.jakt.aiplatform.web.checker.SysMenuParamChecker;
import com.jakt.aiplatform.web.param.SysMenuCreateRequest;
import com.jakt.aiplatform.web.param.SysMenuQueryRequest;
import com.jakt.aiplatform.web.param.SysMenuUpdateRequest;
import com.jakt.aiplatform.web.result.SysMenuResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysMenu;
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
 * 菜单管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/sysMenus")
@Tag(name = "菜单管理")
public class SysMenuController {

    /** 菜单 Manager。 */
    private final SysMenuManager sysMenuManager;

    public SysMenuController(SysMenuManager sysMenuManager) {
        this.sysMenuManager = sysMenuManager;
    }

    /**
     * 创建菜单。
     *
     * @param request 创建菜单请求体
     * @return 创建成功后的菜单信息
     */
    @PostMapping
    public AiPlatformResult<SysMenuResponse> create(@RequestBody SysMenuCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysMenuCreateRequest param) {
                SysMenuParamChecker.checkSysMenuCreateRequest(param);
            }

            @Override
            public SysMenuResponse execute(SysMenuCreateRequest param) {
                SysMenu sysMenu = sysMenuManager.createSysMenu(SysMenuAssembler.toModel(param));
                return SysMenuAssembler.toResponse(sysMenu);
            }

            @Override
            public void afterService(SysMenuCreateRequest param, SysMenuResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询菜单。
     *
     * @param id 菜单 ID
     * @return 菜单信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<SysMenuResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                SysMenuParamChecker.checkId(param);
            }

            @Override
            public SysMenuResponse execute(Long param) {
                SysMenu sysMenu = sysMenuManager.getSysMenu(param);
                AiPlatformInvoker.throwErrWhenNull(sysMenu, ErrorCodeEnum.RESOURCE_NOT_FOUND, "菜单不存在");
                return SysMenuAssembler.toResponse(sysMenu);
            }

            @Override
            public void afterService(Long param, SysMenuResponse result) {
            }
        });
    }

    /**
     * 分页查询菜单。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<SysMenuResponse>> page(SysMenuQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysMenuQueryRequest param) {
                SysMenuParamChecker.checkSysMenuQueryRequest(param);
            }

            @Override
            public PageResult<SysMenuResponse> execute(SysMenuQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new SysMenuQueryRequest());
                PageResult<SysMenu> page = sysMenuManager.pageSysMenus(SysMenuAssembler.toQueryParam(param));
                return new PageResult<>(page.getTotal(), param.getPageNum(), param.getPageSize(),
                        page.getDataList().stream().map(SysMenuAssembler::toResponse).toList());
            }
        });
    }

    /**
     * 更新菜单（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      菜单 ID
     * @param request 更新内容
     * @return 更新后的菜单信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody SysMenuUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(SysMenuUpdateRequest param) {
                SysMenuParamChecker.checkId(id);
                SysMenuParamChecker.checkSysMenuUpdateRequest(param);
            }

            @Override
            public void execute(SysMenuUpdateRequest param) {
                sysMenuManager.updateSysMenu(SysMenuAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除菜单。
     *
     * @param id 菜单 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                SysMenuParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                sysMenuManager.deleteSysMenu(id);
            }
        });
    }
}
