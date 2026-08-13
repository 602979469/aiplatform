package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysRoleMenu;

import java.util.List;

/**
 * 角色菜单关联仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysRoleMenuRepository {

    /**
     * 按角色ID删除关联。
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteRoleMenuByRoleId(Long roleId);

    /**
     * 按菜单ID统计关联数量。
     *
     * @param menuId 菜单ID
     * @return 关联数量
     */
    int selectCountRoleMenuByMenuId(Long menuId);

    /**
     * 按角色ID集合批量删除关联。
     *
     * @param ids 角色ID数组
     * @return 影响行数
     */
    int deleteRoleMenu(Long[] ids);

    /**
     * 批量新增角色菜单关联。
     *
     * @param roleMenuList 角色菜单关联列表
     * @return 影响行数
     */
    int batchRoleMenu(List<SysRoleMenu> roleMenuList);
}
