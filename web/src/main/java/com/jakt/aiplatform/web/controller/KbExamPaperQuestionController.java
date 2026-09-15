package com.jakt.aiplatform.web.controller;

import cn.hutool.core.util.ObjectUtil;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.template.ApiTemplate;
import com.jakt.aiplatform.biz.service.KbExamPaperQuestionManager;
import com.jakt.aiplatform.core.model.domain.KbExamPaperQuestion;
import com.jakt.aiplatform.web.assembler.KbExamPaperQuestionAssembler;
import com.jakt.aiplatform.web.checker.KbExamPaperQuestionParamChecker;
import com.jakt.aiplatform.web.param.KbExamPaperQuestionCreateRequest;
import com.jakt.aiplatform.web.param.KbExamPaperQuestionQueryRequest;
import com.jakt.aiplatform.web.param.KbExamPaperQuestionUpdateRequest;
import com.jakt.aiplatform.web.result.KbExamPaperQuestionResponse;
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
 * 试卷题目快照与作答管理接口。Controller 只做参数校验、DTO 转换与结果包装，不含业务规则；
 * 参数校验、异常封装、请求日志与 Result 组装统一交给 ApiTemplate。
 */
@RestController
@RequestMapping("/api/v1/kbExamPaperQuestions")
@Tag(name = "试卷题目快照与作答管理")
public class KbExamPaperQuestionController {

    /** 试卷题目快照与作答 Manager。 */
    private final KbExamPaperQuestionManager kbExamPaperQuestionManager;

    public KbExamPaperQuestionController(KbExamPaperQuestionManager kbExamPaperQuestionManager) {
        this.kbExamPaperQuestionManager = kbExamPaperQuestionManager;
    }

    /**
     * 创建试卷题目快照与作答。
     *
     * @param request 创建试卷题目快照与作答请求体
     * @return 创建成功后的试卷题目快照与作答信息
     */
    @PostMapping
    public ApiResult<KbExamPaperQuestionResponse> create(@RequestBody KbExamPaperQuestionCreateRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(KbExamPaperQuestionCreateRequest param) {
                KbExamPaperQuestionParamChecker.checkKbExamPaperQuestionCreateRequest(param);
            }

            @Override
            public KbExamPaperQuestionResponse execute(KbExamPaperQuestionCreateRequest param) {
                KbExamPaperQuestion kbExamPaperQuestion = kbExamPaperQuestionManager.createKbExamPaperQuestion(KbExamPaperQuestionAssembler.toModel(param));
                return KbExamPaperQuestionAssembler.toResponse(kbExamPaperQuestion);
            }
        });
    }

    /**
     * 按主键查询试卷题目快照与作答。
     *
     * @param id 试卷题目快照与作答主键
     * @return 试卷题目快照与作答信息
     */
    @GetMapping("/{id}")
    public ApiResult<KbExamPaperQuestionResponse> get(@PathVariable Long id) {
        return ApiTemplate.execute(id, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(Long param) {
                KbExamPaperQuestionParamChecker.checkId(id);
            }

            @Override
            public KbExamPaperQuestionResponse execute(Long param) {
                KbExamPaperQuestion kbExamPaperQuestion = kbExamPaperQuestionManager.getKbExamPaperQuestion(id);
                AssertUtil.throwErrWhenNull(kbExamPaperQuestion, ErrorCodeEnum.PARAM_INVALID, "试卷题目快照与作答不存在");
                return KbExamPaperQuestionAssembler.toResponse(kbExamPaperQuestion);
            }
        });
    }

    /**
     * 分页查询试卷题目快照与作答。
     *
     * @param request 查询条件（含分页参数与时间区间）
     * @return 分页结果
     */
    @GetMapping("/page")
    public ApiResult<PageResult<KbExamPaperQuestionResponse>> page(KbExamPaperQuestionQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<>() {

            @Override
            public void beforeService(KbExamPaperQuestionQueryRequest param) {
                KbExamPaperQuestionParamChecker.checkKbExamPaperQuestionQueryRequest(param);
            }

            @Override
            public PageResult<KbExamPaperQuestionResponse> execute(KbExamPaperQuestionQueryRequest param) {
                param = ObjectUtil.defaultIfNull(param, new KbExamPaperQuestionQueryRequest());
                PageResult<KbExamPaperQuestion> page = kbExamPaperQuestionManager.pageKbExamPaperQuestions(KbExamPaperQuestionAssembler.toQueryParam(param));
                return ConvertUtil.mapPage(page, KbExamPaperQuestionAssembler::toResponse);
            }
        });
    }

    /**
     * 更新试卷题目快照与作答（全量）。
     * 注意：PUT 为全量覆盖，未传字段会被置 NULL；部分更新请走 updateByCondition（Manager/DomainService）。
     *
     * @param id 试卷题目快照与作答主键
     * @param request 更新内容
     * @return 更新结果
     */
    @PutMapping("/{id}")
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody KbExamPaperQuestionUpdateRequest request) {
        return ApiTemplate.executeWithoutResult(request, new ApiTemplate.CallbackWithoutResult<>() {

            @Override
            public void beforeService(KbExamPaperQuestionUpdateRequest param) {
                KbExamPaperQuestionParamChecker.checkId(id);
                KbExamPaperQuestionParamChecker.checkKbExamPaperQuestionUpdateRequest(param);
            }

            @Override
            public void execute(KbExamPaperQuestionUpdateRequest param) {
                kbExamPaperQuestionManager.updateKbExamPaperQuestion(KbExamPaperQuestionAssembler.toModel(param, id));
            }
        });
    }

    /**
     * 删除试卷题目快照与作答。
     *
     * @param id 试卷题目快照与作答主键
     * @return 删除结果
     */
    @DeleteMapping("/{id}")
    public ApiResult<Void> delete(@PathVariable Long id) {
        return ApiTemplate.executeWithoutResult(id, new ApiTemplate.CallbackWithoutResult<Long>() {

            @Override
            public void beforeService(Long id) {
                KbExamPaperQuestionParamChecker.checkId(id);
            }

            @Override
            public void execute(Long id) {
                kbExamPaperQuestionManager.deleteKbExamPaperQuestion(id);
            }
        });
    }
}
