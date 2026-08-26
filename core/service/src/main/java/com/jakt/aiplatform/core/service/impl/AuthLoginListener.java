package com.jakt.aiplatform.core.service.impl;

import cn.dev33.satoken.listener.SaTokenListenerForSimple;
import cn.dev33.satoken.stp.parameter.SaLoginParameter;
import cn.hutool.core.convert.Convert;
import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.util.tools.ClientInfoUtil;
import com.jakt.aiplatform.core.model.constant.AiPlatformConstant;
import com.jakt.aiplatform.core.model.domain.AuthLoginLog;
import com.jakt.aiplatform.core.model.domain.AuthUser;
import com.jakt.aiplatform.core.model.dto.AuthOnlineSnapshot;
import com.jakt.aiplatform.common.framework.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.enums.LoginLogStatusEnum;
import com.jakt.aiplatform.common.framework.tools.LoggerUtil;
import com.jakt.aiplatform.core.repository.AuthLoginLogRepository;
import com.jakt.aiplatform.core.repository.AuthOnlineRepository;
import com.jakt.aiplatform.core.repository.AuthUserRepository;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * Sa-Token 全局事件监听器：登录写日志 + 在线快照；注销/踢出/顶出写日志并清理快照。
 * 事件处理全部 try-catch，异常不影响 Sa-Token 主流程。
 */
@Component
public class AuthLoginListener extends SaTokenListenerForSimple {

    private final AuthLoginLogRepository authLoginLogRepository;

    private final AuthOnlineRepository authOnlineRepository;

    private final AuthUserRepository authUserRepository;

    public AuthLoginListener(AuthLoginLogRepository authLoginLogRepository,
                             AuthOnlineRepository authOnlineRepository,
                             AuthUserRepository authUserRepository) {
        this.authLoginLogRepository = authLoginLogRepository;
        this.authOnlineRepository = authOnlineRepository;
        this.authUserRepository = authUserRepository;
    }

    @Override
    public void doLogin(String loginType, Object loginId, String tokenValue, SaLoginParameter loginParameter) {
        runSafely(() -> {
            AuthUser user = authUserRepository.findById(Convert.toLong(loginId));
            if (user == null) {
                return;
            }
            writeLog(user, LoginLogStatusEnum.SUCCESS, "登录成功");
            writeSnapshot(user, tokenValue);
        });
    }

    @Override
    public void doLogout(String loginType, Object loginId, String tokenValue) {
        runSafely(() -> {
            writeLog(findUser(loginId), LoginLogStatusEnum.LOGOUT, "注销登录");
            removeSnapshot(tokenValue);
        });
    }

    @Override
    public void doKickout(String loginType, Object loginId, String tokenValue) {
        runSafely(() -> {
            writeLog(findUser(loginId), LoginLogStatusEnum.KICKOUT, "被踢下线");
            removeSnapshot(tokenValue);
        });
    }

    @Override
    public void doReplaced(String loginType, Object loginId, String tokenValue) {
        runSafely(() -> {
            writeLog(findUser(loginId), LoginLogStatusEnum.REPLACED, "被顶下线");
            removeSnapshot(tokenValue);
        });
    }

    /** 按 loginId 查询用户（事件场景可能拿不到用户，返回 null）。 */
    private AuthUser findUser(Object loginId) {
        return loginId == null ? null : authUserRepository.findById(Convert.toLong(loginId));
    }

    /** 写登录记录。 */
    private void writeLog(AuthUser user, LoginLogStatusEnum status, String message) {
        AuthLoginLog log = new AuthLoginLog();
        log.setUserId(user == null ? null : user.getUserId());
        log.setUsername(user == null ? "" : user.getUsername());
        log.setLoginIp(ClientInfoUtil.getClientIp());
        log.setUserAgent(ClientInfoUtil.getUserAgent());
        log.setStatus(status);
        log.setMessage(message);
        log.setLoginTime(LocalDateTime.now());
        authLoginLogRepository.insert(log);
    }

    /** 写在线快照（Redis）。 */
    private void writeSnapshot(AuthUser user, String tokenValue) {
        AuthOnlineSnapshot snapshot = new AuthOnlineSnapshot();
        snapshot.setTokenValue(tokenValue);
        snapshot.setUserId(user.getUserId());
        snapshot.setUsername(user.getUsername());
        snapshot.setNickname(user.getNickname());
        snapshot.setLoginIp(ClientInfoUtil.getClientIp());
        snapshot.setLoginTime(LocalDateTime.now());
        authOnlineRepository.save(AiPlatformConstant.ONLINE_REDIS_KEY_PREFIX, snapshot, AiPlatformConstant.ONLINE_TTL_SECONDS);
    }

    /** 删除在线快照。 */
    private void removeSnapshot(String tokenValue) {
        if (StrUtil.isNotBlank(tokenValue)) {
            authOnlineRepository.removeByTokenValue(AiPlatformConstant.ONLINE_REDIS_KEY_PREFIX, tokenValue);
        }
    }

    /** 安全执行事件动作：异常仅记日志，不阻断 Sa-Token 主流程。 */
    private void runSafely(Runnable action) {
        try {
            action.run();
        } catch (Exception e) {
            LoggerUtil.error(LogFileEnum.COMMON_ERROR, "登录事件处理失败", e);
        }
    }
}
