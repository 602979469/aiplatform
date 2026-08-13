package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.SysJobLogManager;
import com.jakt.aiplatform.web.assembler.SysJobLogAssembler;
import com.jakt.aiplatform.web.checker.SysJobLogParamChecker;
import com.jakt.aiplatform.web.param.SysJobLogCreateRequest;
import com.jakt.aiplatform.web.param.SysJobLogQueryRequest;
import com.jakt.aiplatform.web.param.SysJobLogUpdateRequest;
import com.jakt.aiplatform.web.result.SysJobLogResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysJobLog;
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
 * 定时任务日志管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/sysJobLogs")
@Tag(name = "定时任务日志管理")
public class SysJobLogController {

    /** 定时任务日志 Manager。 */
    private final SysJobLogManager sysJobLogManager;

    public SysJobLogController(SysJobLogManager sysJobLogManager) {
        this.sysJobLogManager = sysJobLogManager;
    }

    /**
     * 创建定时任务日志。
     *
     * @param request 创建定时任务日志请求体
     * @return 创建成功后的定时任务日志信息
     */
    @PostMapping
    public AiPlatformResult<SysJobLogResponse> create(@RequestBody SysJobLogCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysJobLogCreateRequest param) {
                SysJobLogParamChecker.checkSysJobLogCreateRequest(param);
            }

            @Override
            public SysJobLogResponse execute(SysJobLogCreateRequest param) {
                // TODO 接入 SysJobLogManager（RuoYi 移植过渡）
                return null;
            }

            @Override
            public void afterService(SysJobLogCreateRequest param, SysJobLogResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询定时任务日志。
     *
     * @param id 定时任务日志 ID
     * @return 定时任务日志信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<SysJobLogResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                SysJobLogParamChecker.checkId(param);
            }

            @Override
            public SysJobLogResponse execute(Long param) {
                // TODO 接入 SysJobLogManager（RuoYi 移植过渡）
                return null;
            }

            @Override
            public void afterService(Long param, SysJobLogResponse result) {
            }
        });
    }

    /**
     * 分页查询定时任务日志。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<SysJobLogResponse>> page(SysJobLogQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysJobLogQueryRequest param) {
                SysJobLogParamChecker.checkSysJobLogQueryRequest(param);
            }

            @Override
            public PageResult<SysJobLogResponse> execute(SysJobLogQueryRequest param) {
                // TODO 接入 SysJobLogManager（RuoYi 移植过渡）
                return null;
            }
        });
    }

    /**
     * 更新定时任务日志（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      定时任务日志 ID
     * @param request 更新内容
     * @return 更新后的定时任务日志信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody SysJobLogUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(SysJobLogUpdateRequest param) {
                SysJobLogParamChecker.checkId(id);
                SysJobLogParamChecker.checkSysJobLogUpdateRequest(param);
            }

            @Override
            public void execute(SysJobLogUpdateRequest param) {
                // TODO 接入 SysJobLogManager（RuoYi 移植过渡）
            }
        });
    }

    /**
     * 删除定时任务日志。
     *
     * @param id 定时任务日志 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                SysJobLogParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                // TODO 接入 SysJobLogManager（RuoYi 移植过渡）
            }
        });
    }
}
