package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysUserRole;
import com.jakt.aiplatform.core.model.param.SysUserRoleQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 用户角色关联仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysUserRoleRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 用户角色关联领域模型
     */
    SysUserRole findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysUserRole> findPage(SysUserRoleQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 用户角色关联列表
     */
    List<SysUserRole> findList(SysUserRoleQueryParam query);

    /**
     * 新增。
     *
     * @param sysUserRole 用户角色关联
     * @return 新增后的用户角色关联（主键已回填）
     */
    SysUserRole insert(SysUserRole sysUserRole);

    /**
     * 更新。
     *
     * @param sysUserRole 用户角色关联
     */
    void update(SysUserRole sysUserRole);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysUserRole 用户角色关联（至少含主键）
     */
    void updateByCondition(SysUserRole sysUserRole);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
