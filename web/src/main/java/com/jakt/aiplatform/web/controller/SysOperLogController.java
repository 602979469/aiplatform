package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.SysOperLogManager;
import com.jakt.aiplatform.web.assembler.SysOperLogAssembler;
import com.jakt.aiplatform.web.checker.SysOperLogParamChecker;
import com.jakt.aiplatform.web.param.SysOperLogCreateRequest;
import com.jakt.aiplatform.web.param.SysOperLogQueryRequest;
import com.jakt.aiplatform.web.param.SysOperLogUpdateRequest;
import com.jakt.aiplatform.web.result.SysOperLogResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysOperLog;
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
 * 操作日志管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/sysOperLogs")
@Tag(name = "操作日志管理")
public class SysOperLogController {

    /** 操作日志 Manager。 */
    private final SysOperLogManager sysOperLogManager;

    public SysOperLogController(SysOperLogManager sysOperLogManager) {
        this.sysOperLogManager = sysOperLogManager;
    }

    /**
     * 创建操作日志。
     *
     * @param request 创建操作日志请求体
     * @return 创建成功后的操作日志信息
     */
    @PostMapping
    public AiPlatformResult<SysOperLogResponse> create(@RequestBody SysOperLogCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysOperLogCreateRequest param) {
                SysOperLogParamChecker.checkSysOperLogCreateRequest(param);
            }

            @Override
            public SysOperLogResponse execute(SysOperLogCreateRequest param) {
                SysOperLog sysOperLog = sysOperLogManager.createSysOperLog(SysOperLogAssembler.toModel(param));
                return SysOperLogAssembler.toResponse(sysOperLog);
            }

            @Override
            public void afterService(SysOperLogCreateRequest param, SysOperLogResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询操作日志。
     *
     * @param id 操作日志 ID
     * @return 操作日志信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<SysOperLogResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                SysOperLogParamChecker.checkId(param);
            }

            @Override
            public SysOperLogResponse execute(Long param) {
                SysOperLog sysOperLog = sysOperLogManager.getSysOperLog(param);
                AiPlatformInvoker.throwErrWhenNull(sysOperLog, ErrorCodeEnum.RESOURCE_NOT_FOUND, "操作日志不存在");
                return SysOperLogAssembler.toResponse(sysOperLog);
            }

            @Override
            public void afterService(Long param, SysOperLogResponse result) {
            }
        });
    }

    /**
     * 分页查询操作日志。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<SysOperLogResponse>> page(SysOperLogQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysOperLogQueryRequest param) {
                SysOperLogParamChecker.checkSysOperLogQueryRequest(param);
            }

            @Override
            public PageResult<SysOperLogResponse> execute(SysOperLogQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new SysOperLogQueryRequest());
                PageResult<SysOperLog> page = sysOperLogManager.pageSysOperLogs(SysOperLogAssembler.toQueryParam(param));
                return new PageResult<>(page.getTotal(), param.getPageNum(), param.getPageSize(),
                        page.getDataList().stream().map(SysOperLogAssembler::toResponse).toList());
            }
        });
    }

    /**
     * 更新操作日志（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      操作日志 ID
     * @param request 更新内容
     * @return 更新后的操作日志信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody SysOperLogUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(SysOperLogUpdateRequest param) {
                SysOperLogParamChecker.checkId(id);
                SysOperLogParamChecker.checkSysOperLogUpdateRequest(param);
            }

            @Override
            public void execute(SysOperLogUpdateRequest param) {
                sysOperLogManager.updateSysOperLog(SysOperLogAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除操作日志。
     *
     * @param id 操作日志 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                SysOperLogParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                sysOperLogManager.deleteSysOperLog(id);
            }
        });
    }
}
