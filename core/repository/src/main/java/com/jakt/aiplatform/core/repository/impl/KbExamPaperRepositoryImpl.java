package com.jakt.aiplatform.core.repository.impl;

import cn.hutool.core.util.ObjectUtil;

import com.jakt.aiplatform.common.dal.dataobject.KbExamPaperDO;
import com.jakt.aiplatform.common.dal.mapper.KbExamPaperMapper;
import com.jakt.aiplatform.common.dal.query.KbExamPaperDalQuery;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.KbExamPaper;
import com.jakt.aiplatform.core.model.param.KbExamPaperQueryParam;
import com.jakt.aiplatform.core.repository.KbExamPaperRepository;
import com.jakt.aiplatform.core.repository.convertor.KbExamPaperConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 考试试卷仓储：封装 Mapper，对外只暴露领域模型。单表操作不引入事务，多写事务由 core-service 编排。
 */
@Repository
public class KbExamPaperRepositoryImpl implements KbExamPaperRepository {

    /** 考试试卷 Mapper。 */
    private final KbExamPaperMapper kbExamPaperMapper;

    public KbExamPaperRepositoryImpl(KbExamPaperMapper kbExamPaperMapper) {
        this.kbExamPaperMapper = kbExamPaperMapper;
    }

    @Override
    public KbExamPaper findById(Long id) {
        KbExamPaperDO kbExamPaperDO = kbExamPaperMapper.selectById(id);
        return KbExamPaperConvertor.toModel(kbExamPaperDO);
    }

    @Override
    public List<KbExamPaper> findList(KbExamPaperQueryParam query) {
        KbExamPaperDalQuery dalQuery = KbExamPaperConvertor.toDalQuery(query);
        List<KbExamPaperDO> doList = kbExamPaperMapper.selectList(dalQuery);
        return ConvertUtil.map(doList, KbExamPaperConvertor::toModel);
    }

    @Override
    public KbExamPaper findOne(KbExamPaperQueryParam query) {
        KbExamPaperDalQuery dalQuery = KbExamPaperConvertor.toDalQuery(query);
        KbExamPaperDO row = kbExamPaperMapper.selectOne(dalQuery);
        return ObjectUtil.isNull(row) ? null : KbExamPaperConvertor.toModel(row);
    }

    @Override
    public PageResult<KbExamPaper> findPage(KbExamPaperQueryParam query) {
        KbExamPaperDalQuery dalQuery = KbExamPaperConvertor.toDalQuery(query);
        List<KbExamPaperDO> doList = kbExamPaperMapper.selectPage(dalQuery);
        long total = kbExamPaperMapper.countByQuery(dalQuery);
        List<KbExamPaper> list = ConvertUtil.map(doList, KbExamPaperConvertor::toModel);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public KbExamPaper insert(KbExamPaper kbExamPaper) {
        KbExamPaperDO kbExamPaperDO = KbExamPaperConvertor.toDO(kbExamPaper);
        kbExamPaperMapper.insert(kbExamPaperDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        kbExamPaper.setId(kbExamPaperDO.getId());
        return kbExamPaper;
    }

    @Override
    public int update(KbExamPaper kbExamPaper) {
        KbExamPaperDO kbExamPaperDO = KbExamPaperConvertor.toDO(kbExamPaper);
        int affected = kbExamPaperMapper.update(kbExamPaperDO);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbExamPaperRepository.update id={} 影响行数={}",
                kbExamPaper.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(KbExamPaper kbExamPaper) {
        int affected = kbExamPaperMapper.updateByCondition(KbExamPaperConvertor.toDO(kbExamPaper));
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbExamPaperRepository.updateByCondition id={} 影响行数={}",
                kbExamPaper.getId(), affected);
        return affected;
    }

    @Override
    public int deleteById(Long id) {
        int affected = kbExamPaperMapper.deleteById(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbExamPaperRepository.deleteById id={} 影响行数={}",
                id, affected);
        return affected;
    }
}
