package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.GenTableColumn;
import com.jakt.aiplatform.core.model.param.GenTableColumnQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 代码生成字段仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface GenTableColumnRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 代码生成字段领域模型
     */
    GenTableColumn findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<GenTableColumn> findPage(GenTableColumnQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 代码生成字段列表
     */
    List<GenTableColumn> findList(GenTableColumnQueryParam query);

    /**
     * 新增。
     *
     * @param genTableColumn 代码生成字段
     * @return 新增后的代码生成字段（主键已回填）
     */
    GenTableColumn insert(GenTableColumn genTableColumn);

    /**
     * 更新。
     *
     * @param genTableColumn 代码生成字段
     */
    void update(GenTableColumn genTableColumn);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param genTableColumn 代码生成字段（至少含主键）
     */
    void updateByCondition(GenTableColumn genTableColumn);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
