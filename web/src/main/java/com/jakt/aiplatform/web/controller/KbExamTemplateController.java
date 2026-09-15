package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.template.ApiTemplate;
import com.jakt.aiplatform.biz.service.KbExamTemplateManager;
import com.jakt.aiplatform.core.model.domain.KbExamTemplate;
import com.jakt.aiplatform.web.assembler.KbExamTemplateAssembler;
import com.jakt.aiplatform.web.checker.KbExamTemplateParamChecker;
import com.jakt.aiplatform.web.param.KbExamTemplateCreateRequest;
import com.jakt.aiplatform.web.param.KbExamTemplateQueryRequest;
import com.jakt.aiplatform.web.param.KbExamTemplateUpdateRequest;
import com.jakt.aiplatform.web.result.KbExamTemplateResponse;
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
 * 试卷模板管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 ApiTemplate。
 */
@RestController
@RequestMapping("/api/v1/kbExamTemplates")
@Tag(name = "试卷模板管理")
public class KbExamTemplateController {

    /** 试卷模板 Manager。 */
    private final KbExamTemplateManager kbExamTemplateManager;

    public KbExamTemplateController(KbExamTemplateManager kbExamTemplateManager) {
        this.kbExamTemplateManager = kbExamTemplateManager;
    }

    /**
     * 创建试卷模板。
     *
     * @param request 创建试卷模板请求体
     * @return 创建成功后的试卷模板信息
     */
    @PostMapping
    public ApiResult<KbExamTemplateResponse> create(@RequestBody KbExamTemplateCreateRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(KbExamTemplateCreateRequest param) {
                KbExamTemplateParamChecker.checkKbExamTemplateCreateRequest(param);
            }

            @Override
            public KbExamTemplateResponse execute(KbExamTemplateCreateRequest param) {
                KbExamTemplate kbExamTemplate = kbExamTemplateManager.createKbExamTemplate(KbExamTemplateAssembler.toModel(param));
                return KbExamTemplateAssembler.toResponse(kbExamTemplate);
            }
        });
    }

    /**
     * 按主键查询试卷模板。
     *
     * @param id 试卷模板主键
     * @return 试卷模板信息
     */
    @GetMapping("/{id}")
    public ApiResult<KbExamTemplateResponse> get(@PathVariable Long id) {
        return ApiTemplate.execute(id, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                KbExamTemplateParamChecker.checkId(id);
            }

            @Override
            public KbExamTemplateResponse execute(Long param) {
                KbExamTemplate kbExamTemplate = kbExamTemplateManager.getKbExamTemplate(id);
                AssertUtil.throwErrWhenNull(kbExamTemplate, ErrorCodeEnum.PARAM_INVALID, "试卷模板不存在");
                return KbExamTemplateAssembler.toResponse(kbExamTemplate);
            }
        });
    }

    /**
     * 分页查询试卷模板。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public ApiResult<PageResult<KbExamTemplateResponse>> page(KbExamTemplateQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(KbExamTemplateQueryRequest param) {
                KbExamTemplateParamChecker.checkKbExamTemplateQueryRequest(param);
            }

            @Override
            public PageResult<KbExamTemplateResponse> execute(KbExamTemplateQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new KbExamTemplateQueryRequest());
                PageResult<KbExamTemplate> page = kbExamTemplateManager.pageKbExamTemplates(KbExamTemplateAssembler.toQueryParam(param));
                return ConvertUtil.mapPage(page, KbExamTemplateAssembler::toResponse);
            }
        });
    }

    /**
     * 更新试卷模板（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id 试卷模板主键
     * @param request 更新内容
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody KbExamTemplateUpdateRequest request) {
        return ApiTemplate.executeWithoutResult(request, new ApiTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(KbExamTemplateUpdateRequest param) {
                KbExamTemplateParamChecker.checkId(id);
                KbExamTemplateParamChecker.checkKbExamTemplateUpdateRequest(param);
            }

            @Override
            public void execute(KbExamTemplateUpdateRequest param) {
                kbExamTemplateManager.updateKbExamTemplate(KbExamTemplateAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除试卷模板。
     *
     * @param id 试卷模板主键
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        return ApiTemplate.executeWithoutResult(id, new ApiTemplate.CallbackWithoutResult<Long>() {

            @Override
            public void beforeService(Long id) {
                KbExamTemplateParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                kbExamTemplateManager.deleteKbExamTemplate(id);
            }
        });
    }
}
