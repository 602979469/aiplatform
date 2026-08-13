package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysMenu;

import java.util.List;

/**
 * 菜单仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysMenuRepository {

    /**
     * 查询全部菜单。
     *
     * @return 菜单列表
     */
    List<SysMenu> selectMenuAll();

    /**
     * 按用户ID查询全部菜单。
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenuAllByUserId(Long userId);

    /**
     * 查询正常状态菜单。
     *
     * @return 菜单列表
     */
    List<SysMenu> selectMenuNormalAll();

    /**
     * 按用户ID查询可用菜单。
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<SysMenu> selectMenusByUserId(Long userId);

    /**
     * 按用户ID查询权限标识集合。
     *
     * @param userId 用户ID
     * @return 权限标识集合
     */
    List<String> selectPermsByUserId(Long userId);

    /**
     * 按角色ID查询权限标识集合。
     *
     * @param roleId 角色ID
     * @return 权限标识集合
     */
    List<String> selectPermsByRoleId(Long roleId);

    /**
     * 按角色ID查询菜单树标识集合。
     *
     * @param roleId 角色ID
     * @return 菜单树标识集合
     */
    List<String> selectMenuTree(Long roleId);

    /**
     * 按条件查询菜单列表。
     *
     * @param menu 查询条件（实体即条件）
     * @return 菜单列表
     */
    List<SysMenu> selectMenuList(SysMenu menu);

    /**
     * 按用户ID与条件查询菜单列表。
     *
     * @param menu 查询条件（params.userId 必填）
     * @return 菜单列表
     */
    List<SysMenu> selectMenuListByUserId(SysMenu menu);

    /**
     * 按菜单ID删除菜单及其子菜单。
     *
     * @param menuId 菜单ID
     * @return 影响行数
     */
    int deleteMenuById(Long menuId);

    /**
     * 按菜单ID查询菜单（含父菜单名称）。
     *
     * @param menuId 菜单ID
     * @return 菜单领域模型
     */
    SysMenu selectMenuById(Long menuId);

    /**
     * 按父菜单ID统计子菜单数量。
     *
     * @param parentId 父菜单ID
     * @return 子菜单数量
     */
    int selectCountMenuByParentId(Long parentId);

    /**
     * 新增菜单。
     *
     * @param menu 菜单
     * @return 影响行数
     */
    int insertMenu(SysMenu menu);

    /**
     * 全量更新菜单。
     *
     * @param menu 菜单
     * @return 影响行数
     */
    int updateMenu(SysMenu menu);

    /**
     * 仅更新菜单排序。
     *
     * @param menu 菜单（menuId + orderNum）
     * @return 影响行数
     */
    int updateMenuSort(SysMenu menu);

    /**
     * 校验菜单名称在同级下唯一。
     *
     * @param menu 菜单（menuName + parentId，menuId 用于排除自身）
     * @return 是否唯一
     */
    boolean checkMenuNameUnique(SysMenu menu);
}
