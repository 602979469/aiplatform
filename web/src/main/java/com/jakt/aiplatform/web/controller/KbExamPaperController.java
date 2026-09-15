package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.template.ApiTemplate;
import com.jakt.aiplatform.biz.service.KbExamPaperManager;
import com.jakt.aiplatform.core.model.domain.KbExamPaper;
import com.jakt.aiplatform.web.assembler.KbExamPaperAssembler;
import com.jakt.aiplatform.web.checker.KbExamPaperParamChecker;
import com.jakt.aiplatform.web.param.KbExamPaperCreateRequest;
import com.jakt.aiplatform.web.param.KbExamPaperQueryRequest;
import com.jakt.aiplatform.web.param.KbExamPaperUpdateRequest;
import com.jakt.aiplatform.web.result.KbExamPaperResponse;
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
 * 考试试卷管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 ApiTemplate。
 */
@RestController
@RequestMapping("/api/v1/kbExamPapers")
@Tag(name = "考试试卷管理")
public class KbExamPaperController {

    /** 考试试卷 Manager。 */
    private final KbExamPaperManager kbExamPaperManager;

    public KbExamPaperController(KbExamPaperManager kbExamPaperManager) {
        this.kbExamPaperManager = kbExamPaperManager;
    }

    /**
     * 创建考试试卷。
     *
     * @param request 创建考试试卷请求体
     * @return 创建成功后的考试试卷信息
     */
    @PostMapping
    public ApiResult<KbExamPaperResponse> create(@RequestBody KbExamPaperCreateRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(KbExamPaperCreateRequest param) {
                KbExamPaperParamChecker.checkKbExamPaperCreateRequest(param);
            }

            @Override
            public KbExamPaperResponse execute(KbExamPaperCreateRequest param) {
                KbExamPaper kbExamPaper = kbExamPaperManager.createKbExamPaper(KbExamPaperAssembler.toModel(param));
                return KbExamPaperAssembler.toResponse(kbExamPaper);
            }
        });
    }

    /**
     * 按主键查询考试试卷。
     *
     * @param id 考试试卷主键
     * @return 考试试卷信息
     */
    @GetMapping("/{id}")
    public ApiResult<KbExamPaperResponse> get(@PathVariable Long id) {
        return ApiTemplate.execute(id, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                KbExamPaperParamChecker.checkId(id);
            }

            @Override
            public KbExamPaperResponse execute(Long param) {
                KbExamPaper kbExamPaper = kbExamPaperManager.getKbExamPaper(id);
                AssertUtil.throwErrWhenNull(kbExamPaper, ErrorCodeEnum.PARAM_INVALID, "考试试卷不存在");
                return KbExamPaperAssembler.toResponse(kbExamPaper);
            }
        });
    }

    /**
     * 分页查询考试试卷。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public ApiResult<PageResult<KbExamPaperResponse>> page(KbExamPaperQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(KbExamPaperQueryRequest param) {
                KbExamPaperParamChecker.checkKbExamPaperQueryRequest(param);
            }

            @Override
            public PageResult<KbExamPaperResponse> execute(KbExamPaperQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new KbExamPaperQueryRequest());
                PageResult<KbExamPaper> page = kbExamPaperManager.pageKbExamPapers(KbExamPaperAssembler.toQueryParam(param));
                return ConvertUtil.mapPage(page, KbExamPaperAssembler::toResponse);
            }
        });
    }

    /**
     * 更新考试试卷（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id 考试试卷主键
     * @param request 更新内容
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody KbExamPaperUpdateRequest request) {
        return ApiTemplate.executeWithoutResult(request, new ApiTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(KbExamPaperUpdateRequest param) {
                KbExamPaperParamChecker.checkId(id);
                KbExamPaperParamChecker.checkKbExamPaperUpdateRequest(param);
            }

            @Override
            public void execute(KbExamPaperUpdateRequest param) {
                kbExamPaperManager.updateKbExamPaper(KbExamPaperAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除考试试卷。
     *
     * @param id 考试试卷主键
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        return ApiTemplate.executeWithoutResult(id, new ApiTemplate.CallbackWithoutResult<Long>() {

            @Override
            public void beforeService(Long id) {
                KbExamPaperParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                kbExamPaperManager.deleteKbExamPaper(id);
            }
        });
    }
}
