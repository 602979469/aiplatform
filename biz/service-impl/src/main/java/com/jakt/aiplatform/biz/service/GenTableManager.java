package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.GenTable;
import com.jakt.aiplatform.core.model.param.GenTableQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 代码生成管理类接口定义
 * 
 */
public interface GenTableManager {

    /**
     * 创建代码生成
     *
     * @param genTable 代码生成
     * @return 创建成功后的代码生成
     */
    GenTable createGenTable(GenTable genTable);

    /**
     * 按 ID 查询代码生成
     *
     * @param id 代码生成 ID
     * @return 代码生成
     */
    GenTable getGenTable(Long id);

    /**
     * 分页查询代码生成
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<GenTable> pageGenTables(GenTableQueryParam query);

    /**
     * 列表查询代码生成
     *
     * @param query 查询参数
     * @return 代码生成列表
     */
    List<GenTable> listGenTables(GenTableQueryParam query);

    /**
     * 更新代码生成（全量）。
     *
     * @param genTable 代码生成（含主键）
     */
    void updateGenTable(GenTable genTable);

    /**
     * 按条件更新代码生成（只更新传入的非空字段）。
     *
     * @param genTable 代码生成（至少含主键）
     */
    void updateByCondition(GenTable genTable);

    /**
     * 删除代码生成。
     *
     * @param id 代码生成 ID
     */
    void deleteGenTable(Long id);
}
