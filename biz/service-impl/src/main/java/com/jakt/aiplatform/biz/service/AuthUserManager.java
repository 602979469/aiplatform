package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.AuthUser;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.param.AuthUserQueryParam;
import com.jakt.aiplatform.common.framework.result.PageResult;

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
     * 修改当前用户密码（校验原密码）。
     *
     * @param userId      用户ID
     * @param oldPassword 原密码明文
     * @param newPassword 新密码明文
     */
    void updatePassword(Long userId, String oldPassword, String newPassword);

    /**
     * 更新当前用户头像（头像文件走文件管理模块，user_avatar 命名空间）。
     *
     * @param userId           用户ID
     * @param imageBytes       图片字节
     * @param originalFilename 原始文件名（用于取扩展名）
     * @return 头像访问路径（/api/file/avatar/{id}）
     */
    String updateAvatar(Long userId, byte[] imageBytes, String originalFilename);

    /**
     * 修改当前用户个人资料（昵称/邮箱，仅更新非空字段）。
     *
     * @param userId   用户ID
     * @param nickname 昵称
     * @param email    邮箱
     */
    void updateProfile(Long userId, String nickname, String email);

    /**
     * 分配用户角色。
     *
     * @param userId  用户ID
     * @param roleIds 角色ID列表
     */
    void assignUserRoles(Long userId, List<Long> roleIds);

    /**
     * 查询用户已分配角色ID（编辑回显）。
     *
     * @param userId 用户ID
     * @return 角色ID列表
     */
    List<Long> getUserRoleIds(Long userId);

    /**
     * 删除用户。
     *
     * @param userId 用户ID
     */
    void deleteUser(Long userId);
}
