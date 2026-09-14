package com.jakt.aiplatform.web.controller;

import com.jakt.aiplatform.biz.service.KbQuestionSearchManager;
import com.jakt.aiplatform.biz.service.KbQuestionSearchView;
import com.jakt.aiplatform.web.param.KbQuestionSearchRequest;
import com.jakt.aiplatform.web.result.ApiResult;
import com.jakt.aiplatform.web.result.KbQuestionSearchResponse;
import com.jakt.aiplatform.web.template.ApiTemplate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

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
                KbQuestionSearchView view = kbQuestionSearchManager.search(param.getKeyword(), pageNum, pageSize);
                KbQuestionSearchResponse response = new KbQuestionSearchResponse();
                response.setTotal(view.getTotal());
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
}
