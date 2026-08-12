package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.SysLogininforManager;
import com.jakt.aiplatform.web.assembler.SysLogininforAssembler;
import com.jakt.aiplatform.web.checker.SysLogininforParamChecker;
import com.jakt.aiplatform.web.param.SysLogininforCreateRequest;
import com.jakt.aiplatform.web.param.SysLogininforQueryRequest;
import com.jakt.aiplatform.web.param.SysLogininforUpdateRequest;
import com.jakt.aiplatform.web.result.SysLogininforResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysLogininfor;
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
 * 登录日志管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/sysLogininfors")
@Tag(name = "登录日志管理")
public class SysLogininforController {

    /** 登录日志 Manager。 */
    private final SysLogininforManager sysLogininforManager;

    public SysLogininforController(SysLogininforManager sysLogininforManager) {
        this.sysLogininforManager = sysLogininforManager;
    }

    /**
     * 创建登录日志。
     *
     * @param request 创建登录日志请求体
     * @return 创建成功后的登录日志信息
     */
    @PostMapping
    public AiPlatformResult<SysLogininforResponse> create(@RequestBody SysLogininforCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysLogininforCreateRequest param) {
                SysLogininforParamChecker.checkSysLogininforCreateRequest(param);
            }

            @Override
            public SysLogininforResponse execute(SysLogininforCreateRequest param) {
                SysLogininfor sysLogininfor = sysLogininforManager.createSysLogininfor(SysLogininforAssembler.toModel(param));
                return SysLogininforAssembler.toResponse(sysLogininfor);
            }

            @Override
            public void afterService(SysLogininforCreateRequest param, SysLogininforResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询登录日志。
     *
     * @param id 登录日志 ID
     * @return 登录日志信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<SysLogininforResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                SysLogininforParamChecker.checkId(param);
            }

            @Override
            public SysLogininforResponse execute(Long param) {
                SysLogininfor sysLogininfor = sysLogininforManager.getSysLogininfor(param);
                AiPlatformInvoker.throwErrWhenNull(sysLogininfor, ErrorCodeEnum.RESOURCE_NOT_FOUND, "登录日志不存在");
                return SysLogininforAssembler.toResponse(sysLogininfor);
            }

            @Override
            public void afterService(Long param, SysLogininforResponse result) {
            }
        });
    }

    /**
     * 分页查询登录日志。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<SysLogininforResponse>> page(SysLogininforQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysLogininforQueryRequest param) {
                SysLogininforParamChecker.checkSysLogininforQueryRequest(param);
            }

            @Override
            public PageResult<SysLogininforResponse> execute(SysLogininforQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new SysLogininforQueryRequest());
                PageResult<SysLogininfor> page = sysLogininforManager.pageSysLogininfors(SysLogininforAssembler.toQueryParam(param));
                return new PageResult<>(page.getTotal(), param.getPageNum(), param.getPageSize(),
                        page.getDataList().stream().map(SysLogininforAssembler::toResponse).toList());
            }
        });
    }

    /**
     * 更新登录日志（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      登录日志 ID
     * @param request 更新内容
     * @return 更新后的登录日志信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody SysLogininforUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(SysLogininforUpdateRequest param) {
                SysLogininforParamChecker.checkId(id);
                SysLogininforParamChecker.checkSysLogininforUpdateRequest(param);
            }

            @Override
            public void execute(SysLogininforUpdateRequest param) {
                sysLogininforManager.updateSysLogininfor(SysLogininforAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除登录日志。
     *
     * @param id 登录日志 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                SysLogininforParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                sysLogininforManager.deleteSysLogininfor(id);
            }
        });
    }
}
