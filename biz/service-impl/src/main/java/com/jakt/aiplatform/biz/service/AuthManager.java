package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.dto.AuthLoginInfo;
import com.jakt.aiplatform.core.model.dto.AuthUserInfo;

/**
 * 认证管理：登录/注册/登出/当前用户信息/菜单路由编排。
 */
public interface AuthManager {

    /**
     * 登录。
     *
     * @param username 登录账号
     * @param password 密码明文
     * @return 登录结果
     */
    AuthLoginInfo login(String username, String password);

    /**
     * 注册（成功后自动登录）。
     *
     * @param username 登录账号
     * @param password 密码明文
     * @param nickname 昵称
     * @param email    邮箱
     * @return 登录结果
     */
    AuthLoginInfo register(String username, String password, String nickname, String email);

    /**
     * 登出。
     */
    void logout();

    /**
     * 当前登录用户信息（含角色与权限码）。
     *
     * @return 用户信息
     */
    AuthUserInfo getInfo();

}
