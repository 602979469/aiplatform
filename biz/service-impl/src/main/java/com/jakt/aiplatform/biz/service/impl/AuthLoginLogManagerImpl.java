package com.jakt.aiplatform.biz.service.impl;

import com.jakt.aiplatform.biz.service.AuthLoginLogManager;
import com.jakt.aiplatform.core.model.domain.AuthLoginLog;
import com.jakt.aiplatform.core.model.param.AuthLoginLogQueryParam;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.service.AuthLoginLogService;
import org.springframework.stereotype.Service;

    /** 登录记录用例编排实现：业务委托登录记录领域服务。 */
@Service
public class AuthLoginLogManagerImpl implements AuthLoginLogManager {

    private final AuthLoginLogService authLoginLogService;

    public AuthLoginLogManagerImpl(AuthLoginLogService authLoginLogService) {
        this.authLoginLogService = authLoginLogService;
    }

    @Override
    public PageResult<AuthLoginLog> pageLoginLog(AuthLoginLogQueryParam query) {
        return authLoginLogService.pageLoginLog(query);
    }

    @Override
    public void deleteLoginLog(Long logId) {
        authLoginLogService.deleteLoginLog(logId);
    }
}
