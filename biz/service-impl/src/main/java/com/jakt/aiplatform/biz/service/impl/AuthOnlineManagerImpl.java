package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.AuthOnlineManager;
import com.jakt.aiplatform.core.model.dto.AuthOnlineInfo;
import com.jakt.aiplatform.core.model.param.AuthOnlineQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;
import com.jakt.aiplatform.core.service.AuthSessionService;
import org.springframework.stereotype.Service;

/**
 * 在线会话管理用例编排实现：委托在线会话领域服务。
 */
@Service
public class AuthOnlineManagerImpl implements AuthOnlineManager {

    private final AuthSessionService authSessionService;

    public AuthOnlineManagerImpl(AuthSessionService authSessionService) {
        this.authSessionService = authSessionService;
    }

    @Override
    public PageResult<AuthOnlineInfo> listOnline(AuthOnlineQueryParam query) {
        return authSessionService.listOnline(query);
    }

    @Override
    public void kickout(Long userId) {
        authSessionService.kickout(userId);
    }

    @Override
    public void forceLogout(Long userId) {
        authSessionService.forceLogout(userId);
    }

    @Override
    public void disable(Long userId, long seconds) {
        authSessionService.disable(userId, seconds);
    }

    @Override
    public void untieDisable(Long userId) {
        authSessionService.untieDisable(userId);
    }
}
