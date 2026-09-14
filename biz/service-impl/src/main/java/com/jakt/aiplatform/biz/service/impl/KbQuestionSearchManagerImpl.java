package com.jakt.aiplatform.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.jakt.aiplatform.biz.service.KbQuestionSearchManager;
import com.jakt.aiplatform.biz.service.KbQuestionSearchView;
import com.jakt.aiplatform.common.dal.es.EsProperties;
import com.jakt.aiplatform.common.dal.es.EsSearchClient;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 题库检索实现：调用 Elasticsearch（索引 java-kb）。
 */
@Service
public class KbQuestionSearchManagerImpl implements KbQuestionSearchManager {

    /** 摘要截断长度。 */
    private static final int SNIPPET_LENGTH = 200;

    /** ES 客户端。 */
    private final EsSearchClient esSearchClient;

    /** ES 配置。 */
    private final EsProperties esProperties;

    public KbQuestionSearchManagerImpl(EsSearchClient esSearchClient, EsProperties esProperties) {
        this.esSearchClient = esSearchClient;
        this.esProperties = esProperties;
    }

    @Override
    public KbQuestionSearchView search(String keyword, int pageNum, int pageSize) {
        int from = Math.max(0, (pageNum - 1) * pageSize);
        JSONObject body = new JSONObject()
                .set("from", from)
                .set("size", pageSize)
                .set("track_total_hits", true)
                .set("_source", new JSONArray().set("title").set("content").set("category")
                        .set("tags").set("difficulty").set("doc_type"));
        if (StrUtil.isBlank(keyword)) {
            body.set("query", new JSONObject().set("match_all", new JSONObject()));
        } else {
            body.set("query", new JSONObject().set("multi_match", new JSONObject()
                    .set("query", keyword.trim())
                    .set("fields", new JSONArray().set("title^3").set("tags^2").set("content"))
                    .set("type", "best_fields")));
            body.set("highlight", new JSONObject()
                    .set("pre_tags", new JSONArray().set("<em>"))
                    .set("post_tags", new JSONArray().set("</em>"))
                    .set("fields", new JSONObject()
                            .set("content", new JSONObject().set("fragment_size", 160).set("number_of_fragments", 1))
                            .set("title", new JSONObject())));
        }

        JSONObject response = esSearchClient.search(esProperties.getQuestionIndex(), body);
        JSONObject hitsObj = response.getJSONObject("hits");
        KbQuestionSearchView view = new KbQuestionSearchView();
        view.setTotal(hitsObj.getJSONObject("total") == null ? 0L : hitsObj.getJSONObject("total").getLong("value", 0L));
        List<KbQuestionSearchView.Item> items = new ArrayList<>();
        JSONArray hits = hitsObj.getJSONArray("hits");
        if (hits != null) {
            for (Object hitObj : hits) {
                JSONObject hit = (JSONObject) hitObj;
                JSONObject source = hit.getJSONObject("_source");
                KbQuestionSearchView.Item item = new KbQuestionSearchView.Item();
                item.setId(hit.getStr("_id"));
                item.setTitle(source.getStr("title"));
                item.setCategory(source.getStr("category"));
                item.setDifficulty(source.getStr("difficulty"));
                item.setDocType(source.getStr("doc_type", "qa"));
                JSONArray tags = source.getJSONArray("tags");
                item.setTags(tags == null ? "" : String.join(",", tags.toList(String.class)));
                item.setSnippet(resolveSnippet(hit, source));
                items.add(item);
            }
        }
        view.setList(items);
        return view;
    }

    /**
     * 取高亮片段，没有高亮则截断正文。
     *
     * @param hit    ES 命中
     * @param source 文档内容
     * @return 摘要
     */
    private String resolveSnippet(JSONObject hit, JSONObject source) {
        JSONObject highlight = hit.getJSONObject("highlight");
        if (highlight != null) {
            JSONArray fragments = highlight.getJSONArray("content");
            if (fragments != null && !fragments.isEmpty()) {
                return fragments.getStr(0);
            }
            JSONArray titleFragments = highlight.getJSONArray("title");
            if (titleFragments != null && !titleFragments.isEmpty()) {
                return titleFragments.getStr(0);
            }
        }
        return StrUtil.maxLength(StrUtil.nullToEmpty(source.getStr("content")).replaceAll("\\s+", " "), SNIPPET_LENGTH);
    }
}
