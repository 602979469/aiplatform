package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.KbUserQuestionStatDO;
import com.jakt.aiplatform.common.dal.mapper.KbUserQuestionStatMapper;
import com.jakt.aiplatform.common.dal.query.KbUserQuestionStatDalQuery;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.KbUserQuestionStat;
import com.jakt.aiplatform.core.model.param.KbUserQuestionStatQueryParam;
import com.jakt.aiplatform.core.repository.KbUserQuestionStatRepository;
import com.jakt.aiplatform.core.repository.convertor.KbUserQuestionStatConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 用户题目掌握状态仓储：封装 Mapper，对外只暴露领域模型。单表操作不引入事务，多写事务由 core-service 编排。
 */
@Repository
public class KbUserQuestionStatRepositoryImpl implements KbUserQuestionStatRepository {

    /** 用户题目掌握状态 Mapper。 */
    private final KbUserQuestionStatMapper kbUserQuestionStatMapper;

    public KbUserQuestionStatRepositoryImpl(KbUserQuestionStatMapper kbUserQuestionStatMapper) {
        this.kbUserQuestionStatMapper = kbUserQuestionStatMapper;
    }

    @Override
    public KbUserQuestionStat findById(Long id) {
        KbUserQuestionStatDO kbUserQuestionStatDO = kbUserQuestionStatMapper.selectById(id);
        return KbUserQuestionStatConvertor.toModel(kbUserQuestionStatDO);
    }

    @Override
    public List<KbUserQuestionStat> findList(KbUserQuestionStatQueryParam query) {
        KbUserQuestionStatDalQuery dalQuery = KbUserQuestionStatConvertor.toDalQuery(query);
        List<KbUserQuestionStatDO> doList = kbUserQuestionStatMapper.selectList(dalQuery);
        return ConvertUtil.map(doList, KbUserQuestionStatConvertor::toModel);
    }

    @Override
    public KbUserQuestionStat findOne(KbUserQuestionStatQueryParam query) {
        KbUserQuestionStatDalQuery dalQuery = KbUserQuestionStatConvertor.toDalQuery(query);
        KbUserQuestionStatDO row = kbUserQuestionStatMapper.selectOne(dalQuery);
        return row == null ? null : KbUserQuestionStatConvertor.toModel(row);
    }

    @Override
    public PageResult<KbUserQuestionStat> findPage(KbUserQuestionStatQueryParam query) {
        KbUserQuestionStatDalQuery dalQuery = KbUserQuestionStatConvertor.toDalQuery(query);
        List<KbUserQuestionStatDO> doList = kbUserQuestionStatMapper.selectPage(dalQuery);
        long total = kbUserQuestionStatMapper.countByQuery(dalQuery);
        List<KbUserQuestionStat> list = ConvertUtil.map(doList, KbUserQuestionStatConvertor::toModel);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public KbUserQuestionStat insert(KbUserQuestionStat kbUserQuestionStat) {
        KbUserQuestionStatDO kbUserQuestionStatDO = KbUserQuestionStatConvertor.toDO(kbUserQuestionStat);
        kbUserQuestionStatMapper.insert(kbUserQuestionStatDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        kbUserQuestionStat.setId(kbUserQuestionStatDO.getId());
        return kbUserQuestionStat;
    }

    @Override
    public int update(KbUserQuestionStat kbUserQuestionStat) {
        KbUserQuestionStatDO kbUserQuestionStatDO = KbUserQuestionStatConvertor.toDO(kbUserQuestionStat);
        int affected = kbUserQuestionStatMapper.update(kbUserQuestionStatDO);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbUserQuestionStatRepository.update id={} 影响行数={}",
                kbUserQuestionStat.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(KbUserQuestionStat kbUserQuestionStat) {
        int affected = kbUserQuestionStatMapper.updateByCondition(KbUserQuestionStatConvertor.toDO(kbUserQuestionStat));
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbUserQuestionStatRepository.updateByCondition id={} 影响行数={}",
                kbUserQuestionStat.getId(), affected);
        return affected;
    }

    @Override
    public int deleteById(Long id) {
        int affected = kbUserQuestionStatMapper.deleteById(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbUserQuestionStatRepository.deleteById id={} 影响行数={}",
                id, affected);
        return affected;
    }
}
