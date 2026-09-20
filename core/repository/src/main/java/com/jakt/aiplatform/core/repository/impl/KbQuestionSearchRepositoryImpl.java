package com.jakt.aiplatform.core.repository.impl;

import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.util.ObjectUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.jakt.aiplatform.common.dal.es.EsProperties;
import com.jakt.aiplatform.common.dal.es.EsSearchClient;
import com.jakt.aiplatform.core.model.dto.KbQuestionSearchView;
import com.jakt.aiplatform.core.model.param.KbQuestionSearchQuery;
import com.jakt.aiplatform.core.repository.KbQuestionSearchRepository;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 题库检索仓储：Elasticsearch（索引 java-kb）检索与聚合。单表操作不引入事务。
 */
@Repository
public class KbQuestionSearchRepositoryImpl implements KbQuestionSearchRepository {

    /** 摘要截断长度。 */
    private static final int SNIPPET_LENGTH = 200;

    /** 文档类型默认值（ES 文档未显式写入 doc_type 时的兜底）。 */
    private static final String DEFAULT_DOC_TYPE = "qa";

    /** ES 默认 max_result_window：from + size 不能超过该值，超出会 400。 */
    private static final int MAX_RESULT_WINDOW = 10000;

    /** 聚合字段名。 */
    private static final String[] FACET_FIELDS = {"question_type", "category", "subtopic", "difficulty"};

    /** ES 客户端。 */
    private final EsSearchClient esSearchClient;

    /** ES 配置。 */
    private final EsProperties esProperties;

    public KbQuestionSearchRepositoryImpl(EsSearchClient esSearchClient, EsProperties esProperties) {
        this.esSearchClient = esSearchClient;
        this.esProperties = esProperties;
    }

    @Override
    public KbQuestionSearchView search(KbQuestionSearchQuery query) {
        String keyword = query.getKeyword();
        int pageSize = Math.max(1, ObjectUtil.defaultIfNull(query.getPageSize(), 10));
        // 深分页保护：from + size 必须 <= max_result_window（ES 默认 10000）
        int maxPage = Math.max(1, MAX_RESULT_WINDOW / pageSize);
        int safePage = Math.min(Math.max(1, ObjectUtil.defaultIfNull(query.getPageNum(), 1)), maxPage);
        int from = Math.max(0, (safePage - 1) * pageSize);

        JSONArray filter = new JSONArray();
        addTerms(filter, "question_type", query.getQuestionTypes());
        addTerms(filter, "category", query.getCategories());
        addTerms(filter, "subtopic", query.getSubtopics());
        addTerms(filter, "difficulty", query.getDifficulties());

        boolean hasKeyword = StrUtil.isNotBlank(keyword);
        JSONObject must;
        if (hasKeyword) {
            must = new JSONObject().set("multi_match", new JSONObject()
                    .set("query", keyword.trim())
                    .set("fields", new JSONArray().set("title^3").set("tags^2").set("summary").set("content"))
                    .set("type", "best_fields"));
        } else {
            must = new JSONObject().set("match_all", new JSONObject());
        }

        JSONObject body = new JSONObject()
                .set("from", from)
                .set("size", pageSize)
                .set("track_total_hits", true)
                .set("query", new JSONObject().set("bool", new JSONObject().set("must", must).set("filter", filter)))
                .set("aggs", new JSONObject()
                        .set("question_type", terms("question_type", 10))
                        .set("category", terms("category", 60))
                        .set("subtopic", terms("subtopic", 60))
                        .set("difficulty", terms("difficulty", 10)))
                // 列表只取轻量字段：摘要 summary（详情走单独接口），避免传输长正文
                .set("_source", new JSONArray().set("title").set("summary").set("category")
                        .set("tags").set("difficulty").set("doc_type").set("id"));

        if (hasKeyword) {
            body.set("highlight", new JSONObject()
                    .set("pre_tags", new JSONArray().set("<em>"))
                    .set("post_tags", new JSONArray().set("</em>"))
                    .set("fields", new JSONObject()
                            .set("summary", new JSONObject().set("fragment_size", 160).set("number_of_fragments", 1))
                            .set("title", new JSONObject())));
        }

        JSONObject response = esSearchClient.search(esProperties.getQuestionIndex(), body);
        JSONObject hitsObj = response.getJSONObject("hits");

        KbQuestionSearchView view = new KbQuestionSearchView();
        JSONObject totalObj = hitsObj.getJSONObject("total");
        view.setTotal(ObjectUtil.isNull(totalObj) ? 0L : totalObj.getLong("value", 0L));

        List<KbQuestionSearchView.Item> items = new ArrayList<>();
        JSONArray hits = hitsObj.getJSONArray("hits");
        if (CollUtil.isNotEmpty(hits)) {
            for (Object hitObj : hits) {
                JSONObject hit = (JSONObject) hitObj;
                JSONObject source = hit.getJSONObject("_source");
                KbQuestionSearchView.Item item = new KbQuestionSearchView.Item();
                // id 用 MySQL 主键（详情接口按主键查，ES 文档 id 可能不是主键）
                item.setId(source.getStr("id", hit.getStr("_id")));
                item.setTitle(source.getStr("title"));
                item.setCategory(source.getStr("category"));
                item.setDifficulty(source.getStr("difficulty"));
                item.setDocType(source.getStr("doc_type", DEFAULT_DOC_TYPE));
                JSONArray tags = source.getJSONArray("tags");
                item.setTags(ObjectUtil.isNull(tags) ? "" : String.join(",", tags.toList(String.class)));
                item.setSnippet(resolveSnippet(hit, source));
                items.add(item);
            }
        }
        view.setList(items);
        view.setFacets(parseFacets(response));
        return view;
    }

