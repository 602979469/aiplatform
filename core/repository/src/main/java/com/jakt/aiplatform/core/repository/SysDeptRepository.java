package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysDept;
import com.jakt.aiplatform.core.model.param.SysDeptQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 部门仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysDeptRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 部门领域模型
     */
    SysDept findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysDept> findPage(SysDeptQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 部门列表
     */
    List<SysDept> findList(SysDeptQueryParam query);

    /**
     * 新增。
     *
     * @param sysDept 部门
     * @return 新增后的部门（主键已回填）
     */
    SysDept insert(SysDept sysDept);

    /**
     * 更新。
     *
     * @param sysDept 部门
     */
    void update(SysDept sysDept);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysDept 部门（至少含主键）
     */
    void updateByCondition(SysDept sysDept);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
