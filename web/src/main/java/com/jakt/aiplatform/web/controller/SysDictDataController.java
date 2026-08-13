package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.biz.service.SysDictDataManager;
import com.jakt.aiplatform.web.assembler.SysDictDataAssembler;
import com.jakt.aiplatform.web.checker.SysDictDataParamChecker;
import com.jakt.aiplatform.web.param.SysDictDataCreateRequest;
import com.jakt.aiplatform.web.param.SysDictDataQueryRequest;
import com.jakt.aiplatform.web.param.SysDictDataUpdateRequest;
import com.jakt.aiplatform.web.result.SysDictDataResponse;
import com.jakt.aiplatform.web.result.AiPlatformResult;
import com.jakt.aiplatform.web.template.AiPlatformTemplate;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.SysDictData;
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
 * 字典数据管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 AiPlatformTemplate。
 */
@RestController
@RequestMapping("/api/v1/sysDictDatas")
@Tag(name = "字典数据管理")
public class SysDictDataController {

    /** 字典数据 Manager。 */
    private final SysDictDataManager sysDictDataManager;

    public SysDictDataController(SysDictDataManager sysDictDataManager) {
        this.sysDictDataManager = sysDictDataManager;
    }

    /**
     * 创建字典数据。
     *
     * @param request 创建字典数据请求体
     * @return 创建成功后的字典数据信息
     */
    @PostMapping
    public AiPlatformResult<SysDictDataResponse> create(@RequestBody SysDictDataCreateRequest request) {
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysDictDataCreateRequest param) {
                SysDictDataParamChecker.checkSysDictDataCreateRequest(param);
            }

            @Override
            public SysDictDataResponse execute(SysDictDataCreateRequest param) {
                // TODO 接入 SysDictDataManager（RuoYi 移植过渡）
                return null;
            }

            @Override
            public void afterService(SysDictDataCreateRequest param, SysDictDataResponse result) {
            }
        });
    }

    /**
     * 按 ID 查询字典数据。
     *
     * @param id 字典数据 ID
     * @return 字典数据信息
     */
    @GetMapping("/{id}")
    public AiPlatformResult<SysDictDataResponse> get(@PathVariable Long id) {
        return AiPlatformTemplate.execute(id, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                SysDictDataParamChecker.checkId(param);
            }

            @Override
            public SysDictDataResponse execute(Long param) {
                // TODO 接入 SysDictDataManager（RuoYi 移植过渡）
                return null;
            }

            @Override
            public void afterService(Long param, SysDictDataResponse result) {
            }
        });
    }

    /**
     * 分页查询字典数据。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public AiPlatformResult<PageResult<SysDictDataResponse>> page(SysDictDataQueryRequest request) {
        
        return AiPlatformTemplate.execute(request, new AiPlatformTemplate.Callback<>() {

            @Override
            public void beforeService(SysDictDataQueryRequest param) {
                SysDictDataParamChecker.checkSysDictDataQueryRequest(param);
            }

            @Override
            public PageResult<SysDictDataResponse> execute(SysDictDataQueryRequest param) {
                // TODO 接入 SysDictDataManager（RuoYi 移植过渡）
                return null;
            }
        });
    }

    /**
     * 更新字典数据（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id      字典数据 ID
     * @param request 更新内容
     * @return 更新后的字典数据信息
     */
    @PutMapping("/{id}")
    public AiPlatformResult<Void> update(@PathVariable Long id, @RequestBody SysDictDataUpdateRequest request) {
        return AiPlatformTemplate.executeWithoutResult(request, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(SysDictDataUpdateRequest param) {
                SysDictDataParamChecker.checkId(id);
                SysDictDataParamChecker.checkSysDictDataUpdateRequest(param);
            }

            @Override
            public void execute(SysDictDataUpdateRequest param) {
                // TODO 接入 SysDictDataManager（RuoYi 移植过渡）
            }
        });
    }

    /**
     * 删除字典数据。
     *
     * @param id 字典数据 ID
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public AiPlatformResult<Void> delete(@PathVariable Long id) {
        return AiPlatformTemplate.executeWithoutResult(id, new AiPlatformTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(Long id) {
                SysDictDataParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                // TODO 接入 SysDictDataManager（RuoYi 移植过渡）
            }
        });
    }
}
