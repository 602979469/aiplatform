package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysDictType;
import com.jakt.aiplatform.core.model.param.SysDictTypeQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 字典类型仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysDictTypeRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 字典类型领域模型
     */
    SysDictType findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysDictType> findPage(SysDictTypeQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 字典类型列表
     */
    List<SysDictType> findList(SysDictTypeQueryParam query);

    /**
     * 新增。
     *
     * @param sysDictType 字典类型
     * @return 新增后的字典类型（主键已回填）
     */
    SysDictType insert(SysDictType sysDictType);

    /**
     * 更新。
     *
     * @param sysDictType 字典类型
     */
    void update(SysDictType sysDictType);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysDictType 字典类型（至少含主键）
     */
    void updateByCondition(SysDictType sysDictType);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
