package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.GenTableColumn;

import java.util.List;

/**
 * 代码生成字段仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface GenTableColumnRepository {

    /**
     * 查询数据库表的列信息（information_schema）。
     *
     * @param tableName 表名
     * @return 代码生成字段列表
     */
    List<GenTableColumn> selectDbTableColumnsByName(String tableName);

    /**
     * 按表ID查询已配置的字段列表。
     *
     * @param genTableColumn 查询条件（tableId 必填）
     * @return 代码生成字段列表
     */
    List<GenTableColumn> selectGenTableColumnListByTableId(GenTableColumn genTableColumn);

    /**
     * 新增代码生成字段。
     *
     * @param genTableColumn 代码生成字段
     * @return 影响行数
     */
    int insertGenTableColumn(GenTableColumn genTableColumn);

    /**
     * 全量更新代码生成字段。
     *
     * @param genTableColumn 代码生成字段
     * @return 影响行数
     */
    int updateGenTableColumn(GenTableColumn genTableColumn);

    /**
     * 按字段列表批量删除。
     *
     * @param genTableColumns 代码生成字段列表
     * @return 影响行数
     */
    int deleteGenTableColumns(List<GenTableColumn> genTableColumns);

    /**
     * 按表ID集合批量删除字段。
     *
     * @param tableIds 表ID数组
     * @return 影响行数
     */
    int deleteGenTableColumnByIds(Long[] tableIds);
}
