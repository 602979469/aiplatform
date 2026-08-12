package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.SysDictTypeManager;
import com.jakt.aiplatform.web.assembler.SysDictTypeAssembler;
import com.jakt.aiplatform.web.checker.SysDictTypeParamChecker;
import com.jakt.aiplatform.web.param.SysDictTypeCreateRequest;
import com.jakt.aiplatform.web.param.SysDictTypeQueryRequest;
import com.jakt.aiplatform.web.param.SysDictTypeUpdateRequest;
import com.jakt.aiplatform.web.result.SysDictTypeResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysDictType;
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
 * 字典类型管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/sysDictTypes")
@Tag(name = "字典类型管理")
public class SysDictTypeController {

    /** 字典类型 Manager。 */
    private final SysDictTypeManager sysDictTypeManager;

    public SysDictTypeController(SysDictTypeManager sysDictTypeManager) {
        this.sysDictTypeManager = sysDictTypeManager;
    }

    /**
     * 创建字典类型。
     *
     * @param request 创建字典类型请求体
     * @return 创建成功后的字典类型信息
     */
    @PostMapping
    public AiPlatformResult<SysDictTypeResponse> create(@RequestBody SysDictTypeCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysDictTypeCreateRequest param) {
                SysDictTypeParamChecker.checkSysDictTypeCreateRequest(param);
            }

            @Override
            public SysDictTypeResponse execute(SysDictTypeCreateRequest param) {
                SysDictType sysDictType = sysDictTypeManager.createSysDictType(SysDictTypeAssembler.toModel(param));
                return SysDictTypeAssembler.toResponse(sysDictType);
            }

            @Override
            public void afterService(SysDictTypeCreateRequest param, SysDictTypeResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询字典类型。
     *
     * @param id 字典类型 ID
     * @return 字典类型信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<SysDictTypeResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                SysDictTypeParamChecker.checkId(param);
            }

            @Override
            public SysDictTypeResponse execute(Long param) {
                SysDictType sysDictType = sysDictTypeManager.getSysDictType(param);
                AiPlatformInvoker.throwErrWhenNull(sysDictType, ErrorCodeEnum.RESOURCE_NOT_FOUND, "字典类型不存在");
                return SysDictTypeAssembler.toResponse(sysDictType);
            }

            @Override
            public void afterService(Long param, SysDictTypeResponse result) {
            }
        });
    }

    /**
     * 分页查询字典类型。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<SysDictTypeResponse>> page(SysDictTypeQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysDictTypeQueryRequest param) {
                SysDictTypeParamChecker.checkSysDictTypeQueryRequest(param);
            }

            @Override
            public PageResult<SysDictTypeResponse> execute(SysDictTypeQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new SysDictTypeQueryRequest());
                PageResult<SysDictType> page = sysDictTypeManager.pageSysDictTypes(SysDictTypeAssembler.toQueryParam(param));
                return new PageResult<>(page.getTotal(), param.getPageNum(), param.getPageSize(),
                        page.getDataList().stream().map(SysDictTypeAssembler::toResponse).toList());
            }
        });
    }

    /**
     * 更新字典类型（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      字典类型 ID
     * @param request 更新内容
     * @return 更新后的字典类型信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody SysDictTypeUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(SysDictTypeUpdateRequest param) {
                SysDictTypeParamChecker.checkId(id);
                SysDictTypeParamChecker.checkSysDictTypeUpdateRequest(param);
            }

            @Override
            public void execute(SysDictTypeUpdateRequest param) {
                sysDictTypeManager.updateSysDictType(SysDictTypeAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除字典类型。
     *
     * @param id 字典类型 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                SysDictTypeParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                sysDictTypeManager.deleteSysDictType(id);
            }
        });
    }
}