    @Override
    public Map<String, List<KbQuestionSearchView.Bucket>> facets() {
        JSONObject body = new JSONObject()
                .set("size", 0)
                .set("aggs", new JSONObject()
                        .set("question_type", terms("question_type", 10))
                        .set("category", terms("category", 60))
                        .set("subtopic", terms("subtopic", 60))
                        .set("difficulty", terms("difficulty", 10)));
        return parseFacets(esSearchClient.search(esProperties.getQuestionIndex(), body));
    }

    /**
     * 构造 terms 聚合。
     *
     * @param field 字段名
     * @param size 桶数量上限
     * @return terms 聚合 DSL
     */
    private JSONObject terms(String field, int size) {
        return new JSONObject().set("terms", new JSONObject().set("field", field).set("size", size));
    }

    /**
     * 追加 terms 过滤（多值）。
     *
     * @param filter 过滤条件集合
     * @param field 字段名
     * @param values 取值列表；为空不追加
     */
    private void addTerms(JSONArray filter, String field, List<String> values) {
        if (CollUtil.isNotEmpty(values)) {
            filter.add(new JSONObject().set("terms", new JSONObject().set(field, values)));
        }
    }

    /**
     * 解析聚合结果为 facets。
     *
     * @param response ES 原始响应
     * @return 聚合结果
     */
    private Map<String, List<KbQuestionSearchView.Bucket>> parseFacets(JSONObject response) {
        Map<String, List<KbQuestionSearchView.Bucket>> facets = new LinkedHashMap<>();
        JSONObject aggs = response.getJSONObject("aggregations");
        if (ObjectUtil.isNull(aggs)) {
            return facets;
        }
        for (String name : FACET_FIELDS) {
            JSONObject agg = aggs.getJSONObject(name);
            List<KbQuestionSearchView.Bucket> buckets = new ArrayList<>();
            JSONArray bucketArray = ObjectUtil.isNull(agg) ? null : agg.getJSONArray("buckets");
            if (CollUtil.isNotEmpty(bucketArray)) {
                for (Object o : bucketArray) {
                    JSONObject b = (JSONObject) o;
                    KbQuestionSearchView.Bucket bucket = new KbQuestionSearchView.Bucket();
                    bucket.setKey(b.getStr("key"));
                    bucket.setCount(b.getLong("doc_count", 0L));
                    buckets.add(bucket);
                }
            }
            facets.put(name, buckets);
        }
        return facets;
    }

    /**
     * 取高亮片段，没有高亮则截断正文。
     *
     * @param hit ES 命中
     * @param source 文档内容
     * @return 摘要
     */
    private String resolveSnippet(JSONObject hit, JSONObject source) {
        JSONObject highlight = hit.getJSONObject("highlight");
        if (ObjectUtil.isNotNull(highlight)) {
            JSONArray fragments = highlight.getJSONArray("summary");
            if (CollUtil.isNotEmpty(fragments)) {
                return fragments.getStr(0);
            }
            JSONArray titleFragments = highlight.getJSONArray("title");
            if (CollUtil.isNotEmpty(titleFragments)) {
                return titleFragments.getStr(0);
            }
        }
        return StrUtil.maxLength(StrUtil.nullToEmpty(source.getStr("summary")).replaceAll("\\s+", " "), SNIPPET_LENGTH);
    }
}
