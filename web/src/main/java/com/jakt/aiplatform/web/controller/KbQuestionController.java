package com.jakt.aiplatform.web.controller;

import com.jakt.aiplatform.biz.service.KbQuestionSearchManager;
import com.jakt.aiplatform.biz.service.KbQuestionSearchQuery;
import com.jakt.aiplatform.biz.service.KbQuestionSearchView;
import com.jakt.aiplatform.biz.service.KbQuestionDetailView;
import com.jakt.aiplatform.web.param.KbQuestionSearchRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.KbQuestionDetailResponse;
import com.jakt.aiplatform.web.result.KbQuestionSearchResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.Map;

/**
 * 题库接口：检索走 Elasticsearch。
 */
@RestController
@RequestMapping("/api/kb/question")
public class KbQuestionController {

    private final KbQuestionSearchManager kbQuestionSearchManager;

    public KbQuestionController(KbQuestionSearchManager kbQuestionSearchManager) {
        this.kbQuestionSearchManager = kbQuestionSearchManager;
    }

    /** 题库搜索。 */
    @GetMapping("/search")
    public ApiResult<KbQuestionSearchResponse> search(KbQuestionSearchRequest request) {
        return ApiTemplate.execute(request, new ApiTemplate.Callback<KbQuestionSearchRequest,
                KbQuestionSearchResponse>() {

            @Override
            public KbQuestionSearchResponse execute(KbQuestionSearchRequest param) {
                int pageNum = param.getPageNum() == null ? 1 : param.getPageNum();
                int pageSize = param.getPageSize() == null ? 10 : param.getPageSize();
                KbQuestionSearchQuery query = new KbQuestionSearchQuery();
                query.setKeyword(param.getKeyword());
                query.setQuestionTypes(split(param.getQuestionType()));
                query.setCategories(split(param.getCategory()));
                query.setSubtopics(split(param.getSubtopic()));
                query.setDifficulties(split(param.getDifficulty()));
                query.setPageNum(pageNum);
                query.setPageSize(pageSize);
                KbQuestionSearchView view = kbQuestionSearchManager.search(query);
                KbQuestionSearchResponse response = new KbQuestionSearchResponse();
                response.setTotal(view.getTotal());
                if (view.getFacets() != null) {
                    Map<String, List<KbQuestionSearchResponse.Bucket>> facets = new LinkedHashMap<>();
                    view.getFacets().forEach((name, buckets) -> {
                        List<KbQuestionSearchResponse.Bucket> target = new ArrayList<>();
                        if (buckets != null) {
                            for (KbQuestionSearchView.Bucket b : buckets) {
                                KbQuestionSearchResponse.Bucket bucket = new KbQuestionSearchResponse.Bucket();
                                bucket.setKey(b.getKey());
                                bucket.setCount(b.getCount());
                                target.add(bucket);
                            }
                        }
                        facets.put(name, target);
                    });
                    response.setFacets(facets);
                }
                List<KbQuestionSearchResponse.Item> items = view.getList().stream().map(item -> {
                    KbQuestionSearchResponse.Item target = new KbQuestionSearchResponse.Item();
                    target.setId(item.getId());
                    target.setTitle(item.getTitle());
                    target.setSnippet(item.getSnippet());
                    target.setCategory(item.getCategory());
                    target.setTags(item.getTags());
                    target.setDifficulty(item.getDifficulty());
                    target.setDocType(item.getDocType());
                    return target;
                }).toList();
                response.setList(items);
                return response;
            }
        });
    }

    /**
     * 逗号分隔参数转列表。
     *
     * @param value 参数值
     * @return 列表（空则 null）
     */
    private List<String> split(String value) {
        if (value == null || value.isBlank()) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (String item : value.split(",")) {
            if (!item.isBlank()) {
                list.add(item.trim());
            }
        }
        return list.isEmpty() ? null : list;
    }

    /** 题目详情（完整解答，支持 Markdown）。 */
    @GetMapping("/{id}")
    public ApiResult<KbQuestionDetailResponse> detail(@PathVariable Long id) {
        return ApiTemplate.execute(id, new ApiTemplate.Callback<Long, KbQuestionDetailResponse>() {

            @Override
            public KbQuestionDetailResponse execute(Long param) {
                KbQuestionDetailView view = kbQuestionSearchManager.detail(param);
                KbQuestionDetailResponse response = new KbQuestionDetailResponse();
                response.setId(view.getId());
                response.setDocType(view.getDocType());
                response.setCategory(view.getCategory());
                response.setTitle(view.getTitle());
                response.setContent(view.getContent());
                response.setOptions(view.getOptions());
                response.setAnswer(view.getAnswer());
                response.setExplanation(view.getExplanation());
                response.setDifficulty(view.getDifficulty());
                response.setTags(view.getTags());
                response.setSourcePath(view.getSourcePath());
                return response;
            }
        });
    }
}
