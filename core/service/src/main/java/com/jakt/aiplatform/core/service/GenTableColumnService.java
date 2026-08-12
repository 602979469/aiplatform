package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.GenTableColumn;
import com.jakt.aiplatform.core.model.param.GenTableColumnQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 代码生成字段领域服务
 *
 * 实现类为 GenTableColumnServiceImpl（core.service.impl 包）。
 */
public interface GenTableColumnService {

    /**
     * 创建代码生成字段
     *
     * @param genTableColumn 代码生成字段
     * @return 创建后的代码生成字段（主键已回填）
     */
    GenTableColumn createGenTableColumn(GenTableColumn genTableColumn);

    /**
     * 更新代码生成字段（全量）
     *
     * @param genTableColumn 代码生成字段（含主键）
     */
    void updateGenTableColumn(GenTableColumn genTableColumn);

    /**
     * 按条件更新代码生成字段（只更新传入的非空字段）。
     *
     * @param genTableColumn 代码生成字段（至少含主键）
     */
    void updateByCondition(GenTableColumn genTableColumn);

    /**
     * 删除代码生成字段
     *
     * @param id 代码生成字段 ID
     */
    void deleteGenTableColumn(Long id);

    /**
     * 按 ID 获取代码生成字段
     *
     * @param id 代码生成字段 ID
     * @return 代码生成字段
     */
    GenTableColumn getGenTableColumn(Long id);

    /**
     * 分页查询代码生成字段
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<GenTableColumn> findPage(GenTableColumnQueryParam query);

    /**
     * 列表查询代码生成字段
     *
     * @param query 查询参数
     * @return 代码生成字段列表
     */
    List<GenTableColumn> findList(GenTableColumnQueryParam query);
}
