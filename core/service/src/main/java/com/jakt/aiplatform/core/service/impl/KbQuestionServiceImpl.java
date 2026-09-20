package com.jakt.aiplatform.core.service.impl;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.framework.context.UserContext;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.model.domain.KbQuestion;
import com.jakt.aiplatform.core.model.dto.KbQuestionCategoryStat;
import com.jakt.aiplatform.core.model.dto.KbQuestionDetailView;
import com.jakt.aiplatform.core.model.dto.KbQuestionMetaView;
import com.jakt.aiplatform.core.model.dto.KbQuestionSearchView;
import com.jakt.aiplatform.core.model.param.KbQuestionQueryParam;
import com.jakt.aiplatform.core.model.param.KbQuestionSearchQuery;
import com.jakt.aiplatform.core.repository.KbQuestionRepository;
import com.jakt.aiplatform.core.repository.KbQuestionSearchRepository;
import com.jakt.aiplatform.core.service.KbQuestionService;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 题库领域服务实现：管理端 CRUD 走 MySQL，检索走 Elasticsearch。
 */
@Service
public class KbQuestionServiceImpl implements KbQuestionService {

    /** 题库仓储。 */
    private final KbQuestionRepository kbQuestionRepository;

    /** 题库检索仓储。 */
    private final KbQuestionSearchRepository kbQuestionSearchRepository;

    public KbQuestionServiceImpl(KbQuestionRepository kbQuestionRepository,
                                 KbQuestionSearchRepository kbQuestionSearchRepository) {
        this.kbQuestionRepository = kbQuestionRepository;
        this.kbQuestionSearchRepository = kbQuestionSearchRepository;
    }

    @Override
    public PageResult<KbQuestion> page(KbQuestionQueryParam query) {
        return kbQuestionRepository.findPage(normalize(query));
    }

    @Override
    public KbQuestion get(Long id) {
        KbQuestion question = kbQuestionRepository.findById(id);
        AssertUtil.throwErrWhenNull(question, ErrorCodeEnum.PARAM_INVALID, "题目不存在: " + id);
        return question;
    }

    @Override
    public Long create(KbQuestion question) {
        String operator = UserContext.getUserName();
        question.setCreateBy(operator);
        question.setUpdateBy(operator);
        KbQuestion created = kbQuestionRepository.insert(question);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "新增题目成功 id={} category={} type={}",
                created.getId(), created.getCategory(), created.getQuestionType());
        return created.getId();
    }

    @Override
    public void update(KbQuestion question) {
        get(question.getId());
        question.setUpdateBy(UserContext.getUserName());
        int affected = kbQuestionRepository.update(question);
        AssertUtil.throwErrWhenTrue(affected == 0, ErrorCodeEnum.PARAM_INVALID, "更新失败：题目不存在或已被删除");
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "修改题目成功 id={}", question.getId());
    }

    @Override
    public void delete(Long id) {
        get(id);
        int affected = kbQuestionRepository.deleteById(id);
        AssertUtil.throwErrWhenTrue(affected == 0, ErrorCodeEnum.PARAM_INVALID, "删除失败：题目不存在或已被删除");
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除题目成功 id={}", id);
    }

    @Override
    public KbQuestionMetaView meta() {
        List<KbQuestionCategoryStat> stats = kbQuestionRepository.findCategorySubtopicStats();
        Map<String, KbQuestionMetaView.CategoryNode> grouped = new LinkedHashMap<>();
        for (KbQuestionCategoryStat stat : stats) {
            String category = stat.getCategory();
            String subtopic = stat.getSubtopic();
            long total = stat.getTotal();
            KbQuestionMetaView.CategoryNode node = grouped.computeIfAbsent(category, key -> {
                KbQuestionMetaView.CategoryNode created = new KbQuestionMetaView.CategoryNode();
                created.setCategory(key);
                created.setTotal(0L);
                created.setSubtopics(new ArrayList<>());
                return created;
            });
            node.setTotal(node.getTotal() + total);
            KbQuestionMetaView.SubtopicNode subtopicNode = new KbQuestionMetaView.SubtopicNode();
            subtopicNode.setSubtopic(subtopic);
            subtopicNode.setTotal(total);
            node.getSubtopics().add(subtopicNode);
        }
        KbQuestionMetaView view = new KbQuestionMetaView();
        view.setCategories(new ArrayList<>(grouped.values()));
        return view;
    }

    @Override
    public KbQuestionSearchView search(KbQuestionSearchQuery query) {
        return kbQuestionSearchRepository.search(query);
    }

    @Override
    public Map<String, List<KbQuestionSearchView.Bucket>> facets() {
        return kbQuestionSearchRepository.facets();
    }

    @Override
    public KbQuestionDetailView detail(Long id) {
        KbQuestion row = kbQuestionRepository.findById(id);
        AssertUtil.throwErrWhenNull(row, ErrorCodeEnum.PARAM_INVALID, "题目不存在");
        KbQuestionDetailView view = new KbQuestionDetailView();
        view.setId(row.getId());
        view.setDocType(row.getQuestionType());
        view.setCategory(row.getCategory());
        view.setSubtopic(row.getSubtopic());
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

    /**
     * 查询条件归一化：去空白，避免空串参与等值匹配。
     *
     * @param query 原始查询参数
     * @return 归一化后的查询参数
     */
    private KbQuestionQueryParam normalize(KbQuestionQueryParam query) {
        query.setKeyword(StrUtil.trimToNull(query.getKeyword()));
        query.setCategory(StrUtil.trimToNull(query.getCategory()));
        query.setSubtopic(StrUtil.trimToNull(query.getSubtopic()));
        query.setQuestionType(StrUtil.trimToNull(query.getQuestionType()));
        query.setDifficulty(StrUtil.trimToNull(query.getDifficulty()));
        return query;
    }
}
