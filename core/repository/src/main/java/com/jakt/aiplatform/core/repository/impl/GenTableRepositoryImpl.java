package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.GenTableDO;
import com.jakt.aiplatform.common.dal.mapper.GenTableMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.GenTable;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.GenTableQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.GenTableRepository;
import com.jakt.aiplatform.core.repository.convertor.GenTableConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 代码生成仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class GenTableRepositoryImpl implements GenTableRepository {

    /** 代码生成 Mapper。 */
    private final GenTableMapper genTableMapper;

    public GenTableRepositoryImpl(GenTableMapper genTableMapper) {
        this.genTableMapper = genTableMapper;
    }

    @Override
    public GenTable findById(Long id) {
        return GenTableConvertor.toModel(genTableMapper.selectById(id));
    }

    @Override
    public List<GenTable> findList(GenTableQueryParam query) {
        return genTableMapper.selectList(query).stream().map(GenTableConvertor::toModel).toList();
    }

    @Override
    public PageResult<GenTable> findPage(GenTableQueryParam query) {
        List<GenTableDO> doList = genTableMapper.selectPage(query);
        long total = genTableMapper.countByQuery(query);
        List<GenTable> list = doList.stream().map(GenTableConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public GenTable insert(GenTable genTable) {
        GenTableDO genTableDO = GenTableConvertor.toDO(genTable);
        genTableMapper.insert(genTableDO);
        return GenTableConvertor.toModel(genTableDO);
    }

    @Override
    public void update(GenTable genTable) {
        GenTableDO genTableDO = GenTableConvertor.toDO(genTable);
        int affected = genTableMapper.update(genTableDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "GenTableRepository.update tableId={} 影响行数={}", genTable.getTableId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(GenTable genTable) {
        int affected = genTableMapper.updateByCondition(GenTableConvertor.toDO(genTable));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "GenTableRepository.updateByCondition tableId={} 影响行数={}", genTable.getTableId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = genTableMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "GenTableRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
