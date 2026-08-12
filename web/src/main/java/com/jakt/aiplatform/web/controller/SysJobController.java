package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.SysJobManager;
import com.jakt.aiplatform.web.assembler.SysJobAssembler;
import com.jakt.aiplatform.web.checker.SysJobParamChecker;
import com.jakt.aiplatform.web.param.SysJobCreateRequest;
import com.jakt.aiplatform.web.param.SysJobQueryRequest;
import com.jakt.aiplatform.web.param.SysJobUpdateRequest;
import com.jakt.aiplatform.web.result.SysJobResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysJob;
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
 * 定时任务管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/sysJobs")
@Tag(name = "定时任务管理")
public class SysJobController {

    /** 定时任务 Manager。 */
    private final SysJobManager sysJobManager;

    public SysJobController(SysJobManager sysJobManager) {
        this.sysJobManager = sysJobManager;
    }

    /**
     * 创建定时任务。
     *
     * @param request 创建定时任务请求体
     * @return 创建成功后的定时任务信息
     */
    @PostMapping
    public AiPlatformResult<SysJobResponse> create(@RequestBody SysJobCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysJobCreateRequest param) {
                SysJobParamChecker.checkSysJobCreateRequest(param);
            }

            @Override
            public SysJobResponse execute(SysJobCreateRequest param) {
                SysJob sysJob = sysJobManager.createSysJob(SysJobAssembler.toModel(param));
                return SysJobAssembler.toResponse(sysJob);
            }

            @Override
            public void afterService(SysJobCreateRequest param, SysJobResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询定时任务。
     *
     * @param id 定时任务 ID
     * @return 定时任务信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<SysJobResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                SysJobParamChecker.checkId(param);
            }

            @Override
            public SysJobResponse execute(Long param) {
                SysJob sysJob = sysJobManager.getSysJob(param);
                AiPlatformInvoker.throwErrWhenNull(sysJob, ErrorCodeEnum.RESOURCE_NOT_FOUND, "定时任务不存在");
                return SysJobAssembler.toResponse(sysJob);
            }

            @Override
            public void afterService(Long param, SysJobResponse result) {
            }
        });
    }

    /**
     * 分页查询定时任务。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<SysJobResponse>> page(SysJobQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysJobQueryRequest param) {
                SysJobParamChecker.checkSysJobQueryRequest(param);
            }

            @Override
            public PageResult<SysJobResponse> execute(SysJobQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new SysJobQueryRequest());
                PageResult<SysJob> page = sysJobManager.pageSysJobs(SysJobAssembler.toQueryParam(param));
                return new PageResult<>(page.getTotal(), param.getPageNum(), param.getPageSize(),
                        page.getDataList().stream().map(SysJobAssembler::toResponse).toList());
            }
        });
    }

    /**
     * 更新定时任务（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      定时任务 ID
     * @param request 更新内容
     * @return 更新后的定时任务信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody SysJobUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(SysJobUpdateRequest param) {
                SysJobParamChecker.checkId(id);
                SysJobParamChecker.checkSysJobUpdateRequest(param);
            }

            @Override
            public void execute(SysJobUpdateRequest param) {
                sysJobManager.updateSysJob(SysJobAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除定时任务。
     *
     * @param id 定时任务 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                SysJobParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                sysJobManager.deleteSysJob(id);
            }
        });
    }
}
