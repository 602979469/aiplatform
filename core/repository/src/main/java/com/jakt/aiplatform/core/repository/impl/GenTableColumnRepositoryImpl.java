package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.GenTableColumnDO;
import com.jakt.aiplatform.common.dal.mapper.GenTableColumnMapper;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.GenTableColumn;
import com.jakt.aiplatform.core.model.param.GenTableColumnQueryParam;
import com.jakt.aiplatform.core.repository.GenTableColumnRepository;
import com.jakt.aiplatform.core.repository.convertor.GenTableColumnConvertor;
import org.springframework.stereotype.Repository;

import java.util.List;

/**
 * 代码生成字段仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class GenTableColumnRepositoryImpl implements GenTableColumnRepository {

    /** 代码生成字段 Mapper。 */
    private final GenTableColumnMapper genTableColumnMapper;

    public GenTableColumnRepositoryImpl(GenTableColumnMapper genTableColumnMapper) {
        this.genTableColumnMapper = genTableColumnMapper;
    }

    @Override
    public List<GenTableColumn> selectDbTableColumnsByName(String tableName) {
        List<GenTableColumnDO> list = genTableColumnMapper.selectDbTableColumnsByName(tableName);
        return ListUtil.convert(list, GenTableColumnConvertor::toModel);
    }

    @Override
    public List<GenTableColumn> selectGenTableColumnListByTableId(GenTableColumn genTableColumn) {
        GenTableColumnQueryParam query = GenTableColumnConvertor.toQueryParam(genTableColumn);
        List<GenTableColumnDO> list = genTableColumnMapper.selectGenTableColumnListByTableId(query);
        return ListUtil.convert(list, GenTableColumnConvertor::toModel);
    }

    @Override
    public int insertGenTableColumn(GenTableColumn genTableColumn) {
        return genTableColumnMapper.insert(GenTableColumnConvertor.toDO(genTableColumn));
    }

    @Override
    public int updateGenTableColumn(GenTableColumn genTableColumn) {
        return genTableColumnMapper.update(GenTableColumnConvertor.toDO(genTableColumn));
    }

    @Override
    public int deleteGenTableColumns(List<GenTableColumn> genTableColumns) {
        List<GenTableColumnDO> doList = ListUtil.convert(genTableColumns, GenTableColumnConvertor::toDO);
        return genTableColumnMapper.deleteGenTableColumns(doList);
    }

    @Override
    public int deleteGenTableColumnByIds(Long[] tableIds) {
        return genTableColumnMapper.deleteGenTableColumnByIds(tableIds);
    }
}
