package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.AuthRole;
import com.jakt.aiplatform.core.model.param.AuthRoleQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;

import java.util.List;

/**
 * 角色表仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface AuthRoleRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 角色表领域模型
     */
    AuthRole findById(Long id);

    /**
     * 查询用户有效角色标识集合。
     *
     * @param userId 用户ID
     * @return 角色标识列表
     */
    List<String> findRoleKeysByUserId(Long userId);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<AuthRole> findPage(AuthRoleQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 角色表列表
     */
    List<AuthRole> findList(AuthRoleQueryParam query);

    /**
     * 按条件查询单条：基于 {@code findList} 的结果集判断，不新增 Mapper 方法。
     *
     * @param query 查询参数
     * @return 角色表领域模型；未查询到返回 null，结果多于 1 条抛「查询结果不唯一」
     */
    AuthRole findOne(AuthRoleQueryParam query);

    /**
     * 新增。
     *
     * @param authRole 角色表
     * @return 新增后的角色表；主键已回填到入参，返回同一对象
     */
    AuthRole insert(AuthRole authRole);

    /**
     * 更新。
     *
     * @param authRole 角色表
     */
    int update(AuthRole authRole);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param authRole 角色表（至少含主键）
     */
    int updateByCondition(AuthRole authRole);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    int deleteById(Long id);

    /**
     * 查询角色已分配菜单ID列表。
     *
     * @param roleId 角色ID
     * @return 菜单ID列表
     */
    List<Long> findMenuIdsByRoleId(Long roleId);

    /**
     * 重建角色菜单绑定（先删后插）。
     *
     * @param roleId  角色ID
     * @param menuIds 菜单ID列表（空列表表示清空）
     */
    void replaceRoleMenus(Long roleId, List<Long> menuIds);

    /**
     * 清空角色关联（角色删除时调用：角色菜单 + 用户角色）。
     *
     * @param roleId 角色ID
     */
    void clearRoleBindings(Long roleId);
}
