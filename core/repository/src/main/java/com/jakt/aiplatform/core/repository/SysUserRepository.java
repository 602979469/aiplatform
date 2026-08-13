package com.jakt.aiplatform.core.repository;

import com.jakt.aiplatform.core.model.domain.SysUser;

import java.util.List;

/**
 * 用户仓储（RuoYi 方法 1:1 还原）：封装 Mapper，对外只暴露领域模型。
 */
public interface SysUserRepository {

    /**
     * 按登录账号查询用户。
     *
     * @param loginName 登录账号
     * @return 用户领域模型
     */
    SysUser selectUserByLoginName(String loginName);

    /**
     * 按手机号码查询用户。
     *
     * @param phonenumber 手机号码
     * @return 用户领域模型
     */
    SysUser selectUserByPhoneNumber(String phonenumber);

    /**
     * 按邮箱查询用户。
     *
     * @param email 邮箱
     * @return 用户领域模型
     */
    SysUser selectUserByEmail(String email);

    /**
     * 查询用户列表（join 部门）。
     *
     * @param user 查询条件（实体即条件）
     * @return 用户列表
     */
    List<SysUser> selectUserList(SysUser user);

    /**
     * 查询已分配指定角色的用户列表。
     *
     * @param user 查询条件（roleId 必填）
     * @return 用户列表
     */
    List<SysUser> selectAllocatedList(SysUser user);

    /**
     * 查询未分配指定角色的用户列表。
     *
     * @param user 查询条件（roleId 必填）
     * @return 用户列表
     */
    List<SysUser> selectUnallocatedList(SysUser user);

    /**
     * 校验登录账号唯一。
     *
     * @param user 用户（含 userId 用于排除自身）
     * @return 是否唯一
     */
    boolean checkLoginNameUnique(SysUser user);

    /**
     * 校验手机号码唯一。
     *
     * @param user 用户
     * @return 是否唯一
     */
    boolean checkPhoneUnique(SysUser user);

    /**
     * 校验邮箱唯一。
     *
     * @param user 用户
     * @return 是否唯一
     */
    boolean checkEmailUnique(SysUser user);

    /**
     * 按用户ID查询用户。
     *
     * @param userId 用户ID
     * @return 用户领域模型
     */
    SysUser selectUserById(Long userId);

    /**
     * 按用户ID删除。
     *
     * @param userId 用户ID
     * @return 影响行数
     */
    int deleteUserById(Long userId);

    /**
     * 按 ID 集合批量删除。
     *
     * @param ids 用户ID集合（逗号分隔）
     * @return 影响行数
     */
    int deleteUserByIds(String ids);

    /**
     * 更新用户头像。
     *
     * @param userId 用户ID
     * @param avatar 头像路径
     * @return 影响行数
     */
    int updateUserAvatar(Long userId, String avatar);

    /**
     * 重置密码。
     *
     * @param user 用户（userId/password/salt/pwdUpdateDate）
     * @return 影响行数
     */
    int resetUserPwd(SysUser user);

    /**
     * 修改用户状态。
     *
     * @param user 用户（userId/status）
     * @return 影响行数
     */
    int updateUserStatus(SysUser user);

    /**
     * 更新登录信息。
     *
     * @param user 用户（userId/loginIp/loginDate）
     * @return 影响行数
     */
    int updateLoginInfo(SysUser user);

    /**
     * 全量更新用户。
     *
     * @param user 用户
     * @return 影响行数
     */
    int updateUser(SysUser user);

    /**
     * 新增用户。
     *
     * @param user 用户
     * @return 影响行数
     */
    int insertUser(SysUser user);
}
