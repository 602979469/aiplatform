package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.GenTableColumnDO;
import com.jakt.aiplatform.common.dal.mapper.GenTableColumnMapper;
import com.jakt.aiplatform.common.util.tools.AiPlatformInvoker;
import com.jakt.aiplatform.core.model.domain.GenTableColumn;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.GenTableColumnQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;
import com.jakt.aiplatform.core.model.util.AiPlatformLoggerUtil;
import com.jakt.aiplatform.core.repository.GenTableColumnRepository;
import com.jakt.aiplatform.core.repository.convertor.GenTableColumnConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 代码生成字段仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
@Repository
public class GenTableColumnRepositoryImpl implements GenTableColumnRepository {

    /** 代码生成字段 Mapper。 */
    private final GenTableColumnMapper genTableColumnMapper;

    public GenTableColumnRepositoryImpl(GenTableColumnMapper genTableColumnMapper) {
        this.genTableColumnMapper = genTableColumnMapper;
    }

    @Override
    public GenTableColumn findById(Long id) {
        return GenTableColumnConvertor.toModel(genTableColumnMapper.selectById(id));
    }

    @Override
    public List<GenTableColumn> findList(GenTableColumnQueryParam query) {
        return genTableColumnMapper.selectList(query).stream().map(GenTableColumnConvertor::toModel).toList();
    }

    @Override
    public PageResult<GenTableColumn> findPage(GenTableColumnQueryParam query) {
        List<GenTableColumnDO> doList = genTableColumnMapper.selectPage(query);
        long total = genTableColumnMapper.countByQuery(query);
        List<GenTableColumn> list = doList.stream().map(GenTableColumnConvertor::toModel).toList();
        return new PageResult<>(total, query.getPageNum(), query.getPageSize(), list);
    }

    @Override
    public GenTableColumn insert(GenTableColumn genTableColumn) {
        GenTableColumnDO genTableColumnDO = GenTableColumnConvertor.toDO(genTableColumn);
        genTableColumnMapper.insert(genTableColumnDO);
        return GenTableColumnConvertor.toModel(genTableColumnDO);
    }

    @Override
    public void update(GenTableColumn genTableColumn) {
        GenTableColumnDO genTableColumnDO = GenTableColumnConvertor.toDO(genTableColumn);
        int affected = genTableColumnMapper.update(genTableColumnDO);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "GenTableColumnRepository.update columnId={} 影响行数={}", genTableColumn.getColumnId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void updateByCondition(GenTableColumn genTableColumn) {
        int affected = genTableColumnMapper.updateByCondition(GenTableColumnConvertor.toDO(genTableColumn));
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "GenTableColumnRepository.updateByCondition columnId={} 影响行数={}", genTableColumn.getColumnId(), affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.UPDATE_FAILED, "更新失败：记录不存在或已被修改");
    }

    @Override
    public void deleteById(Long id) {
        int affected = genTableColumnMapper.deleteById(id);
        AiPlatformLoggerUtil.info(LogFileEnum.BIZ_SERVICE, "GenTableColumnRepository.deleteById id={} 影响行数={}", id, affected);
        AiPlatformInvoker.throwErrWhenTrue(affected == 0, ErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
