package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.collection.CollUtil;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.core.model.domain.KbQuestion;
import com.jakt.aiplatform.core.model.dto.KbQuestionDetailView;
import com.jakt.aiplatform.core.model.dto.KbQuestionMetaView;
import com.jakt.aiplatform.core.model.dto.KbQuestionSearchView;
import com.jakt.aiplatform.core.model.param.KbQuestionQueryParam;
import com.jakt.aiplatform.core.model.param.KbQuestionSearchQuery;
import com.jakt.aiplatform.web.param.KbQuestionQueryRequest;
import com.jakt.aiplatform.web.param.KbQuestionSaveRequest;
import com.jakt.aiplatform.web.param.KbQuestionSearchRequest;
import com.jakt.aiplatform.web.result.KbQuestionDetailResponse;
import com.jakt.aiplatform.web.result.KbQuestionItemResponse;
import com.jakt.aiplatform.web.result.KbQuestionMetaResponse;
import com.jakt.aiplatform.web.result.KbQuestionSearchResponse;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 题库组装器：请求 ↔ 领域参数 ↔ 响应。
 */
public final class KbQuestionAdminAssembler {

    private KbQuestionAdminAssembler() {
    }

    /**
     * 查询请求 → 查询参数。
     *
     * @param request 查询请求
     * @return 查询参数
     */
    public static KbQuestionQueryParam toQueryParam(KbQuestionQueryRequest request) {
        KbQuestionQueryParam param = new KbQuestionQueryParam();
        if (request == null) {
            return param;
        }
        param.setPageNum(request.getPageNum() == null ? 1 : request.getPageNum());
        param.setPageSize(request.getPageSize() == null ? 10 : request.getPageSize());
        param.setKeyword(request.getKeyword());
        param.setCategory(request.getCategory());
        param.setSubtopic(request.getSubtopic());
        param.setQuestionType(request.getQuestionType());
        param.setDifficulty(request.getDifficulty());
        return param;
    }

    /**
     * 新增/修改请求 → 题目领域模型。
     *
     * @param request 请求
     * @return 题库题目领域模型
     */
    public static KbQuestion toModel(KbQuestionSaveRequest request) {
        KbQuestion target = new KbQuestion();
        if (request == null) {
            return target;
        }
        target.setId(request.getId());
        target.setQuestionType(request.getQuestionType());
        target.setCategory(request.getCategory());
        target.setSubtopic(StrUtil.nullToEmpty(request.getSubtopic()));
        target.setTitle(request.getTitle());
        target.setContent(request.getContent());
        target.setOptions(request.getOptions());
        target.setAnswer(request.getAnswer());
        target.setExplanation(request.getExplanation());
        target.setDifficulty(request.getDifficulty());
        target.setTags(StrUtil.nullToEmpty(request.getTags()));
        target.setSourcePath(StrUtil.nullToEmpty(request.getSourcePath()));
        return target;
    }

    /**
     * 题目领域模型 → 列表项响应。
     *
     * @param row 题目领域模型
     * @return 列表项响应
     */
    public static KbQuestionItemResponse toItem(KbQuestion row) {
        if (row == null) {
            return null;
        }
        KbQuestionItemResponse target = new KbQuestionItemResponse();
        target.setId(row.getId());
        target.setQuestionType(row.getQuestionType());
        target.setCategory(row.getCategory());
        target.setSubtopic(row.getSubtopic());
        target.setTitle(row.getTitle());
        target.setDifficulty(row.getDifficulty());
        target.setTags(row.getTags());
        target.setUpdateTime(row.getUpdateTime());
        return target;
    }

    /**
     * 知识点元数据 → 响应。
     *
     * @param view 知识点元数据
     * @return 元数据响应
     */
    public static KbQuestionMetaResponse toMetaResponse(KbQuestionMetaView view) {
        KbQuestionMetaResponse response = new KbQuestionMetaResponse();
        if (view == null) {
            return response;
        }
        List<KbQuestionMetaResponse.CategoryNode> categories = new ArrayList<>();
        if (view.getCategories() != null) {
            for (KbQuestionMetaView.CategoryNode source : view.getCategories()) {
                KbQuestionMetaResponse.CategoryNode category = new KbQuestionMetaResponse.CategoryNode();
                category.setCategory(source.getCategory());
                category.setTotal(source.getTotal());
                List<KbQuestionMetaResponse.SubtopicNode> subtopics = new ArrayList<>();
                if (source.getSubtopics() != null) {
                    for (KbQuestionMetaView.SubtopicNode item : source.getSubtopics()) {
                        KbQuestionMetaResponse.SubtopicNode subtopic = new KbQuestionMetaResponse.SubtopicNode();
                        subtopic.setSubtopic(item.getSubtopic());
                        subtopic.setTotal(item.getTotal());
                        subtopics.add(subtopic);
                    }
                }
                category.setSubtopics(subtopics);
                categories.add(category);
            }
        }
        response.setCategories(categories);
        return response;
    }

