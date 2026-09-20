package com.jakt.aiplatform.core.repository.impl;

import cn.hutool.core.util.ObjectUtil;

import com.jakt.aiplatform.common.dal.dataobject.KbExamTemplateDO;
import com.jakt.aiplatform.common.dal.mapper.KbExamTemplateMapper;
import com.jakt.aiplatform.common.dal.query.KbExamTemplateDalQuery;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.KbExamTemplate;
import com.jakt.aiplatform.core.model.param.KbExamTemplateQueryParam;
import com.jakt.aiplatform.core.repository.KbExamTemplateRepository;
import com.jakt.aiplatform.core.repository.convertor.KbExamTemplateConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 试卷模板仓储：封装 Mapper，对外只暴露领域模型。单表操作不引入事务，多写事务由 core-service 编排。
 */
@Repository
public class KbExamTemplateRepositoryImpl implements KbExamTemplateRepository {

    /** 试卷模板 Mapper。 */
    private final KbExamTemplateMapper kbExamTemplateMapper;

    public KbExamTemplateRepositoryImpl(KbExamTemplateMapper kbExamTemplateMapper) {
        this.kbExamTemplateMapper = kbExamTemplateMapper;
    }

    @Override
    public KbExamTemplate findById(Long id) {
        KbExamTemplateDO kbExamTemplateDO = kbExamTemplateMapper.selectById(id);
        return KbExamTemplateConvertor.toModel(kbExamTemplateDO);
    }

    @Override
    public List<KbExamTemplate> findList(KbExamTemplateQueryParam query) {
        KbExamTemplateDalQuery dalQuery = KbExamTemplateConvertor.toDalQuery(query);
        List<KbExamTemplateDO> doList = kbExamTemplateMapper.selectList(dalQuery);
        return ConvertUtil.map(doList, KbExamTemplateConvertor::toModel);
    }

    @Override
    public KbExamTemplate findOne(KbExamTemplateQueryParam query) {
        KbExamTemplateDalQuery dalQuery = KbExamTemplateConvertor.toDalQuery(query);
        KbExamTemplateDO row = kbExamTemplateMapper.selectOne(dalQuery);
        return ObjectUtil.isNull(row) ? null : KbExamTemplateConvertor.toModel(row);
    }

    @Override
    public PageResult<KbExamTemplate> findPage(KbExamTemplateQueryParam query) {
        KbExamTemplateDalQuery dalQuery = KbExamTemplateConvertor.toDalQuery(query);
        List<KbExamTemplateDO> doList = kbExamTemplateMapper.selectPage(dalQuery);
        long total = kbExamTemplateMapper.countByQuery(dalQuery);
        List<KbExamTemplate> list = ConvertUtil.map(doList, KbExamTemplateConvertor::toModel);
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public KbExamTemplate insert(KbExamTemplate kbExamTemplate) {
        KbExamTemplateDO kbExamTemplateDO = KbExamTemplateConvertor.toDO(kbExamTemplate);
        kbExamTemplateMapper.insert(kbExamTemplateDO);
        // 主键回填到入参（自增主键由数据库生成），调用方直接使用原对象
        kbExamTemplate.setId(kbExamTemplateDO.getId());
        return kbExamTemplate;
    }

    @Override
    public int update(KbExamTemplate kbExamTemplate) {
        KbExamTemplateDO kbExamTemplateDO = KbExamTemplateConvertor.toDO(kbExamTemplate);
        int affected = kbExamTemplateMapper.update(kbExamTemplateDO);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbExamTemplateRepository.update id={} 影响行数={}",
                kbExamTemplate.getId(), affected);
        return affected;
    }

    @Override
    public int updateByCondition(KbExamTemplate kbExamTemplate) {
        int affected = kbExamTemplateMapper.updateByCondition(KbExamTemplateConvertor.toDO(kbExamTemplate));
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbExamTemplateRepository.updateByCondition id={} 影响行数={}",
                kbExamTemplate.getId(), affected);
        return affected;
    }

    @Override
    public int deleteById(Long id) {
        int affected = kbExamTemplateMapper.deleteById(id);
        LoggerUtil.info(LogFileEnum.BIZ_SERVICE, "KbExamTemplateRepository.deleteById id={} 影响行数={}",
                id, affected);
        return affected;
    }
}
