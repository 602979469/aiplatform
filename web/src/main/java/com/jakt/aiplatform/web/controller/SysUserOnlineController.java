package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.SysUserOnlineManager;
import com.jakt.aiplatform.web.assembler.SysUserOnlineAssembler;
import com.jakt.aiplatform.web.checker.SysUserOnlineParamChecker;
import com.jakt.aiplatform.web.param.SysUserOnlineCreateRequest;
import com.jakt.aiplatform.web.param.SysUserOnlineQueryRequest;
import com.jakt.aiplatform.web.param.SysUserOnlineUpdateRequest;
import com.jakt.aiplatform.web.result.SysUserOnlineResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysUserOnline;
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
 * 在线用户管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/sysUserOnlines")
@Tag(name = "在线用户管理")
public class SysUserOnlineController {

    /** 在线用户 Manager。 */
    private final SysUserOnlineManager sysUserOnlineManager;

    public SysUserOnlineController(SysUserOnlineManager sysUserOnlineManager) {
        this.sysUserOnlineManager = sysUserOnlineManager;
    }

    /**
     * 创建在线用户。
     *
     * @param request 创建在线用户请求体
     * @return 创建成功后的在线用户信息
     */
    @PostMapping
    public AiPlatformResult<SysUserOnlineResponse> create(@RequestBody SysUserOnlineCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysUserOnlineCreateRequest param) {
                SysUserOnlineParamChecker.checkSysUserOnlineCreateRequest(param);
            }

            @Override
            public SysUserOnlineResponse execute(SysUserOnlineCreateRequest param) {
                SysUserOnline sysUserOnline = sysUserOnlineManager.createSysUserOnline(SysUserOnlineAssembler.toModel(param));
                return SysUserOnlineAssembler.toResponse(sysUserOnline);
            }

            @Override
            public void afterService(SysUserOnlineCreateRequest param, SysUserOnlineResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询在线用户。
     *
     * @param id 在线用户 ID
     * @return 在线用户信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<SysUserOnlineResponse> get(@PathVariable String id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(String param) {
                SysUserOnlineParamChecker.checkId(param);
            }

            @Override
            public SysUserOnlineResponse execute(String param) {
                SysUserOnline sysUserOnline = sysUserOnlineManager.getSysUserOnline(param);
                AiPlatformInvoker.throwErrWhenNull(sysUserOnline, ErrorCodeEnum.RESOURCE_NOT_FOUND, "在线用户不存在");
                return SysUserOnlineAssembler.toResponse(sysUserOnline);
            }

            @Override
            public void afterService(String param, SysUserOnlineResponse result) {
            }
        });
    }

    /**
     * 分页查询在线用户。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<SysUserOnlineResponse>> page(SysUserOnlineQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysUserOnlineQueryRequest param) {
                SysUserOnlineParamChecker.checkSysUserOnlineQueryRequest(param);
            }

            @Override
            public PageResult<SysUserOnlineResponse> execute(SysUserOnlineQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new SysUserOnlineQueryRequest());
                PageResult<SysUserOnline> page = sysUserOnlineManager.pageSysUserOnlines(SysUserOnlineAssembler.toQueryParam(param));
                return new PageResult<>(page.getTotal(), param.getPageNum(), param.getPageSize(),
                        page.getDataList().stream().map(SysUserOnlineAssembler::toResponse).toList());
            }
        });
    }

    /**
     * 更新在线用户（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      在线用户 ID
     * @param request 更新内容
     * @return 更新后的在线用户信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable String id, @RequestBody SysUserOnlineUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(SysUserOnlineUpdateRequest param) {
                SysUserOnlineParamChecker.checkId(id);
                SysUserOnlineParamChecker.checkSysUserOnlineUpdateRequest(param);
            }

            @Override
            public void execute(SysUserOnlineUpdateRequest param) {
                sysUserOnlineManager.updateSysUserOnline(SysUserOnlineAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除在线用户。
     *
     * @param id 在线用户 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable String id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(String id) {
                SysUserOnlineParamChecker.checkId(id);
            }

            @Override
            public void execute(String id) {
                sysUserOnlineManager.deleteSysUserOnline(id);
            }
        });
    }
}
