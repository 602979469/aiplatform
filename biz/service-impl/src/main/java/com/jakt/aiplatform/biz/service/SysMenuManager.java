package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysMenu;
import com.jakt.aiplatform.core.model.param.SysMenuQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 菜单管理类接口定义
 * 
 */
public interface SysMenuManager {

    /**
     * 创建菜单
     *
     * @param sysMenu 菜单
     * @return 创建成功后的菜单
     */
    SysMenu createSysMenu(SysMenu sysMenu);

    /**
     * 按 ID 查询菜单
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
    PageResult<SysMenu> pageSysMenus(SysMenuQueryParam query);

    /**
     * 列表查询菜单
     *
     * @param query 查询参数
     * @return 菜单列表
     */
    List<SysMenu> listSysMenus(SysMenuQueryParam query);

    /**
     * 更新菜单（全量）。
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
     * 删除菜单。
     *
     * @param id 菜单 ID
     */
    void deleteSysMenu(Long id);
}
