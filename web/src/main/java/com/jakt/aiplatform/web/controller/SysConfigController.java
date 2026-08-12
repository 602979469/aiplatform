package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.SysConfigManager;
import com.jakt.aiplatform.web.assembler.SysConfigAssembler;
import com.jakt.aiplatform.web.checker.SysConfigParamChecker;
import com.jakt.aiplatform.web.param.SysConfigCreateRequest;
import com.jakt.aiplatform.web.param.SysConfigQueryRequest;
import com.jakt.aiplatform.web.param.SysConfigUpdateRequest;
import com.jakt.aiplatform.web.result.SysConfigResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysConfig;
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
 * 参数配置管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/sysConfigs")
@Tag(name = "参数配置管理")
public class SysConfigController {

    /** 参数配置 Manager。 */
    private final SysConfigManager sysConfigManager;

    public SysConfigController(SysConfigManager sysConfigManager) {
        this.sysConfigManager = sysConfigManager;
    }

    /**
     * 创建参数配置。
     *
     * @param request 创建参数配置请求体
     * @return 创建成功后的参数配置信息
     */
    @PostMapping
    public AiPlatformResult<SysConfigResponse> create(@RequestBody SysConfigCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysConfigCreateRequest param) {
                SysConfigParamChecker.checkSysConfigCreateRequest(param);
            }

            @Override
            public SysConfigResponse execute(SysConfigCreateRequest param) {
                SysConfig sysConfig = sysConfigManager.createSysConfig(SysConfigAssembler.toModel(param));
                return SysConfigAssembler.toResponse(sysConfig);
            }

            @Override
            public void afterService(SysConfigCreateRequest param, SysConfigResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询参数配置。
     *
     * @param id 参数配置 ID
     * @return 参数配置信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<SysConfigResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                SysConfigParamChecker.checkId(param);
            }

            @Override
            public SysConfigResponse execute(Long param) {
                SysConfig sysConfig = sysConfigManager.getSysConfig(param);
                AiPlatformInvoker.throwErrWhenNull(sysConfig, ErrorCodeEnum.RESOURCE_NOT_FOUND, "参数配置不存在");
                return SysConfigAssembler.toResponse(sysConfig);
            }

            @Override
            public void afterService(Long param, SysConfigResponse result) {
            }
        });
    }

    /**
     * 分页查询参数配置。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<SysConfigResponse>> page(SysConfigQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysConfigQueryRequest param) {
                SysConfigParamChecker.checkSysConfigQueryRequest(param);
            }

            @Override
            public PageResult<SysConfigResponse> execute(SysConfigQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new SysConfigQueryRequest());
                PageResult<SysConfig> page = sysConfigManager.pageSysConfigs(SysConfigAssembler.toQueryParam(param));
                return new PageResult<>(page.getTotal(), param.getPageNum(), param.getPageSize(),
                        page.getDataList().stream().map(SysConfigAssembler::toResponse).toList());
            }
        });
    }

    /**
     * 更新参数配置（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      参数配置 ID
     * @param request 更新内容
     * @return 更新后的参数配置信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody SysConfigUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(SysConfigUpdateRequest param) {
                SysConfigParamChecker.checkId(id);
                SysConfigParamChecker.checkSysConfigUpdateRequest(param);
            }

            @Override
            public void execute(SysConfigUpdateRequest param) {
                sysConfigManager.updateSysConfig(SysConfigAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除参数配置。
     *
     * @param id 参数配置 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                SysConfigParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                sysConfigManager.deleteSysConfig(id);
            }
        });
    }
}
