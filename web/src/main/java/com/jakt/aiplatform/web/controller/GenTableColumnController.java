package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.GenTableColumnManager;
import com.jakt.aiplatform.web.assembler.GenTableColumnAssembler;
import com.jakt.aiplatform.web.checker.GenTableColumnParamChecker;
import com.jakt.aiplatform.web.param.GenTableColumnCreateRequest;
import com.jakt.aiplatform.web.param.GenTableColumnQueryRequest;
import com.jakt.aiplatform.web.param.GenTableColumnUpdateRequest;
import com.jakt.aiplatform.web.result.GenTableColumnResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.GenTableColumn;
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
 * 代码生成字段管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/genTableColumns")
@Tag(name = "代码生成字段管理")
public class GenTableColumnController {

    /** 代码生成字段 Manager。 */
    private final GenTableColumnManager genTableColumnManager;

    public GenTableColumnController(GenTableColumnManager genTableColumnManager) {
        this.genTableColumnManager = genTableColumnManager;
    }

    /**
     * 创建代码生成字段。
     *
     * @param request 创建代码生成字段请求体
     * @return 创建成功后的代码生成字段信息
     */
    @PostMapping
    public AiPlatformResult<GenTableColumnResponse> create(@RequestBody GenTableColumnCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(GenTableColumnCreateRequest param) {
                GenTableColumnParamChecker.checkGenTableColumnCreateRequest(param);
            }

            @Override
            public GenTableColumnResponse execute(GenTableColumnCreateRequest param) {
                GenTableColumn genTableColumn = genTableColumnManager.createGenTableColumn(GenTableColumnAssembler.toModel(param));
                return GenTableColumnAssembler.toResponse(genTableColumn);
            }

            @Override
            public void afterService(GenTableColumnCreateRequest param, GenTableColumnResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询代码生成字段。
     *
     * @param id 代码生成字段 ID
     * @return 代码生成字段信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<GenTableColumnResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                GenTableColumnParamChecker.checkId(param);
            }

            @Override
            public GenTableColumnResponse execute(Long param) {
                GenTableColumn genTableColumn = genTableColumnManager.getGenTableColumn(param);
                AiPlatformInvoker.throwErrWhenNull(genTableColumn, ErrorCodeEnum.RESOURCE_NOT_FOUND, "代码生成字段不存在");
                return GenTableColumnAssembler.toResponse(genTableColumn);
            }

            @Override
            public void afterService(Long param, GenTableColumnResponse result) {
            }
        });
    }

    /**
     * 分页查询代码生成字段。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<GenTableColumnResponse>> page(GenTableColumnQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(GenTableColumnQueryRequest param) {
                GenTableColumnParamChecker.checkGenTableColumnQueryRequest(param);
            }

            @Override
            public PageResult<GenTableColumnResponse> execute(GenTableColumnQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new GenTableColumnQueryRequest());
                PageResult<GenTableColumn> page = genTableColumnManager.pageGenTableColumns(GenTableColumnAssembler.toQueryParam(param));
                return new PageResult<>(page.getTotal(), param.getPageNum(), param.getPageSize(),
                        page.getDataList().stream().map(GenTableColumnAssembler::toResponse).toList());
            }
        });
    }

    /**
     * 更新代码生成字段（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      代码生成字段 ID
     * @param request 更新内容
     * @return 更新后的代码生成字段信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody GenTableColumnUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(GenTableColumnUpdateRequest param) {
                GenTableColumnParamChecker.checkId(id);
                GenTableColumnParamChecker.checkGenTableColumnUpdateRequest(param);
            }

            @Override
            public void execute(GenTableColumnUpdateRequest param) {
                genTableColumnManager.updateGenTableColumn(GenTableColumnAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除代码生成字段。
     *
     * @param id 代码生成字段 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                GenTableColumnParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                genTableColumnManager.deleteGenTableColumn(id);
            }
        });
    }
}
