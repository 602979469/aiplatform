package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.GenTableManager;
import com.jakt.aiplatform.web.assembler.GenTableAssembler;
import com.jakt.aiplatform.web.checker.GenTableParamChecker;
import com.jakt.aiplatform.web.param.GenTableCreateRequest;
import com.jakt.aiplatform.web.param.GenTableQueryRequest;
import com.jakt.aiplatform.web.param.GenTableUpdateRequest;
import com.jakt.aiplatform.web.result.GenTableResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.GenTable;
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
 * 代码生成管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/genTables")
@Tag(name = "代码生成管理")
public class GenTableController {

    /** 代码生成 Manager。 */
    private final GenTableManager genTableManager;

    public GenTableController(GenTableManager genTableManager) {
        this.genTableManager = genTableManager;
    }

    /**
     * 创建代码生成。
     *
     * @param request 创建代码生成请求体
     * @return 创建成功后的代码生成信息
     */
    @PostMapping
    public AiPlatformResult<GenTableResponse> create(@RequestBody GenTableCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(GenTableCreateRequest param) {
                GenTableParamChecker.checkGenTableCreateRequest(param);
            }

            @Override
            public GenTableResponse execute(GenTableCreateRequest param) {
                GenTable genTable = genTableManager.createGenTable(GenTableAssembler.toModel(param));
                return GenTableAssembler.toResponse(genTable);
            }

            @Override
            public void afterService(GenTableCreateRequest param, GenTableResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询代码生成。
     *
     * @param id 代码生成 ID
     * @return 代码生成信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<GenTableResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                GenTableParamChecker.checkId(param);
            }

            @Override
            public GenTableResponse execute(Long param) {
                GenTable genTable = genTableManager.getGenTable(param);
                AiPlatformInvoker.throwErrWhenNull(genTable, ErrorCodeEnum.RESOURCE_NOT_FOUND, "代码生成不存在");
                return GenTableAssembler.toResponse(genTable);
            }

            @Override
            public void afterService(Long param, GenTableResponse result) {
            }
        });
    }

    /**
     * 分页查询代码生成。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<GenTableResponse>> page(GenTableQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(GenTableQueryRequest param) {
                GenTableParamChecker.checkGenTableQueryRequest(param);
            }

            @Override
            public PageResult<GenTableResponse> execute(GenTableQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new GenTableQueryRequest());
                PageResult<GenTable> page = genTableManager.pageGenTables(GenTableAssembler.toQueryParam(param));
                return new PageResult<>(page.getTotal(), param.getPageNum(), param.getPageSize(),
                        page.getDataList().stream().map(GenTableAssembler::toResponse).toList());
            }
        });
    }

    /**
     * 更新代码生成（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      代码生成 ID
     * @param request 更新内容
     * @return 更新后的代码生成信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody GenTableUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(GenTableUpdateRequest param) {
                GenTableParamChecker.checkId(id);
                GenTableParamChecker.checkGenTableUpdateRequest(param);
            }

            @Override
            public void execute(GenTableUpdateRequest param) {
                genTableManager.updateGenTable(GenTableAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除代码生成。
     *
     * @param id 代码生成 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                GenTableParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                genTableManager.deleteGenTable(id);
            }
        });
    }
}
