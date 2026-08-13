package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.GenTable;

import java.util.List;

/**
 * 代码生成业务表仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface GenTableRepository {

    /**
     * 按条件查询代码生成业务表列表。
     *
     * @param genTable 查询条件（实体即条件）
     * @return 代码生成业务表列表
     */
    List<GenTable> selectGenTableList(GenTable genTable);

    /**
     * 查询数据库未导入的业务表（information_schema）。
     *
     * @param genTable 查询条件
     * @return 数据库业务表列表
     */
    List<GenTable> selectDbTableList(GenTable genTable);

    /**
     * 按表名集合查询数据库业务表。
     *
     * @param tableNames 表名数组
     * @return 数据库业务表列表
     */
    List<GenTable> selectDbTableListByNames(String[] tableNames);

    /**
     * 查询全部代码生成业务表（含字段）。
     *
     * @return 代码生成业务表列表
     */
    List<GenTable> selectGenTableAll();

    /**
     * 按主键查询代码生成业务表（含字段）。
     *
     * @param id 主键
     * @return 代码生成业务表领域模型
     */
    GenTable selectGenTableById(Long id);

    /**
     * 按表名查询代码生成业务表（含字段）。
     *
     * @param tableName 表名
     * @return 代码生成业务表领域模型
     */
    GenTable selectGenTableByName(String tableName);

    /**
     * 新增代码生成业务表。
     *
     * @param genTable 代码生成业务表
     * @return 影响行数
     */
    int insertGenTable(GenTable genTable);

    /**
     * 全量更新代码生成业务表。
     *
     * @param genTable 代码生成业务表
     * @return 影响行数
     */
    int updateGenTable(GenTable genTable);

    /**
     * 按 ID 集合批量删除代码生成业务表。
     *
     * @param ids 主键数组
     * @return 影响行数
     */
    int deleteGenTableByIds(Long[] ids);

    /**
     * 执行建表 SQL（代码生成动态建表）。
     *
     * @param sql 建表 SQL
     * @return 影响行数
     */
    int createTable(String sql);
}
