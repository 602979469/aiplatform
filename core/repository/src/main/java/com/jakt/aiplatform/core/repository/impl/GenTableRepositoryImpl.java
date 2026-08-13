package com.jakt.aiplatform.core.repository.impl;

import com.jakt.aiplatform.common.dal.dataobject.GenTableColumnDO;
import com.jakt.aiplatform.common.dal.dataobject.GenTableDO;
import com.jakt.aiplatform.common.dal.mapper.GenTableColumnMapper;
import com.jakt.aiplatform.common.dal.mapper.GenTableMapper;
import com.jakt.aiplatform.common.util.tools.ListUtil;
import com.jakt.aiplatform.core.model.domain.GenTable;
import com.jakt.aiplatform.core.model.domain.GenTableColumn;
import com.jakt.aiplatform.core.model.param.GenTableColumnQueryParam;
import com.jakt.aiplatform.core.model.param.GenTableQueryParam;
import com.jakt.aiplatform.core.repository.GenTableRepository;
import com.jakt.aiplatform.core.repository.convertor.GenTableColumnConvertor;
import com.jakt.aiplatform.core.repository.convertor.GenTableConvertor;
import org.springframework.stereotype.Repository;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 代码生成业务表仓储实现（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
@Repository
public class GenTableRepositoryImpl implements GenTableRepository {

    /** 代码生成业务表 Mapper。 */
    private final GenTableMapper genTableMapper;

    /** 代码生成字段 Mapper（组装 columns 用）。 */
    private final GenTableColumnMapper genTableColumnMapper;

    public GenTableRepositoryImpl(GenTableMapper genTableMapper, GenTableColumnMapper genTableColumnMapper) {
        this.genTableMapper = genTableMapper;
        this.genTableColumnMapper = genTableColumnMapper;
    }

    /** 按表ID查询字段并组装到领域模型。 */
    private void fillColumns(GenTable table) {
        GenTableColumnQueryParam query = new GenTableColumnQueryParam();
        query.setTableId(table.getTableId());
        List<GenTableColumnDO> columnList = genTableColumnMapper.selectGenTableColumnListByTableId(query);
        table.setColumns(ListUtil.convert(columnList, GenTableColumnConvertor::toModel));
    }

    @Override
    public List<GenTable> selectGenTableList(GenTable genTable) {
        List<GenTableDO> list = genTableMapper.selectGenTableList(GenTableConvertor.toQueryParam(genTable));
        return ListUtil.convert(list, GenTableConvertor::toModel);
    }

    @Override
    public List<GenTable> selectDbTableList(GenTable genTable) {
        List<GenTableDO> list = genTableMapper.selectDbTableList(GenTableConvertor.toQueryParam(genTable));
        return ListUtil.convert(list, GenTableConvertor::toModel);
    }

    @Override
    public List<GenTable> selectDbTableListByNames(String[] tableNames) {
        List<GenTableDO> list = genTableMapper.selectDbTableListByNames(tableNames);
        return ListUtil.convert(list, GenTableConvertor::toModel);
    }

    @Override
    public List<GenTable> selectGenTableAll() {
        List<GenTableDO> tableList = genTableMapper.selectGenTableAll();
        List<GenTable> list = ListUtil.convert(tableList, GenTableConvertor::toModel);
        List<GenTableColumnDO> columnList = genTableColumnMapper.selectList(new GenTableColumnQueryParam());
        Map<Long, List<GenTableColumn>> columnMap = columnList.stream()
                .collect(Collectors.groupingBy(GenTableColumnDO::getTableId,
                        Collectors.mapping(GenTableColumnConvertor::toModel, Collectors.toList())));
        for (GenTable table : list) {
            List<GenTableColumn> columns = columnMap.get(table.getTableId());
            table.setColumns(columns == null ? new ArrayList<>() : columns);
        }
        return list;
    }

    @Override
    public GenTable selectGenTableById(Long id) {
        GenTable table = GenTableConvertor.toModel(genTableMapper.selectById(id));
        if (table != null) {
            fillColumns(table);
        }
        return table;
    }

    @Override
    public GenTable selectGenTableByName(String tableName) {
        GenTable table = GenTableConvertor.toModel(genTableMapper.selectGenTableByName(tableName));
        if (table != null) {
            fillColumns(table);
        }
        return table;
    }

    @Override
    public int insertGenTable(GenTable genTable) {
        return genTableMapper.insert(GenTableConvertor.toDO(genTable));
    }

    @Override
    public int updateGenTable(GenTable genTable) {
        return genTableMapper.update(GenTableConvertor.toDO(genTable));
    }

    @Override
    public int deleteGenTableByIds(Long[] ids) {
        return genTableMapper.deleteGenTableByIds(ids);
    }

    @Override
    public int createTable(String sql) {
        return genTableMapper.createTable(sql);
    }
}
