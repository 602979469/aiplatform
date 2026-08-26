package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.AuthUser;
import com.jakt.aiplatform.core.model.param.AuthUserQueryParam;
import com.jakt.aiplatform.common.framework.result.PageResult;

import java.util.List;

/**
 * 用户表仓储：封装 Mapper，对外只暴露领域模型。当前阶段单表操作不引入事务。
 */
public interface AuthUserRepository {

    /**
     * 按主键查询。
     *
     * @param id 主键
     * @return 用户表领域模型
     */
    AuthUser findById(Long id);

    /**
     * 按登录账号查询。
     *
     * @param username 登录账号
     * @return 用户表领域模型；不存在返回 null
     */
    AuthUser findByUsername(String username);

    /**
     * 分页查询。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<AuthUser> findPage(AuthUserQueryParam query);

    /**
     * 列表查询。
     *
     * @param query 查询参数
     * @return 用户表列表
     */
    List<AuthUser> findList(AuthUserQueryParam query);

    /**
     * 按条件查询单条：基于 {@code findList} 的结果集判断，不新增 Mapper 方法。
     *
     * @param query 查询参数
     * @return 用户表领域模型；未查询到返回 null，结果多于 1 条抛「查询结果不唯一」
     */
    AuthUser findOne(AuthUserQueryParam query);

    /**
     * 新增。
     *
     * @param authUser 用户表
     * @return 新增后的用户表；主键已回填到入参，返回同一对象
     */
    AuthUser insert(AuthUser authUser);

    /**
     * 更新。
     *
     * @param authUser 用户表
     */
    int update(AuthUser authUser);

    /**
     * 按条件更新：只更新传入的非空字段（部分更新）。
     * 注意：无法把字段更新为 null，需要置 null 请用 {@link #update}；create_time/update_time 由数据库自动维护。
     *
     * @param authUser 用户表（至少含主键）
     */
    int updateByCondition(AuthUser authUser);

    /**
     * 按主键删除。
     *
     * @param id 主键
     */
    int deleteById(Long id);

    /**
     * 重建用户角色绑定（先删后插）。
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表（空列表表示清空）
     */
    void replaceRoles(Long userId, List<Long> roleIds);

    /**
     * 查询用户角色ID列表。
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> findRoleIdsByUserId(Long userId);

    /**
     * 清空用户角色绑定（用户删除时调用）。
     *
     * @param userId 用户ID
     */
    void clearUserRoles(Long userId);
}
