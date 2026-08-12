package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysRoleDept;
import com.jakt.aiplatform.core.model.param.SysRoleDeptQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 角色部门关联仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysRoleDeptRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 角色部门关联领域模型
     */
    SysRoleDept findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysRoleDept> findPage(SysRoleDeptQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 角色部门关联列表
     */
    List<SysRoleDept> findList(SysRoleDeptQueryParam query);

    /**
     * 新增。
     *
     * @param sysRoleDept 角色部门关联
     * @return 新增后的角色部门关联（主键已回填）
     */
    SysRoleDept insert(SysRoleDept sysRoleDept);

    /**
     * 更新。
     *
     * @param sysRoleDept 角色部门关联
     */
    void update(SysRoleDept sysRoleDept);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysRoleDept 角色部门关联（至少含主键）
     */
    void updateByCondition(SysRoleDept sysRoleDept);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
