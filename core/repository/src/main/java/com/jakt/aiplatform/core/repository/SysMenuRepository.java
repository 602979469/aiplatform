package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysMenu;
import com.jakt.aiplatform.core.model.param.SysMenuQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 菜单仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface SysMenuRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 菜单领域模型
     */
    SysMenu findById(Long id);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysMenu> findPage(SysMenuQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 菜单列表
     */
    List<SysMenu> findList(SysMenuQueryParam query);

    /**
     * 新增。
     *
     * @param sysMenu 菜单
     * @return 新增后的菜单（主键已回填）
     */
    SysMenu insert(SysMenu sysMenu);

    /**
     * 更新。
     *
     * @param sysMenu 菜单
     */
    void update(SysMenu sysMenu);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param sysMenu 菜单（至少含主键）
     */
    void updateByCondition(SysMenu sysMenu);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    void deleteById(Long id);
}
