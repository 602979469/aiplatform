package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.GenTable;
import com.jakt.aiplatform.core.model.param.GenTableQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 代码生成仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface GenTableRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 代码生成领域模型
     */
    GenTable findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<GenTable> findPage(GenTableQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 代码生成列表
     */
    List<GenTable> findList(GenTableQueryParam query);

    /**
     * 新增。
     *
     * @param genTable 代码生成
     * @return 新增后的代码生成（主键已回填）
     */
    GenTable insert(GenTable genTable);

    /**
     * 更新。
     *
     * @param genTable 代码生成
     */
    void update(GenTable genTable);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param genTable 代码生成（至少含主键）
     */
    void updateByCondition(GenTable genTable);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
