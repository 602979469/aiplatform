package com.jakt.aiplatform.web.assembler;

import com.jakt.aiplatform.biz.service.KbQuestionMetaView;
import com.jakt.aiplatform.common.dal.dataobject.KbQuestionDO;
import com.jakt.aiplatform.core.model.param.KbQuestionQueryParam;
import com.jakt.aiplatform.web.param.KbQuestionQueryRequest;
import com.jakt.aiplatform.web.param.KbQuestionSaveRequest;
import com.jakt.aiplatform.web.result.KbQuestionItemResponse;
import com.jakt.aiplatform.web.result.KbQuestionMetaResponse;

import java.util.ArrayList;
import java.util.List;

/**
 * 题库管理组装器：请求 ↔ 领域参数 ↔ 响应。
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
     * 新增/修改请求 → 题目数据对象。
     *
     * @param request 请求
     * @return 数据对象
     */
    public static KbQuestionDO toDO(KbQuestionSaveRequest request) {
        KbQuestionDO target = new KbQuestionDO();
        if (request == null) {
            return target;
        }
        target.setId(request.getId());
        target.setQuestionType(request.getQuestionType());
        target.setCategory(request.getCategory());
        // subtopic / tags / source_path 在库里是 NOT NULL DEFAULT ''，显式传 null 会违反约束
        target.setSubtopic(request.getSubtopic() == null ? "" : request.getSubtopic());
        target.setTitle(request.getTitle());
        target.setContent(request.getContent());
        target.setOptions(request.getOptions());
        target.setAnswer(request.getAnswer());
        target.setExplanation(request.getExplanation());
        target.setDifficulty(request.getDifficulty());
        target.setTags(request.getTags() == null ? "" : request.getTags());
        target.setSourcePath(request.getSourcePath() == null ? "" : request.getSourcePath());
        return target;
    }

    /**
     * 题目 → 列表项响应。
     *
     * @param row 题目
     * @return 列表项；入参为空返回 null
     */
    public static KbQuestionItemResponse toItem(KbQuestionDO row) {
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
     * @param view 元数据
     * @return 响应
     */
    public static KbQuestionMetaResponse toMetaResponse(KbQuestionMetaView view) {
        KbQuestionMetaResponse response = new KbQuestionMetaResponse();
        List<KbQuestionMetaResponse.CategoryNode> categories = new ArrayList<>();
        if (view != null && view.getCategories() != null) {
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
}
