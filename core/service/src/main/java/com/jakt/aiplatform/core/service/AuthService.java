package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.dto.AuthLoginInfo;
import com.jakt.aiplatform.core.model.domain.AuthUser;

/**
 * 认证领域服务：注册、登录、登出。
 */
public interface AuthService {

    /**
     * 登录：校验账号密码后签发 token。
     *
     * @param username 登录账号
     * @param password 密码明文
     * @return 登录结果
     */
    AuthLoginInfo login(String username, String password);

    /**
     * 注册：创建用户并绑定默认角色，成功后自动登录。
     *
     * @param username 登录账号
     * @param password 密码明文
     * @param nickname 昵称
     * @param email    邮箱（可为空）
     * @return 登录结果
     */
    AuthLoginInfo register(String username, String password, String nickname, String email);

    /**
     * 登出当前会话。
     */
    void logout();

    /**
     * 获取当前登录用户（从 UserContext 取 userId）。
     *
     * @return 当前用户
     */
    AuthUser getCurrentUser();
}
