package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.KbExamTemplateRuleDO;
import com.jakt.aiplatform.common.dal.mapper.KbExamTemplateRuleMapper;
import com.jakt.aiplatform.common.dal.query.KbExamTemplateRuleDalQuery;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.KbExamTemplateRule;
import com.jakt.aiplatform.core.model.param.KbExamTemplateRuleQueryParam;
import com.jakt.aiplatform.core.repository.KbExamTemplateRuleRepository;
import com.jakt.aiplatform.core.repository.convertor.KbExamTemplateRuleConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 试卷模板知识点规则仓储：封装 Mapper，对外只暴露领域模型。单表操作不引入事务，多写事务由 core-service 编排。
 */
@Repository
public class KbExamTemplateRuleRepositoryImpl implements KbExamTemplateRuleRepository {

    /** 试卷模板知识点规则 Mapper。 */
    private final KbExamTemplateRuleMapper kbExamTemplateRuleMapper;

    public KbExamTemplateRuleRepositoryImpl(KbExamTemplateRuleMapper kbExamTemplateRuleMapper) {
        this.kbExamTemplateRuleMapper = kbExamTemplateRuleMapper;
    }

    @Override
    public KbExamTemplateRule findById(Long id) {
        KbExamTemplateRuleDO kbExamTemplateRuleDO = kbExamTemplateRuleMapper.selectById(id);
        return KbExamTemplateRuleConvertor.toModel(kbExamTemplateRuleDO);
    }

    @Override
    public List<KbExamTemplateRule> findList(KbExamTemplateRuleQueryParam query) {
        KbExamTemplateRuleDalQuery dalQuery = KbExamTemplateRuleConvertor.toDalQuery(query);
        List<KbExamTemplateRuleDO> doList = kbExamTemplateRuleMapper.selectList(dalQuery);
        return ConvertUtil.map(doList, KbExamTemplateRuleConvertor::toModel);
    }

    @Override
    public KbExamTemplateRule findOne(KbExamTemplateRuleQueryParam query) {
        KbExamTemplateRuleDalQuery dalQuery = KbExamTemplateRuleConvertor.toDalQuery(query);
        KbExamTemplateRuleDO row = kbExamTemplateRuleMapper.selectOne(dalQuery);
        return row == null ? null : KbExamTemplateRuleConvertor.toModel(row);
    }

    @Override
    public PageResult<KbExamTemplateRule> findPage(KbExamTemplateRuleQueryParam query) {
        KbExamTemplateRuleDalQuery dalQuery = KbExamTemplateRuleConvertor.toDalQuery(query);
        List<KbExamTemplateRuleDO> doList = kbExamTemplateRuleMapper.selectPage(dalQuery);
        long total = kbExamTemplateRuleMapper.countByQuery(dalQuery);
        List<KbExamTemplateRule> list = ConvertUtil.map(doList, KbExamTemplateRuleConvertor::toModel);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public KbExamTemplateRule insert(KbExamTemplateRule kbExamTemplateRule) {
        KbExamTemplateRuleDO kbExamTemplateRuleDO = KbExamTemplateRuleConvertor.toDO(kbExamTemplateRule);
        kbExamTemplateRuleMapper.insert(kbExamTemplateRuleDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        kbExamTemplateRule.setId(kbExamTemplateRuleDO.getId());
        return kbExamTemplateRule;
    }

    @Override
    public int update(KbExamTemplateRule kbExamTemplateRule) {
        KbExamTemplateRuleDO kbExamTemplateRuleDO = KbExamTemplateRuleConvertor.toDO(kbExamTemplateRule);
        int affected = kbExamTemplateRuleMapper.update(kbExamTemplateRuleDO);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbExamTemplateRuleRepository.update id={} 影响行数={}",
                kbExamTemplateRule.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(KbExamTemplateRule kbExamTemplateRule) {
        int affected = kbExamTemplateRuleMapper.updateByCondition(KbExamTemplateRuleConvertor.toDO(kbExamTemplateRule));
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbExamTemplateRuleRepository.updateByCondition id={} 影响行数={}",
                kbExamTemplateRule.getId(), affected);
        return affected;
    }

    @Override
    public int deleteById(Long id) {
        int affected = kbExamTemplateRuleMapper.deleteById(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbExamTemplateRuleRepository.deleteById id={} 影响行数={}",
                id, affected);
        return affected;
    }
}
