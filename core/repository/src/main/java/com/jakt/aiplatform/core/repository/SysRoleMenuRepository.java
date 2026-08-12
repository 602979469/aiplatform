package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysRoleMenu;
import com.jakt.aiplatform.core.model.param.SysRoleMenuQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 角色菜单关联仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysRoleMenuRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 角色菜单关联领域模型
     */
    SysRoleMenu findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysRoleMenu> findPage(SysRoleMenuQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 角色菜单关联列表
     */
    List<SysRoleMenu> findList(SysRoleMenuQueryParam query);

    /**
     * 新增。
     *
     * @param sysRoleMenu 角色菜单关联
     * @return 新增后的角色菜单关联（主键已回填）
     */
    SysRoleMenu insert(SysRoleMenu sysRoleMenu);

    /**
     * 更新。
     *
     * @param sysRoleMenu 角色菜单关联
     */
    void update(SysRoleMenu sysRoleMenu);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysRoleMenu 角色菜单关联（至少含主键）
     */
    void updateByCondition(SysRoleMenu sysRoleMenu);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
