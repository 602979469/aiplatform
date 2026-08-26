package com.jakt.aiplatform.core.service.impl;
import com.jakt.aiplatform.core.model.enums.BizErrorCodeEnum;

import com.jakt.aiplatform.core.model.domain.AuthLoginLog;
import com.jakt.aiplatform.common.framework.tools.AssertUtil;
import com.jakt.aiplatform.core.model.param.AuthLoginLogQueryParam;
import com.jakt.aiplatform.common.framework.result.PageResult;
import com.jakt.aiplatform.core.repository.AuthLoginLogRepository;
import com.jakt.aiplatform.core.service.AuthLoginLogService;
import org.springframework.stereotype.Service;

/**
 * 登录记录领域服务实现。
 */
@Service
public class AuthLoginLogServiceImpl implements AuthLoginLogService {

    private final AuthLoginLogRepository authLoginLogRepository;

    public AuthLoginLogServiceImpl(AuthLoginLogRepository authLoginLogRepository) {
        this.authLoginLogRepository = authLoginLogRepository;
    }

    @Override
    public PageResult<AuthLoginLog> pageLoginLog(AuthLoginLogQueryParam query) {
        return authLoginLogRepository.findPage(query);
    }

    @Override
    public void deleteLoginLog(Long logId) {
        int affected = authLoginLogRepository.deleteById(logId);
        AssertUtil.throwErrWhenTrue(affected == 0, BizErrorCodeEnum.DELETE_FAILED, "删除失败：记录不存在或已被删除");
    }
}
