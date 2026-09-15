package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.template.ApiTemplate;
import com.jakt.aiplatform.biz.service.KbExamTemplateRuleManager;
import com.jakt.aiplatform.core.model.domain.KbExamTemplateRule;
import com.jakt.aiplatform.web.assembler.KbExamTemplateRuleAssembler;
import com.jakt.aiplatform.web.checker.KbExamTemplateRuleParamChecker;
import com.jakt.aiplatform.web.param.KbExamTemplateRuleCreateRequest;
import com.jakt.aiplatform.web.param.KbExamTemplateRuleQueryRequest;
import com.jakt.aiplatform.web.param.KbExamTemplateRuleUpdateRequest;
import com.jakt.aiplatform.web.result.KbExamTemplateRuleResponse;
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
 * 试卷模板知识点规则管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 ApiTemplate。
 */
@RestController
@RequestMapping("/api/v1/kbExamTemplateRules")
@Tag(name = "试卷模板知识点规则管理")
public class KbExamTemplateRuleController {

    /** 试卷模板知识点规则 Manager。 */
    private final KbExamTemplateRuleManager kbExamTemplateRuleManager;

    public KbExamTemplateRuleController(KbExamTemplateRuleManager kbExamTemplateRuleManager) {
        this.kbExamTemplateRuleManager = kbExamTemplateRuleManager;
    }

    /**
     * 创建试卷模板知识点规则。
     *
     * @param request 创建试卷模板知识点规则请求体
     * @return 创建成功后的试卷模板知识点规则信息
     */
    @PostMapping
    public ApiResult<KbExamTemplateRuleResponse> create(@RequestBody KbExamTemplateRuleCreateRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(KbExamTemplateRuleCreateRequest param) {
                KbExamTemplateRuleParamChecker.checkKbExamTemplateRuleCreateRequest(param);
            }

            @Override
            public KbExamTemplateRuleResponse execute(KbExamTemplateRuleCreateRequest param) {
                KbExamTemplateRule kbExamTemplateRule = kbExamTemplateRuleManager.createKbExamTemplateRule(KbExamTemplateRuleAssembler.toModel(param));
                return KbExamTemplateRuleAssembler.toResponse(kbExamTemplateRule);
            }
        });
    }

    /**
     * 按主键查询试卷模板知识点规则。
     *
     * @param id 试卷模板知识点规则主键
     * @return 试卷模板知识点规则信息
     */
    @GetMapping("/{id}")
    public ApiResult<KbExamTemplateRuleResponse> get(@PathVariable Long id) {
        return ApiTemplate.execute(id, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                KbExamTemplateRuleParamChecker.checkId(id);
            }

            @Override
            public KbExamTemplateRuleResponse execute(Long param) {
                KbExamTemplateRule kbExamTemplateRule = kbExamTemplateRuleManager.getKbExamTemplateRule(id);
                AssertUtil.throwErrWhenNull(kbExamTemplateRule, ErrorCodeEnum.PARAM_INVALID, "试卷模板知识点规则不存在");
                return KbExamTemplateRuleAssembler.toResponse(kbExamTemplateRule);
            }
        });
    }

    /**
     * 分页查询试卷模板知识点规则。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public ApiResult<PageResult<KbExamTemplateRuleResponse>> page(KbExamTemplateRuleQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(KbExamTemplateRuleQueryRequest param) {
                KbExamTemplateRuleParamChecker.checkKbExamTemplateRuleQueryRequest(param);
            }

            @Override
            public PageResult<KbExamTemplateRuleResponse> execute(KbExamTemplateRuleQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new KbExamTemplateRuleQueryRequest());
                PageResult<KbExamTemplateRule> page = kbExamTemplateRuleManager.pageKbExamTemplateRules(KbExamTemplateRuleAssembler.toQueryParam(param));
                return ConvertUtil.mapPage(page, KbExamTemplateRuleAssembler::toResponse);
            }
        });
    }

    /**
     * 更新试卷模板知识点规则（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id 试卷模板知识点规则主键
     * @param request 更新内容
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody KbExamTemplateRuleUpdateRequest request) {
        return ApiTemplate.executeWithoutResult(request, new ApiTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(KbExamTemplateRuleUpdateRequest param) {
                KbExamTemplateRuleParamChecker.checkId(id);
                KbExamTemplateRuleParamChecker.checkKbExamTemplateRuleUpdateRequest(param);
            }

            @Override
            public void execute(KbExamTemplateRuleUpdateRequest param) {
                kbExamTemplateRuleManager.updateKbExamTemplateRule(KbExamTemplateRuleAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除试卷模板知识点规则。
     *
     * @param id 试卷模板知识点规则主键
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        return ApiTemplate.executeWithoutResult(id, new ApiTemplate.CallbackWithoutResult<Long>() {

            @Override
            public void beforeService(Long id) {
                KbExamTemplateRuleParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                kbExamTemplateRuleManager.deleteKbExamTemplateRule(id);
            }
        });
    }
}
