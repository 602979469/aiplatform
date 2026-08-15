package com.jakt.aiplatform.core.service.impl;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.digest.BCrypt;
import com.jakt.aiplatform.common.util.tools.AssertUtil;
import com.jakt.aiplatform.common.util.tools.ClientInfoUtil;
import com.jakt.aiplatform.core.model.dto.AuthLoginInfo;
import com.jakt.aiplatform.core.model.domain.AuthRole;
import com.jakt.aiplatform.core.model.domain.AuthUser;
import com.jakt.aiplatform.core.model.context.UserContext;
import com.jakt.aiplatform.core.model.context.AuthSessionKeys;
import com.jakt.aiplatform.core.model.enums.ErrorCodeEnum;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.enums.LoginLogStatusEnum;
import com.jakt.aiplatform.core.model.exception.AiPlatformException;
import com.jakt.aiplatform.common.util.enums.LogFileEnum;
import com.jakt.aiplatform.core.model.param.AuthRoleQueryParam;
import com.jakt.aiplatform.common.util.result.Result;
import com.jakt.aiplatform.common.util.template.BizTemplate;
import com.jakt.aiplatform.common.util.template.TransactionTemplate;
import com.jakt.aiplatform.common.util.tools.LoggerUtil;
import com.jakt.aiplatform.core.repository.AuthLoginLogRepository;
import com.jakt.aiplatform.core.repository.AuthRoleRepository;
import com.jakt.aiplatform.core.repository.AuthUserRepository;
import com.jakt.aiplatform.core.service.AuthService;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 认证领域服务实现：登录/注册/登出。成功登录日志由 AuthLoginListener 事件写入，本类只写失败日志。
 */
@Service
public class AuthServiceImpl implements AuthService {

    /** 注册默认角色 key。 */
    private static final String DEFAULT_ROLE_KEY = "common";

    private final AuthUserRepository authUserRepository;

    private final AuthRoleRepository authRoleRepository;

    private final AuthLoginLogRepository authLoginLogRepository;

    private final TransactionTemplate transactionTemplate;

    public AuthServiceImpl(AuthUserRepository authUserRepository,
                           AuthRoleRepository authRoleRepository,
                           AuthLoginLogRepository authLoginLogRepository,
                           TransactionTemplate transactionTemplate) {
        this.authUserRepository = authUserRepository;
        this.authRoleRepository = authRoleRepository;
        this.authLoginLogRepository = authLoginLogRepository;
        this.transactionTemplate = transactionTemplate;
    }

    @Override
    public AuthLoginInfo login(String username, String password) {
        AuthUser user = authUserRepository.findByUsername(username);
        if (user == null || !BCrypt.checkpw(password, user.getPassword())) {
            writeLoginLog(null, username, LoginLogStatusEnum.FAIL, "用户名或密码错误");
            throw AiPlatformException.ofThrow(ErrorCodeEnum.LOGIN_FAILED);
        }
        AssertUtil.throwErrWhenTrue(user.getStatus() == EnableStatusEnum.DISABLE, ErrorCodeEnum.USER_DISABLED);
        AssertUtil.throwErrWhenTrue(StpUtil.isDisable(user.getUserId()), ErrorCodeEnum.ACCOUNT_BANNED);
        StpUtil.login(user.getUserId());
        StpUtil.getSession().set(AuthSessionKeys.USERNAME, user.getUsername());
        return buildLoginInfo(user.getUserId());
    }

    @Override
    public AuthLoginInfo register(String username, String password, String nickname, String email) {
        AssertUtil.throwErrWhenTrue(authUserRepository.findByUsername(username) != null,
                ErrorCodeEnum.USERNAME_EXISTS);
        Result<AuthUser> result = BizTemplate.execute(transactionTemplate, () -> {
            AuthUser user = new AuthUser();
            user.setUsername(username);
            user.setPassword(BCrypt.hashpw(password));
            user.setNickname(StrUtil.blankToDefault(nickname, username));
            user.setEmail(StrUtil.nullToEmpty(email));
            user.setAvatar("");
            user.setStatus(EnableStatusEnum.ENABLE);
            authUserRepository.insert(user);

            AuthRoleQueryParam roleQuery = new AuthRoleQueryParam();
            roleQuery.setRoleKey(DEFAULT_ROLE_KEY);
            AuthRole defaultRole = authRoleRepository.findOne(roleQuery);
            AssertUtil.throwErrWhenNull(defaultRole, ErrorCodeEnum.SYSTEM_ERROR, "默认角色未配置");
            authUserRepository.replaceRoles(user.getUserId(), List.of(defaultRole.getRoleId()));
            return user;
        });
        if (!result.isSuccess()) {
            throw AiPlatformException.ofThrow(result.getErrorCode(), result.getErrorMessage());
        }
        StpUtil.login(result.getData().getUserId());
        StpUtil.getSession().set(AuthSessionKeys.USERNAME, result.getData().getUsername());
        return buildLoginInfo(result.getData().getUserId());
    }

    @Override
    public void logout() {
        StpUtil.logout();
    }

    @Override
    public AuthUser getCurrentUser() {
        AuthUser user = authUserRepository.findById(UserContext.getUserId());
        AssertUtil.throwErrWhenNull(user, ErrorCodeEnum.NOT_LOGIN);
        return user;
    }

    /** 组装登录结果（token 名与值取自 Sa-Token 当前会话）。 */
    private AuthLoginInfo buildLoginInfo(Long userId) {
        AuthLoginInfo info = new AuthLoginInfo();
        info.setUserId(userId);
        info.setTokenName(StpUtil.getTokenName());
        info.setTokenValue(StpUtil.getTokenValue());
        return info;
    }

    /** 写登录失败日志（事件处理异常不影响登录主流程）。 */
    private void writeLoginLog(Long userId, String username, LoginLogStatusEnum status, String message) {
        try {
            com.jakt.aiplatform.core.model.domain.AuthLoginLog log =
                    new com.jakt.aiplatform.core.model.domain.AuthLoginLog();
            log.setUserId(userId);
            log.setUsername(username);
            log.setLoginIp(ClientInfoUtil.getClientIp());
            log.setUserAgent(ClientInfoUtil.getUserAgent());
            log.setStatus(status);
            log.setMessage(message);
            log.setLoginTime(LocalDateTime.now());
            authLoginLogRepository.insert(log);
        } catch (Exception e) {
            LoggerUtil.error(LogFileEnum.COMMON_ERROR, "写入登录失败日志异常", e);
        }
    }

}
