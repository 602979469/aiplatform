package com.jakt.aiplatform.core.repository.impl;

import cn.hutool.core.util.ObjectUtil;

import cn.hutool.core.collection.CollUtil;
import com.jakt.aiplatform.common.dal.dataobject.KbQuestionDO;
import com.jakt.aiplatform.common.dal.mapper.KbQuestionMapper;
import com.jakt.aiplatform.common.dal.query.KbQuestionDalQuery;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.KbQuestion;
import com.jakt.aiplatform.core.model.dto.KbQuestionCategoryStat;
import com.jakt.aiplatform.core.model.dto.KbQuestionTypeStat;
import com.jakt.aiplatform.core.model.param.KbQuestionPickParam;
import com.jakt.aiplatform.core.model.param.KbQuestionQueryParam;
import com.jakt.aiplatform.core.repository.KbQuestionRepository;
import com.jakt.aiplatform.core.repository.convertor.KbQuestionConvertor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

/**
 * 题库题目仓储：封装 Mapper，对外只暴露领域模型。单表操作不引入事务，多写事务由 core-service 编排。
 */
@Repository
public class KbQuestionRepositoryImpl implements KbQuestionRepository {

    /** 题库题目 Mapper。 */
    private final KbQuestionMapper kbQuestionMapper;

    public KbQuestionRepositoryImpl(KbQuestionMapper kbQuestionMapper) {
        this.kbQuestionMapper = kbQuestionMapper;
    }

    @Override
    public KbQuestion findById(Long id) {
        KbQuestionDO kbQuestionDO = kbQuestionMapper.selectById(id);
        return KbQuestionConvertor.toModel(kbQuestionDO);
    }

    @Override
    public PageResult<KbQuestion> findPage(KbQuestionQueryParam query) {
        KbQuestionDalQuery dalQuery = KbQuestionConvertor.toDalQuery(query);
        List<KbQuestionDO> doList = kbQuestionMapper.selectPage(dalQuery);
        long total = kbQuestionMapper.countByQuery(dalQuery);
        List<KbQuestion> list = ConvertUtil.map(doList, KbQuestionConvertor::toModel);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public List<KbQuestion> findList(KbQuestionQueryParam query) {
        KbQuestionDalQuery dalQuery = KbQuestionConvertor.toDalQuery(query);
        List<KbQuestionDO> doList = kbQuestionMapper.selectList(dalQuery);
        return ConvertUtil.map(doList, KbQuestionConvertor::toModel);
    }

    @Override
    public KbQuestion insert(KbQuestion kbQuestion) {
        KbQuestionDO kbQuestionDO = KbQuestionConvertor.toDO(kbQuestion);
        kbQuestionMapper.insert(kbQuestionDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        kbQuestion.setId(kbQuestionDO.getId());
        return kbQuestion;
    }

    @Override
    public int update(KbQuestion kbQuestion) {
        KbQuestionDO kbQuestionDO = KbQuestionConvertor.toDO(kbQuestion);
        int affected = kbQuestionMapper.update(kbQuestionDO);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbQuestionRepository.update id={} 影响行数={}",
                kbQuestion.getId(), affected);
        return affected;
    }

    @Override
    public int deleteById(Long id) {
        int affected = kbQuestionMapper.deleteById(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbQuestionRepository.deleteById id={} 影响行数={}", id, affected);
        return affected;
    }

    @Override
    public List<KbQuestion> findByIds(List<Long> ids) {
        if (CollUtil.isEmpty(ids)) {
            return new ArrayList<>();
        }
        List<KbQuestionDO> doList = kbQuestionMapper.selectByIds(ids);
        return ConvertUtil.map(doList, KbQuestionConvertor::toModel);
    }

    @Override
    public List<Long> pickIds(KbQuestionPickParam param) {
        List<Long> ids = kbQuestionMapper.selectPickIds(KbQuestionConvertor.toPickQuery(param));
        return ObjectUtil.defaultIfNull(ids, new ArrayList<>());
    }

    @Override
    public List<KbQuestionTypeStat> countPickByType(KbQuestionPickParam param) {
        List<Map<String, Object>> rows = kbQuestionMapper.countPickByType(KbQuestionConvertor.toPickQuery(param));
        List<KbQuestionTypeStat> list = new ArrayList<>();
        if (CollUtil.isEmpty(rows)) {
            return list;
        }
        for (Map<String, Object> row : rows) {
            KbQuestionTypeStat stat = new KbQuestionTypeStat();
            stat.setQuestionType(String.valueOf(row.get("questionType")));
            stat.setTotal(Long.parseLong(String.valueOf(row.get("total"))));
            list.add(stat);
        }
        return list;
    }

    @Override
    public List<KbQuestionCategoryStat> findCategorySubtopicStats() {
        List<Map<String, Object>> rows = kbQuestionMapper.selectCategorySubtopicSummary();
        if (CollUtil.isEmpty(rows)) {
            return new ArrayList<>();
        }
        List<KbQuestionCategoryStat> list = new ArrayList<>();
        for (Map<String, Object> row : rows) {
            KbQuestionCategoryStat stat = new KbQuestionCategoryStat();
            stat.setCategory(String.valueOf(row.get("category")));
            stat.setSubtopic(String.valueOf(row.get("subtopic")));
            stat.setTotal(Long.parseLong(String.valueOf(row.get("total"))));
            list.add(stat);
        }
        return list;
    }
}
