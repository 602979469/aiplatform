package com.jakt.aiplatform.biz.service.impl;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.biz.service.KbQuestionAdminManager;
import com.jakt.aiplatform.biz.service.KbQuestionMetaView;
import com.jakt.aiplatform.common.dal.dataobject.KbQuestionDO;
import com.jakt.aiplatform.common.dal.mapper.KbQuestionMapper;
import com.jakt.aiplatform.common.dal.query.KbQuestionDalQuery;
import com.jakt.aiplatform.common.framework.context.UserContext;
import com.jakt.aiplatform.common.framework.enums.ErrorCodeEnum;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.exception.AiPlatformException;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.model.param.KbQuestionQueryParam;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 题库管理实现：题库无删除标记，删除走物理删除（当前无回收需求）。
 */
@Service
public class KbQuestionAdminManagerImpl implements KbQuestionAdminManager {

    /** 题库 Mapper。 */
    private final KbQuestionMapper kbQuestionMapper;

    public KbQuestionAdminManagerImpl(KbQuestionMapper kbQuestionMapper) {
        this.kbQuestionMapper = kbQuestionMapper;
    }

    @Override
    public PageResult<KbQuestionDO> page(KbQuestionQueryParam query) {
        KbQuestionDalQuery dalQuery = new KbQuestionDalQuery();
        dalQuery.setPageNum(query.getPageNum());
        dalQuery.setPageSize(query.getPageSize());
        dalQuery.setKeyword(StrUtil.trimToNull(query.getKeyword()));
        dalQuery.setCategory(StrUtil.trimToNull(query.getCategory()));
        dalQuery.setSubtopic(StrUtil.trimToNull(query.getSubtopic()));
        dalQuery.setQuestionType(StrUtil.trimToNull(query.getQuestionType()));
        dalQuery.setDifficulty(StrUtil.trimToNull(query.getDifficulty()));
        List<KbQuestionDO> rows = kbQuestionMapper.selectPage(dalQuery);
        long total = kbQuestionMapper.countByQuery(dalQuery);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), rows);
    }

    @Override
    public KbQuestionDO get(Long id) {
        KbQuestionDO row = kbQuestionMapper.selectById(id);
        if (row == null) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "题目不存在: " + id);
        }
        return row;
    }

    @Override
    public Long create(KbQuestionDO question) {
        String operator = UserContext.getUserName();
        question.setCreateBy(operator);
        question.setUpdateBy(operator);
        kbQuestionMapper.insert(question);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "新增题目成功 id={} category={} type={}",
                question.getId(), question.getCategory(), question.getQuestionType());
        return question.getId();
    }

    @Override
    public void update(KbQuestionDO question) {
        get(question.getId());
        question.setUpdateBy(UserContext.getUserName());
        int affected = kbQuestionMapper.update(question);
        if (affected == 0) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "更新失败：题目不存在或已被删除");
        }
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "修改题目成功 id={}", question.getId());
    }

    @Override
    public void delete(Long id) {
        get(id);
        int affected = kbQuestionMapper.deleteById(id);
        if (affected == 0) {
            throw AiPlatformException.ofThrow(ErrorCodeEnum.PARAM_INVALID, "删除失败：题目不存在或已被删除");
        }
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "删除题目成功 id={}", id);
    }

    @Override
    public KbQuestionMetaView meta() {
        List<Map<String, Object>> rows = kbQuestionMapper.selectCategorySubtopicSummary();
        Map<String, KbQuestionMetaView.CategoryNode> grouped = new LinkedHashMap<>();
        for (Map<String, Object> row : rows) {
            String category = row.get("category") == null ? "" : String.valueOf(row.get("category"));
            String subtopic = row.get("subtopic") == null ? "" : String.valueOf(row.get("subtopic"));
            long total = row.get("total") == null ? 0L : Long.parseLong(String.valueOf(row.get("total")));
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
}
