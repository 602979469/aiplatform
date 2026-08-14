package com.jakt.aiplatform.core.service;

import com.jakt.aiplatform.core.model.domain.AuthLoginLog;
import com.jakt.aiplatform.core.model.param.AuthLoginLogQueryParam;
import com.jakt.aiplatform.common.util.result.PageResult;

/**
 * 登录记录领域服务：分页查询与删除。
 */
public interface AuthLoginLogService {

    /**
     * 分页查询登录记录。
     *
     * @param query 查询参数
     * @return 分页结果
     */
    PageResult<AuthLoginLog> pageLoginLog(AuthLoginLogQueryParam query);

    /**
     * 删除登录记录。
     *
     * @param logId 日志ID
     */
    void deleteLoginLog(Long logId);
}
