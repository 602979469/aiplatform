package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.AuthUser;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.param.AuthUserQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;

import java.util.List;

/**
 * 用户管理用例编排：用户 CRUD、启停、重置密码、角色分配。
 */
public interface AuthUserManager {

    /**
     * 分页查询用户。
     *
     * @param query 查询参数
     * @return 用户分页
     */
    PageResult<AuthUser> pageUser(AuthUserQueryParam query);

    /**
     * 查询用户详情。
     *
     * @param userId 用户ID
     * @return 用户
     */
    AuthUser getUser(Long userId);

    /**
     * 新增用户并绑定角色。
     *
     * @param user    用户（含明文密码）
     * @param roleIds 角色ID列表
     * @return 新增后的用户
     */
    AuthUser createUser(AuthUser user, List<Long> roleIds);

    /**
     * 修改用户资料（不含密码）。
     *
     * @param user 用户（含 userId）
     */
    void updateUser(AuthUser user);

    /**
     * 启停用户。
     *
     * @param userId 用户ID
     * @param status 状态
     */
    void changeUserStatus(Long userId, EnableStatusEnum status);

    /**
     * 重置密码。
     *
     * @param userId   用户ID
     * @param password 新密码明文
     */
    void resetPassword(Long userId, String password);

    /**
     * 分配用户角色。
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void assignUserRoles(Long userId, List<Long> roleIds);

    /**
     * 删除用户。
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);
}
