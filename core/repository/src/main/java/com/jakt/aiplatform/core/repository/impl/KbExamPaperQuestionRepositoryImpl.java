package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.KbExamPaperQuestionDO;
import com.jakt.aiplatform.common.dal.mapper.KbExamPaperQuestionMapper;
import com.jakt.aiplatform.common.dal.query.KbExamPaperQuestionDalQuery;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.KbExamPaperQuestion;
import com.jakt.aiplatform.core.model.param.KbExamPaperQuestionQueryParam;
import com.jakt.aiplatform.core.repository.KbExamPaperQuestionRepository;
import com.jakt.aiplatform.core.repository.convertor.KbExamPaperQuestionConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 试卷题目快照与作答仓储：封装 Mapper，对外只暴露领域模型。单表操作不引入事务，多写事务由 core-service 编排。
 */
@Repository
public class KbExamPaperQuestionRepositoryImpl implements KbExamPaperQuestionRepository {

    /** 试卷题目快照与作答 Mapper。 */
    private final KbExamPaperQuestionMapper kbExamPaperQuestionMapper;

    public KbExamPaperQuestionRepositoryImpl(KbExamPaperQuestionMapper kbExamPaperQuestionMapper) {
        this.kbExamPaperQuestionMapper = kbExamPaperQuestionMapper;
    }

    @Override
    public KbExamPaperQuestion findById(Long id) {
        KbExamPaperQuestionDO kbExamPaperQuestionDO = kbExamPaperQuestionMapper.selectById(id);
        return KbExamPaperQuestionConvertor.toModel(kbExamPaperQuestionDO);
    }

    @Override
    public List<KbExamPaperQuestion> findList(KbExamPaperQuestionQueryParam query) {
        KbExamPaperQuestionDalQuery dalQuery = KbExamPaperQuestionConvertor.toDalQuery(query);
        List<KbExamPaperQuestionDO> doList = kbExamPaperQuestionMapper.selectList(dalQuery);
        return ConvertUtil.map(doList, KbExamPaperQuestionConvertor::toModel);
    }

    @Override
    public KbExamPaperQuestion findOne(KbExamPaperQuestionQueryParam query) {
        KbExamPaperQuestionDalQuery dalQuery = KbExamPaperQuestionConvertor.toDalQuery(query);
        KbExamPaperQuestionDO row = kbExamPaperQuestionMapper.selectOne(dalQuery);
        return row == null ? null : KbExamPaperQuestionConvertor.toModel(row);
    }

    @Override
    public PageResult<KbExamPaperQuestion> findPage(KbExamPaperQuestionQueryParam query) {
        KbExamPaperQuestionDalQuery dalQuery = KbExamPaperQuestionConvertor.toDalQuery(query);
        List<KbExamPaperQuestionDO> doList = kbExamPaperQuestionMapper.selectPage(dalQuery);
        long total = kbExamPaperQuestionMapper.countByQuery(dalQuery);
        List<KbExamPaperQuestion> list = ConvertUtil.map(doList, KbExamPaperQuestionConvertor::toModel);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public KbExamPaperQuestion insert(KbExamPaperQuestion kbExamPaperQuestion) {
        KbExamPaperQuestionDO kbExamPaperQuestionDO = KbExamPaperQuestionConvertor.toDO(kbExamPaperQuestion);
        kbExamPaperQuestionMapper.insert(kbExamPaperQuestionDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        kbExamPaperQuestion.setId(kbExamPaperQuestionDO.getId());
        return kbExamPaperQuestion;
    }

    @Override
    public int update(KbExamPaperQuestion kbExamPaperQuestion) {
        KbExamPaperQuestionDO kbExamPaperQuestionDO = KbExamPaperQuestionConvertor.toDO(kbExamPaperQuestion);
        int affected = kbExamPaperQuestionMapper.update(kbExamPaperQuestionDO);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbExamPaperQuestionRepository.update id={} 影响行数={}",
                kbExamPaperQuestion.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(KbExamPaperQuestion kbExamPaperQuestion) {
        int affected = kbExamPaperQuestionMapper.updateByCondition(KbExamPaperQuestionConvertor.toDO(kbExamPaperQuestion));
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbExamPaperQuestionRepository.updateByCondition id={} 影响行数={}",
                kbExamPaperQuestion.getId(), affected);
        return affected;
    }

    @Override
    public int deleteById(Long id) {
        int affected = kbExamPaperQuestionMapper.deleteById(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbExamPaperQuestionRepository.deleteById id={} 影响行数={}",
                id, affected);
        return affected;
    }
}
