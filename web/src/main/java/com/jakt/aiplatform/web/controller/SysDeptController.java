package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.SysDeptManager;
import com.jakt.aiplatform.web.assembler.SysDeptAssembler;
import com.jakt.aiplatform.web.checker.SysDeptParamChecker;
import com.jakt.aiplatform.web.param.SysDeptCreateRequest;
import com.jakt.aiplatform.web.param.SysDeptQueryRequest;
import com.jakt.aiplatform.web.param.SysDeptUpdateRequest;
import com.jakt.aiplatform.web.result.SysDeptResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysDept;
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
 * 部门管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/sysDepts")
@Tag(name = "部门管理")
public class SysDeptController {

    /** 部门 Manager。 */
    private final SysDeptManager sysDeptManager;

    public SysDeptController(SysDeptManager sysDeptManager) {
        this.sysDeptManager = sysDeptManager;
    }

    /**
     * 创建部门。
     *
     * @param request 创建部门请求体
     * @return 创建成功后的部门信息
     */
    @PostMapping
    public AiPlatformResult<SysDeptResponse> create(@RequestBody SysDeptCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysDeptCreateRequest param) {
                SysDeptParamChecker.checkSysDeptCreateRequest(param);
            }

            @Override
            public SysDeptResponse execute(SysDeptCreateRequest param) {
                // TODO 接入 SysDeptManager（RuoYi 移植过渡）
                return null;
            }

            @Override
            public void afterService(SysDeptCreateRequest param, SysDeptResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询部门。
     *
     * @param id 部门 ID
     * @return 部门信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<SysDeptResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                SysDeptParamChecker.checkId(param);
            }

            @Override
            public SysDeptResponse execute(Long param) {
                // TODO 接入 SysDeptManager（RuoYi 移植过渡）
                return null;
            }

            @Override
            public void afterService(Long param, SysDeptResponse result) {
            }
        });
    }

    /**
     * 分页查询部门。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<SysDeptResponse>> page(SysDeptQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysDeptQueryRequest param) {
                SysDeptParamChecker.checkSysDeptQueryRequest(param);
            }

            @Override
            public PageResult<SysDeptResponse> execute(SysDeptQueryRequest param) {
                // TODO 接入 SysDeptManager（RuoYi 移植过渡）
                return null;
            }
        });
    }

    /**
     * 更新部门（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      部门 ID
     * @param request 更新内容
     * @return 更新后的部门信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody SysDeptUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(SysDeptUpdateRequest param) {
                SysDeptParamChecker.checkId(id);
                SysDeptParamChecker.checkSysDeptUpdateRequest(param);
            }

            @Override
            public void execute(SysDeptUpdateRequest param) {
                // TODO 接入 SysDeptManager（RuoYi 移植过渡）
            }
        });
    }

    /**
     * 删除部门。
     *
     * @param id 部门 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                SysDeptParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                // TODO 接入 SysDeptManager（RuoYi 移植过渡）
            }
        });
    }
}
