package com.jakt.aiplatform.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import cn.hutool.json.JSONArray;
import cn.hutool.json.JSONObject;
import com.jakt.aiplatform.biz.service.KbQuestionDetailView;
import com.jakt.aiplatform.biz.service.KbQuestionSearchManager;
import com.jakt.aiplatform.biz.service.KbQuestionSearchView;
import com.jakt.aiplatform.common.dal.dataobject.KbQuestionDO;
import com.jakt.aiplatform.common.dal.es.EsProperties;
import com.jakt.aiplatform.common.dal.es.EsSearchClient;
import com.jakt.aiplatform.common.dal.mapper.KbQuestionMapper;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
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

    /** ES 默认 max_result_window：from + size 不能超过该值。 */
    private static final int MAX_RESULT_WINDOW = 10000;

    /** ES 客户端。 */
    private final EsSearchClient esSearchClient;

    /** ES 配置。 */
    private final EsProperties esProperties;

    /** 题库 Mapper（详情查询走 MySQL）。 */
    private final KbQuestionMapper kbQuestionMapper;

    public KbQuestionSearchManagerImpl(EsSearchClient esSearchClient, EsProperties esProperties,
                                       KbQuestionMapper kbQuestionMapper) {
        this.esSearchClient = esSearchClient;
        this.esProperties = esProperties;
        this.kbQuestionMapper = kbQuestionMapper;
    }

    @Override
    public KbQuestionSearchView search(String keyword, int pageNum, int pageSize) {
        // 深分页保护：from + size 必须 <= max_result_window（ES 默认 10000），超出会 400
        int maxPage = Math.max(1, MAX_RESULT_WINDOW / Math.max(1, pageSize));
        int safePage = Math.min(Math.max(1, pageNum), maxPage);
        int from = Math.max(0, (safePage - 1) * pageSize);
        JSONObject body = new JSONObject()
                .set("from", from)
                .set("size", pageSize)
                .set("track_total_hits", true)
                // 列表只取轻量字段：摘要 summary（详情走单独接口），避免传输长正文
                .set("_source", new JSONArray().set("title").set("summary").set("category")
                        .set("tags").set("difficulty").set("doc_type").set("id"));
        if (StrUtil.isBlank(keyword)) {
            body.set("query", new JSONObject().set("match_all", new JSONObject()));
        } else {
            body.set("query", new JSONObject().set("multi_match", new JSONObject()
                    .set("query", keyword.trim())
                    .set("fields", new JSONArray().set("title^3").set("tags^2").set("summary").set("content"))
                    .set("type", "best_fields")));
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
        view.setTotal(hitsObj.getJSONObject("total") == null ? 0L : hitsObj.getJSONObject("total").getLong("value", 0L));
        List<KbQuestionSearchView.Item> items = new ArrayList<>();
        JSONArray hits = hitsObj.getJSONArray("hits");
        if (hits != null) {
            for (Object hitObj : hits) {
                JSONObject hit = (JSONObject) hitObj;
                JSONObject source = hit.getJSONObject("_source");
                KbQuestionSearchView.Item item = new KbQuestionSearchView.Item();
                // id 用 MySQL 主键（详情接口按主键查，ES 文档 _id 也是主键）
                item.setId(source.getStr("id", hit.getStr("_id")));
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
            JSONArray fragments = highlight.getJSONArray("summary");
            if (fragments != null && !fragments.isEmpty()) {
                return fragments.getStr(0);
            }
            JSONArray titleFragments = highlight.getJSONArray("title");
            if (titleFragments != null && !titleFragments.isEmpty()) {
                return titleFragments.getStr(0);
            }
        }
        return StrUtil.maxLength(StrUtil.nullToEmpty(source.getStr("summary")).replaceAll("\\s+", " "), SNIPPET_LENGTH);
    }

    @Override
    public KbQuestionDetailView detail(Long id) {
        KbQuestionDO row = kbQuestionMapper.selectById(id);
        if (row == null) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "题目不存在");
        }
        KbQuestionDetailView view = new KbQuestionDetailView();
        view.setId(row.getId());
        view.setDocType(row.getQuestionType());
        view.setCategory(row.getCategory());
        view.setTitle(row.getTitle());
        view.setContent(row.getContent());
        view.setOptions(row.getOptions());
        view.setAnswer(row.getAnswer());
        view.setExplanation(row.getExplanation());
        view.setDifficulty(row.getDifficulty());
        view.setTags(row.getTags());
        view.setSourcePath(row.getSourcePath());
        view.setCreateTime(row.getCreateTime());
        return view;
    }
}
