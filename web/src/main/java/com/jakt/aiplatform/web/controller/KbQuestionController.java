package com.jakt.aiplatform.web.controller;

import cn.dev33.satoken.annotation.SaCheckPermission;
import com.jakt.aiplatform.biz.service.KbQuestionAdminManager;
import com.jakt.aiplatform.biz.service.KbQuestionSearchManager;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.KbQuestion;
import com.jakt.aiplatform.core.model.dto.KbQuestionDetailView;
import com.jakt.aiplatform.core.model.dto.KbQuestionSearchView;
import com.jakt.aiplatform.web.assembler.KbQuestionAdminAssembler;
import com.jakt.aiplatform.web.checker.KbQuestionAdminParamChecker;
import com.jakt.aiplatform.web.param.KbQuestionQueryRequest;
import com.jakt.aiplatform.web.param.KbQuestionSaveRequest;
import com.jakt.aiplatform.web.param.KbQuestionSearchRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.KbQuestionDetailResponse;
import com.jakt.aiplatform.web.result.KbQuestionItemResponse;
import com.jakt.aiplatform.web.result.KbQuestionMetaResponse;
import com.jakt.aiplatform.web.result.KbQuestionSearchResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 题库接口：管理走 MySQL，检索走 Elasticsearch。
 */
@RestController
@RequestMapping("/api/kb/question")
public class KbQuestionController {

    /** 题库检索 Manager。 */
    private final KbQuestionSearchManager kbQuestionSearchManager;

    /** 题库管理 Manager。 */
    private final KbQuestionAdminManager kbQuestionAdminManager;

    public KbQuestionController(KbQuestionSearchManager kbQuestionSearchManager,
                                KbQuestionAdminManager kbQuestionAdminManager) {
        this.kbQuestionSearchManager = kbQuestionSearchManager;
        this.kbQuestionAdminManager = kbQuestionAdminManager;
    }

    /**
     * 题库管理：分页查询（走 MySQL，关键词 + 知识点/题型/难度筛选）。
     *
     * @param request 查询请求
     * @return 分页结果
     */
    @GetMapping("/page")
    @SaCheckPermission("kb:question:list")
    public ApiResult<PageResult<KbQuestionItemResponse>> page(KbQuestionQueryRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<KbQuestionQueryRequest,
                PageResult<KbQuestionItemResponse>>() {
            @Override
            public void beforeService(KbQuestionQueryRequest param) {
                KbQuestionAdminParamChecker.checkQuery(param);
            }

            @Override
            public PageResult<KbQuestionItemResponse> execute(KbQuestionQueryRequest param) {
                PageResult<KbQuestion> page = kbQuestionAdminManager.page(KbQuestionAdminAssembler.toQueryParam(param));
                return ConvertUtil.mapPage(page, KbQuestionAdminAssembler::toItem);
            }
        });
    }

    /**
     * 题库管理：知识点元数据（分类 → 子主题 + 题量）。
     *
     * @return 元数据响应
     */
    @GetMapping("/meta")
    @SaCheckPermission("kb:question:list")
    public ApiResult<KbQuestionMetaResponse> meta() {
        return ApiTemplate.execute(new Object(), new ApiTemplate.Callback<Object, KbQuestionMetaResponse>() {
            @Override
            public KbQuestionMetaResponse execute(Object param) {
                return KbQuestionAdminAssembler.toMetaResponse(kbQuestionAdminManager.meta());
            }
        });
    }

    /**
     * 题库管理：新增题目。
     *
     * @param request 保存请求
     * @return 新增后的主键
     */
    @PostMapping
    @SaCheckPermission("kb:question:add")
    public ApiResult<Long> create(@RequestBody KbQuestionSaveRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<KbQuestionSaveRequest, Long>() {
            @Override
            public void beforeService(KbQuestionSaveRequest param) {
                KbQuestionAdminParamChecker.checkSave(param);
            }

            @Override
            public Long execute(KbQuestionSaveRequest param) {
                return kbQuestionAdminManager.create(KbQuestionAdminAssembler.toModel(param));
            }
        });
    }

    /**
     * 题库管理：修改题目。
     *
     * @param id 题目ID
     * @param request 保存请求
     * @return 空响应
     */
    @PutMapping("/{id}")
    @SaCheckPermission("kb:question:edit")
    public ApiResult<Void> update(@PathVariable Long id, @RequestBody KbQuestionSaveRequest request) {
        request.setId(id);
        return ApiTemplate.executeWithoutResult(request, new ApiTemplate.CallbackWithoutResult<KbQuestionSaveRequest>() {
            @Override
            public void beforeService(KbQuestionSaveRequest param) {
                KbQuestionAdminParamChecker.checkId(param.getId());
                KbQuestionAdminParamChecker.checkSave(param);
            }

            @Override
            public void execute(KbQuestionSaveRequest param) {
                kbQuestionAdminManager.update(KbQuestionAdminAssembler.toModel(param));
            }
        });
    }

    /**
     * 题库管理：物理删除题目。
     *
     * @param id 题目ID
     * @return 空响应
     */
    @DeleteMapping("/{id}")
    @SaCheckPermission("kb:question:remove")
    public ApiResult<Void> delete(@PathVariable Long id) {
        return ApiTemplate.executeWithoutResult(id, new ApiTemplate.CallbackWithoutResult<Long>() {
            @Override
            public void beforeService(Long param) {
                KbQuestionAdminParamChecker.checkId(param);
            }

            @Override
            public void execute(Long param) {
                kbQuestionAdminManager.delete(param);
            }
        });
    }

    /**
     * 题库搜索。
     *
     * @param request 搜索请求
     * @return 搜索结果
     */
    @GetMapping("/search")
    public ApiResult<KbQuestionSearchResponse> search(KbQuestionSearchRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<KbQuestionSearchRequest,
                KbQuestionSearchResponse>() {
            @Override
            public KbQuestionSearchResponse execute(KbQuestionSearchRequest param) {
                KbQuestionSearchView view = kbQuestionSearchManager.search(KbQuestionAdminAssembler.toSearchQuery(param));
                return KbQuestionAdminAssembler.toSearchResponse(view);
            }
        });
    }

    /**
     * 题库筛选项（进页面即可用，无需先查询）。
     *
     * @return 筛选项
     */
    @GetMapping("/facets")
    public ApiResult<Map<String, List<KbQuestionSearchResponse.Bucket>>> facets() {
        return ApiTemplate.execute("facets", new ApiTemplate.Callback<String,
                Map<String, List<KbQuestionSearchResponse.Bucket>>>() {
            @Override
            public Map<String, List<KbQuestionSearchResponse.Bucket>> execute(String param) {
                return KbQuestionAdminAssembler.toBucketResponse(kbQuestionSearchManager.facets());
            }
        });
    }

    /**
     * 题目详情（含完整解答，列表接口不返回大字段）。
     *
     * @param id 题目ID
     * @return 详情响应
     */
    @GetMapping("/{id}")
    public ApiResult<KbQuestionDetailResponse> detail(@PathVariable Long id) {
        return ApiTemplate.execute(id, new ApiTemplate.Callback<Long, KbQuestionDetailResponse>() {
            @Override
            public KbQuestionDetailResponse execute(Long param) {
                KbQuestionDetailView view = kbQuestionSearchManager.detail(param);
                return KbQuestionAdminAssembler.toDetailResponse(view);
            }
        });
    }
}
