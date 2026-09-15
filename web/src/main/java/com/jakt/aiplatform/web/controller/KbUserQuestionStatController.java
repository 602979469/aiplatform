package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.template.ApiTemplate;
import com.jakt.aiplatform.biz.service.KbUserQuestionStatManager;
import com.jakt.aiplatform.core.model.domain.KbUserQuestionStat;
import com.jakt.aiplatform.web.assembler.KbUserQuestionStatAssembler;
import com.jakt.aiplatform.web.checker.KbUserQuestionStatParamChecker;
import com.jakt.aiplatform.web.param.KbUserQuestionStatCreateRequest;
import com.jakt.aiplatform.web.param.KbUserQuestionStatQueryRequest;
import com.jakt.aiplatform.web.param.KbUserQuestionStatUpdateRequest;
import com.jakt.aiplatform.web.result.KbUserQuestionStatResponse;
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
 * 用户题目掌握状态管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 ApiTemplate。
 */
@RestController
@RequestMapping("/api/v1/kbUserQuestionStats")
@Tag(name = "用户题目掌握状态管理")
public class KbUserQuestionStatController {

    /** 用户题目掌握状态 Manager。 */
    private final KbUserQuestionStatManager kbUserQuestionStatManager;

    public KbUserQuestionStatController(KbUserQuestionStatManager kbUserQuestionStatManager) {
        this.kbUserQuestionStatManager = kbUserQuestionStatManager;
    }

    /**
     * 创建用户题目掌握状态。
     *
     * @param request 创建用户题目掌握状态请求体
     * @return 创建成功后的用户题目掌握状态信息
     */
    @PostMapping
    public ApiResult<KbUserQuestionStatResponse> create(@RequestBody KbUserQuestionStatCreateRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(KbUserQuestionStatCreateRequest param) {
                KbUserQuestionStatParamChecker.checkKbUserQuestionStatCreateRequest(param);
            }

            @Override
            public KbUserQuestionStatResponse execute(KbUserQuestionStatCreateRequest param) {
                KbUserQuestionStat kbUserQuestionStat = kbUserQuestionStatManager.createKbUserQuestionStat(KbUserQuestionStatAssembler.toModel(param));
                return KbUserQuestionStatAssembler.toResponse(kbUserQuestionStat);
            }
        });
    }

    /**
     * 按主键查询用户题目掌握状态。
     *
     * @param id 用户题目掌握状态主键
     * @return 用户题目掌握状态信息
     */
    @GetMapping("/{id}")
    public ApiResult<KbUserQuestionStatResponse> get(@PathVariable Long id) {
        return ApiTemplate.execute(id, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                KbUserQuestionStatParamChecker.checkId(id);
            }

            @Override
            public KbUserQuestionStatResponse execute(Long param) {
                KbUserQuestionStat kbUserQuestionStat = kbUserQuestionStatManager.getKbUserQuestionStat(id);
                AssertUtil.throwErrWhenNull(kbUserQuestionStat, ErrorCodeEnum.PARAM_INVALID, "用户题目掌握状态不存在");
                return KbUserQuestionStatAssembler.toResponse(kbUserQuestionStat);
            }
        });
    }

    /**
     * 分页查询用户题目掌握状态。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public ApiResult<PageResult<KbUserQuestionStatResponse>> page(KbUserQuestionStatQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(KbUserQuestionStatQueryRequest param) {
                KbUserQuestionStatParamChecker.checkKbUserQuestionStatQueryRequest(param);
            }

            @Override
            public PageResult<KbUserQuestionStatResponse> execute(KbUserQuestionStatQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new KbUserQuestionStatQueryRequest());
                PageResult<KbUserQuestionStat> page = kbUserQuestionStatManager.pageKbUserQuestionStats(KbUserQuestionStatAssembler.toQueryParam(param));
                return ConvertUtil.mapPage(page, KbUserQuestionStatAssembler::toResponse);
            }
        });
    }

    /**
     * 更新用户题目掌握状态（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id 用户题目掌握状态主键
     * @param request 更新内容
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody KbUserQuestionStatUpdateRequest request) {
        return ApiTemplate.executeWithoutResult(request, new ApiTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(KbUserQuestionStatUpdateRequest param) {
                KbUserQuestionStatParamChecker.checkId(id);
                KbUserQuestionStatParamChecker.checkKbUserQuestionStatUpdateRequest(param);
            }

            @Override
            public void execute(KbUserQuestionStatUpdateRequest param) {
                kbUserQuestionStatManager.updateKbUserQuestionStat(KbUserQuestionStatAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除用户题目掌握状态。
     *
     * @param id 用户题目掌握状态主键
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        return ApiTemplate.executeWithoutResult(id, new ApiTemplate.CallbackWithoutResult<Long>() {

            @Override
            public void beforeService(Long id) {
                KbUserQuestionStatParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                kbUserQuestionStatManager.deleteKbUserQuestionStat(id);
            }
        });
    }
}
