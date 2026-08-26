package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.dto.AuthOnlineInfo;
import com.jakt.aiplatform.core.model.param.AuthOnlineQueryParam;
import com.jakt.aiplatform.common.framework.result.PageResult;

/**
 * 在线会话管理用例编排。
 */
public interface AuthOnlineManager {

    /**
     * 分页查询在线用户。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<AuthOnlineInfo> listOnline(AuthOnlineQueryParam query);

    /**
     * 踢人下线。
     *
     * @param userId 用户ID
     */
    void kickout(Long userId);

    /**
     * 强制注销。
     *
     * @param userId 用户ID
     */
    void forceLogout(Long userId);

    /**
     * 封禁并立即掉线。
     *
     * @param userId  用户ID
     * @param seconds 封禁时长（秒）
     */
    void disable(Long userId, long seconds);

    /**
     * 解封。
     *
     * @param userId 用户ID
     */
    void untieDisable(Long userId);
}
