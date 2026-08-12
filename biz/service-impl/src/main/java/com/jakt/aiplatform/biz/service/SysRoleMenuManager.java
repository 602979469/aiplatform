package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.SysRoleMenu;
import com.jakt.aiplatform.core.model.param.SysRoleMenuQueryParam;
import com.jakt.aiplatform.core.model.result.PageResult;

import java.util.List;

/**
 * 角色菜单关联管理类接口定义
 * 
 */
public interface SysRoleMenuManager {

    /**
     * 创建角色菜单关联
     *
     * @param sysRoleMenu 角色菜单关联
     * @return 创建成功后的角色菜单关联
     */
    SysRoleMenu createSysRoleMenu(SysRoleMenu sysRoleMenu);

    /**
     * 按 ID 查询角色菜单关联
     *
     * @param id 角色菜单关联 ID
     * @return 角色菜单关联
     */
    SysRoleMenu getSysRoleMenu(Long id);

    /**
     * 分页查询角色菜单关联
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<SysRoleMenu> pageSysRoleMenus(SysRoleMenuQueryParam query);

    /**
     * 列表查询角色菜单关联
     *
     * @param query 查询参数
     * @return 角色菜单关联列表
     */
    List<SysRoleMenu> listSysRoleMenus(SysRoleMenuQueryParam query);

    /**
     * 更新角色菜单关联（全量）。
     *
     * @param sysRoleMenu 角色菜单关联（含主键）
     */
    void updateSysRoleMenu(SysRoleMenu sysRoleMenu);

    /**
     * 按条件更新角色菜单关联（只更新传入的非空字段）。
     *
     * @param sysRoleMenu 角色菜单关联（至少含主键）
     */
    void updateByCondition(SysRoleMenu sysRoleMenu);

    /**
     * 删除角色菜单关联。
     *
     * @param id 角色菜单关联 ID
     */
    void deleteSysRoleMenu(Long id);
}
