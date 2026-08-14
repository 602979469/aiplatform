package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.dto.AuthOnlineInfo;
import com.jakt.aiplatform.core.model.param.AuthOnlineQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;

/**
 * 在线会话领域服务：在线列表、踢人、强制注销、封禁。
 */
public interface AuthSessionService {

    /**
     * 分页查询在线用户。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<AuthOnlineInfo> listOnline(AuthOnlineQueryParam query);

    /**
     * 踢人下线：token 打被踢标记，再次访问提示已下线。
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
     * 封禁账号并立即掉线。
     *
     * @param userId  用户ID
     * @param seconds 封禁时长（秒）
     */
    void disable(Long userId, long seconds);

    /**
     * 解封账号。
     *
     * @param userId 用户ID
     */
    void untieDisable(Long userId);
}
