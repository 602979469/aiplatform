package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysRole;

import java.util.List;

/**
 * 角色仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysRoleRepository {

    /**
     * 按条件查询角色列表。
     *
     * @param role 查询条件（实体即条件）
     * @return 角色列表
     */
    List<SysRole> selectRoleList(SysRole role);

    /**
     * 按用户ID查询角色列表。
     *
     * @param userId 用户ID
     * @return 角色列表
     */
    List<SysRole> selectRolesByUserId(Long userId);

    /**
     * 按主键查询角色。
     *
     * @param roleId 角色ID
     * @return 角色领域模型
     */
    SysRole selectRoleById(Long roleId);

    /**
     * 按主键删除角色（逻辑删除）。
     *
     * @param roleId 角色ID
     * @return 影响行数
     */
    int deleteRoleById(Long roleId);

    /**
     * 按 ID 集合批量删除角色（逻辑删除）。
     *
     * @param ids 角色ID集合（逗号分隔）
     * @return 影响行数
     */
    int deleteRoleByIds(String ids);

    /**
     * 全量更新角色。
     *
     * @param role 角色
     * @return 影响行数
     */
    int updateRole(SysRole role);

    /**
     * 新增角色。
     *
     * @param role 角色
     * @return 影响行数
     */
    int insertRole(SysRole role);

    /**
     * 校验角色名称唯一。
     *
     * @param role 角色（含 roleId 用于排除自身）
     * @return 是否唯一
     */
    boolean checkRoleNameUnique(SysRole role);

    /**
     * 校验角色权限字符串唯一。
     *
     * @param role 角色（含 roleId 用于排除自身）
     * @return 是否唯一
     */
    boolean checkRoleKeyUnique(SysRole role);
}
