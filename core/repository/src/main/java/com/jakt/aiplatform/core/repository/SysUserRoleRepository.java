package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysUserRole;

import java.util.List;

/**
 * 用户角色关联仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysUserRoleRepository {

    /**
     * 按用户ID查询关联列表。
     *
     * @param userId 用户ID
     * @return 用户角色关联列表
     */
    List<SysUserRole> selectUserRoleByUserId(Long userId);

    /**
     * 按用户ID删除关联。
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteUserRoleByUserId(Long userId);

    /**
     * 按用户ID集合批量删除关联。
     *
     * @param ids 用户ID数组
     * @return 影响行数
     */
    int deleteUserRole(Long[] ids);

    /**
     * 按角色ID统计关联数量。
     *
     * @param roleId 角色ID
     * @return 关联数量
     */
    int countUserRoleByRoleId(Long roleId);

    /**
     * 批量新增用户角色关联。
     *
     * @param userRoleList 用户角色关联列表
     * @return 影响行数
     */
    int batchUserRole(List<SysUserRole> userRoleList);

    /**
     * 按用户ID与角色ID删除单条关联。
     *
     * @param userRole 用户角色关联（userId + roleId）
     * @return 影响行数
     */
    int deleteUserRoleInfo(SysUserRole userRole);

    /**
     * 按角色ID与用户ID集合批量删除关联。
     *
     * @param roleId  角色ID
     * @param userIds 用户ID数组
     * @return 影响行数
     */
    int deleteUserRoleInfos(Long roleId, Long[] userIds);
}
