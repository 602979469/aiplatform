package com.jakt.aiplatform.common.dal.mapper;

import com.jakt.aiplatform.common.dal.dataobject.AuthRoleMenuDO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

/**
 * 角色菜单关联表 Mapper。
 */
@Mapper
public interface AuthRoleMenuMapper {

    /**
     * 批量绑定角色菜单。
     *
     * @param list 关联列表
     * @return 受影响行数
     */
    int batchInsert(@Param("list") List<AuthRoleMenuDO> list);

    /**
     * 删除角色全部菜单。
     *
     * @param roleId 角色ID
     * @return 受影响行数
     */
    int deleteByRoleId(Long roleId);

    /**
     * 删除菜单下全部角色（菜单删除时清理）。
     *
     * @param menuId 菜单ID
     * @return 受影响行数
     */
    int deleteByMenuId(Long menuId);

    /**
     * 查询角色菜单ID列表。
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> selectMenuIdsByRoleId(Long roleId);
}
