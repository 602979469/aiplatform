package com.jakt.aiplatform.biz.service;

import com.jakt.aiplatform.core.model.domain.AuthLoginLog;
import com.jakt.aiplatform.core.model.param.AuthLoginLogQueryParam;
import com.jakt.aiplatform.common.framework.result.PageResult;

/**
 * 登录记录用例编排：分页查询与删除。
 */
public interface AuthLoginLogManager {

    /**
     * 分页查询登录记录。
     *
     * @param query 查询参数
     * @return 登录记录分页
     */
    PageResult<AuthLoginLog> pageLoginLog(AuthLoginLogQueryParam query);

    /**
     * 删除登录记录。
     *
     * @param logId 日志ID
     */
    void deleteLoginLog(Long logId);
}
