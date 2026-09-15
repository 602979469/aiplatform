package com.jakt.aiplatform.web.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import cn.dev33.satoken.stp.StpUtil;
import com.jakt.aiplatform.biz.service.KbExamManager;
import com.jakt.aiplatform.biz.service.KbExamPaperView;
import com.jakt.aiplatform.biz.service.KbExamResultView;
import com.jakt.aiplatform.biz.service.KbExamRuleParam;
import com.jakt.aiplatform.biz.service.KbExamStartParam;
import com.jakt.aiplatform.biz.service.KbExamWrongView;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.model.domain.KbExamPaper;
import com.jakt.aiplatform.web.param.KbExamAnswerRequest;
import com.jakt.aiplatform.web.param.KbExamStartRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.template.ApiTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.ArrayList;
import java.util.List;

/**
 * 考题系统接口：组卷、答题、交卷判分、成绩、历史记录、错题集。
 *
 * <p>答题人统一取当前登录用户（Sa-Token），请求体不接受 userId，避免越权。
 */
@RestController
@RequestMapping("/api/kb/exam")
public class KbExamController {

    private final KbExamManager kbExamManager;

    public KbExamController(KbExamManager kbExamManager) {
        this.kbExamManager = kbExamManager;
    }

    /** 开始考试：选模板或快速创建，返回试卷（不含答案）。 */
    @PostMapping("/start")
    @SaCheckPermission("kb:exam:start")
    public ApiResult<KbExamPaperView> start(@RequestBody KbExamStartRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<KbExamStartRequest, KbExamPaperView>() {

            @Override
            public KbExamPaperView execute(KbExamStartRequest param) {
                KbExamStartParam startParam = new KbExamStartParam();
                startParam.setUserId(StpUtil.getLoginIdAsLong());
                startParam.setTemplateId(param.getTemplateId());
                startParam.setTitle(param.getTitle());
                startParam.setMode(param.getMode());
                startParam.setQuestionCount(param.getQuestionCount());
                startParam.setPerQuestionSeconds(param.getPerQuestionSeconds());
                startParam.setExcludeMastered(param.getExcludeMastered());
                startParam.setObjectiveOnly(param.getObjectiveOnly());
                List<KbExamRuleParam> rules = new ArrayList<>();
                if (param.getRules() != null) {
                    for (KbExamStartRequest.Rule rule : param.getRules()) {
                        KbExamRuleParam item = new KbExamRuleParam();
                        item.setCategory(rule.getCategory());
                        item.setSubtopic(rule.getSubtopic());
                        item.setQuestionType(rule.getQuestionType());
                        item.setDifficulty(rule.getDifficulty());
                        item.setCount(rule.getCount());
                        rules.add(item);
                    }
                }
                startParam.setRules(rules);
                return kbExamManager.start(startParam);
            }
        });
    }

    /** 续考：取回试卷与已作答内容。 */
    @GetMapping("/paper/{paperId}")
    @SaCheckPermission("kb:exam:start")
    public ApiResult<KbExamPaperView> paper(@PathVariable Long paperId) {
        return ApiTemplate.execute(paperId, new ApiTemplate.Callback<Long, KbExamPaperView>() {

            @Override
            public KbExamPaperView execute(Long param) {
                return kbExamManager.getPaper(param, StpUtil.getLoginIdAsLong());
            }
        });
    }

    /** 提交单题作答（幂等）。 */
    @PutMapping("/paper/{paperId}/answer")
    @SaCheckPermission("kb:exam:start")
    public ApiResult<Void> answer(@PathVariable Long paperId, @RequestBody KbExamAnswerRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<KbExamAnswerRequest, Void>() {

            @Override
            public Void execute(KbExamAnswerRequest param) {
                kbExamManager.answer(paperId, StpUtil.getLoginIdAsLong(), param.getSeq(),
                        param.getUserAnswer(), param.getCostSeconds());
                return null;
            }
        });
    }

    /** 交卷判分（超时自动交卷也走这里）。 */
    @PostMapping("/paper/{paperId}/submit")
    @SaCheckPermission("kb:exam:start")
    public ApiResult<KbExamResultView> submit(@PathVariable Long paperId) {
        return ApiTemplate.execute(paperId, new ApiTemplate.Callback<Long, KbExamResultView>() {

            @Override
            public KbExamResultView execute(Long param) {
                return kbExamManager.submit(param, StpUtil.getLoginIdAsLong());
            }
        });
    }

    /** 成绩详情（含错题解析）。 */
    @GetMapping("/paper/{paperId}/result")
    @SaCheckPermission("kb:exam:start")
    public ApiResult<KbExamResultView> result(@PathVariable Long paperId) {
        return ApiTemplate.execute(paperId, new ApiTemplate.Callback<Long, KbExamResultView>() {

            @Override
            public KbExamResultView execute(Long param) {
                return kbExamManager.result(param, StpUtil.getLoginIdAsLong());
            }
        });
    }

    /** 考试记录。 */
    @GetMapping("/history")
    @SaCheckPermission("kb:exam:start")
    public ApiResult<PageResult<KbExamPaper>> history(@RequestParam(required = false) Integer pageNum,
                                                      @RequestParam(required = false) Integer pageSize) {
        return ApiTemplate.execute(pageNum, new ApiTemplate.Callback<Integer, PageResult<KbExamPaper>>() {

            @Override
            public PageResult<KbExamPaper> execute(Integer param) {
                return kbExamManager.history(StpUtil.getLoginIdAsLong(), param, pageSize);
            }
        });
    }

    /** 错题集。 */
    @GetMapping("/wrong")
    @SaCheckPermission("kb:exam:start")
    public ApiResult<PageResult<KbExamWrongView>> wrong(@RequestParam(required = false) String category,
                                                        @RequestParam(required = false) Integer pageNum,
                                                        @RequestParam(required = false) Integer pageSize) {
        return ApiTemplate.execute(category, new ApiTemplate.Callback<String, PageResult<KbExamWrongView>>() {

            @Override
            public PageResult<KbExamWrongView> execute(String param) {
                return kbExamManager.wrongBook(StpUtil.getLoginIdAsLong(), param, pageNum, pageSize);
            }
        });
    }

    /** 标记已掌握（移出错题集）。 */
    @PostMapping("/wrong/{questionId}/mastered")
    @SaCheckPermission("kb:exam:start")
    public ApiResult<Void> markMastered(@PathVariable Long questionId) {
        return ApiTemplate.execute(questionId, new ApiTemplate.Callback<Long, Void>() {

            @Override
            public Void execute(Long param) {
                kbExamManager.markMastered(StpUtil.getLoginIdAsLong(), param);
                return null;
            }
        });
    }
}