    /**
     * 检索请求 → 检索条件。
     *
     * @param request 检索请求
     * @return 检索条件
     */
    public static KbQuestionSearchQuery toSearchQuery(KbQuestionSearchRequest request) {
        KbQuestionSearchQuery query = new KbQuestionSearchQuery();
        if (request == null) {
            return query;
        }
        query.setKeyword(request.getKeyword());
        query.setQuestionTypes(split(request.getQuestionType()));
        query.setCategories(split(request.getCategory()));
        query.setSubtopics(split(request.getSubtopic()));
        query.setDifficulties(split(request.getDifficulty()));
        query.setPageNum(request.getPageNum() == null ? 1 : request.getPageNum());
        query.setPageSize(request.getPageSize() == null ? 10 : request.getPageSize());
        return query;
    }

    /**
     * 检索结果 → 响应。
     *
     * @param view 检索结果
     * @return 检索响应
     */
    public static KbQuestionSearchResponse toSearchResponse(KbQuestionSearchView view) {
        KbQuestionSearchResponse response = new KbQuestionSearchResponse();
        if (view == null) {
            return response;
        }
        response.setTotal(view.getTotal());
        response.setFacets(toBucketResponse(view.getFacets()));
        List<KbQuestionSearchResponse.Item> items = new ArrayList<>();
        if (view.getList() != null) {
            for (KbQuestionSearchView.Item source : view.getList()) {
                KbQuestionSearchResponse.Item item = new KbQuestionSearchResponse.Item();
                item.setId(source.getId());
                item.setTitle(source.getTitle());
                item.setSnippet(source.getSnippet());
                item.setCategory(source.getCategory());
                item.setTags(source.getTags());
                item.setDifficulty(source.getDifficulty());
                item.setDocType(source.getDocType());
                items.add(item);
            }
        }
        response.setList(items);
        return response;
    }

    /**
     * 聚合结果 → 聚合响应。
     *
     * @param facets 聚合结果；为空返回空 Map
     * @return 聚合响应
     */
    public static Map<String, List<KbQuestionSearchResponse.Bucket>> toBucketResponse(
            Map<String, List<KbQuestionSearchView.Bucket>> facets) {
        Map<String, List<KbQuestionSearchResponse.Bucket>> result = new LinkedHashMap<>();
        if (facets == null) {
            return result;
        }
        facets.forEach((name, buckets) -> {
            List<KbQuestionSearchResponse.Bucket> target = new ArrayList<>();
            if (buckets != null) {
                for (KbQuestionSearchView.Bucket bucket : buckets) {
                    KbQuestionSearchResponse.Bucket item = new KbQuestionSearchResponse.Bucket();
                    item.setKey(bucket.getKey());
                    item.setCount(bucket.getCount());
                    target.add(item);
                }
            }
            result.put(name, target);
        });
        return result;
    }

    /**
     * 题目详情视图 → 详情响应。
     *
     * @param view 题目详情视图
     * @return 详情响应
     */
    public static KbQuestionDetailResponse toDetailResponse(KbQuestionDetailView view) {
        KbQuestionDetailResponse response = new KbQuestionDetailResponse();
        if (view == null) {
            return response;
        }
        response.setId(view.getId());
        response.setDocType(view.getDocType());
        response.setCategory(view.getCategory());
        response.setSubtopic(view.getSubtopic());
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

    /**
     * 逗号分隔参数转列表。
     *
     * @param value 参数值
     * @return 列表（空则 null）
     */
    private static List<String> split(String value) {
        if (StrUtil.isBlank(value)) {
            return null;
        }
        List<String> list = new ArrayList<>();
        for (String item : value.split(",")) {
            if (StrUtil.isNotBlank(item)) {
                list.add(item.trim());
            }
        }
        return CollUtil.isEmpty(list) ? null : list;
    }
}
