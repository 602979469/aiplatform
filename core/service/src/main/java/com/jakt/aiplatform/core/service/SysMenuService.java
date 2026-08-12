package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.SysMenu;
import com.jakt.aiplatform.core.model.param.SysMenuQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 菜单领域服务
 *
 * 实现类为 SysMenuServiceImpl（core.service.impl 包）。
 */
public interface SysMenuService {

    /**
     * 创建菜单
     *
     * @param sysMenu 菜单
     * @return 创建后的菜单（主键已回填）
     */
    SysMenu createSysMenu(SysMenu sysMenu);

    /**
     * 更新菜单（全量）
     *
     * @param sysMenu 菜单（含主键）
     */
    void updateSysMenu(SysMenu sysMenu);

    /**
     * 按条件更新菜单（只更新传入的非空字段）。
     *
     * @param sysMenu 菜单（至少含主键）
     */
    void updateByCondition(SysMenu sysMenu);

    /**
     * 删除菜单
     *
     * @param id 菜单 ID
     */
    void deleteSysMenu(Long id);

    /**
     * 按 ID 获取菜单
     *
     * @param id 菜单 ID
     * @return 菜单
     */
    SysMenu getSysMenu(Long id);

    /**
     * 分页查询菜单
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysMenu> findPage(SysMenuQueryParam query);

    /**
     * 列表查询菜单
     *
     * @param query 查询参数
     * @return 菜单列表
     */
    List<SysMenu> findList(SysMenuQueryParam query);
}
