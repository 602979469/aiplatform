package com.jakt.aiplatform.web.assembler;

import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.AuthLoginLog;
import com.jakt.aiplatform.core.model.param.AuthLoginLogQueryParam;
import com.jakt.aiplatform.web.param.AuthLoginLogQueryRequest;
import com.jakt.aiplatform.web.result.AuthLoginLogResponse;

import java.util.List;

/**
 * 登录记录域组装器：登录记录响应转换。
 */
public final class AuthLoginLogAssembler {

    private AuthLoginLogAssembler() {
    }

    /**
     * 登录记录 → 记录响应。
     *
     * @param log 登录记录；为空返回 null
     * @return 记录响应
     */
    public static AuthLoginLogResponse toLoginLogResponse(AuthLoginLog log) {
        if (log == null) {
            return null;
        }
        AuthLoginLogResponse response = new AuthLoginLogResponse();
        response.setLogId(log.getLogId());
        response.setUserId(log.getUserId());
        response.setUsername(log.getUsername());
        response.setLoginIp(log.getLoginIp());
        response.setUserAgent(log.getUserAgent());
        response.setStatus(log.getStatus());
        response.setMessage(log.getMessage());
        response.setLoginTime(log.getLoginTime());
        return response;
    }

    /**
     * 登录记录查询请求 → 查询参数（分页缺省走 PageParam 默认值）。
     *
     * @param request 查询请求；为空返回空查询参数
     * @return 查询参数
     */
    public static AuthLoginLogQueryParam toQueryParam(AuthLoginLogQueryRequest request) {
        if (request == null) {
            return new AuthLoginLogQueryParam();
        }
        AuthLoginLogQueryParam query = new AuthLoginLogQueryParam();
        if (request.getPageNum() != null) {
            query.setPageNum(request.getPageNum());
        }
        if (request.getPageSize() != null) {
            query.setPageSize(request.getPageSize());
        }
        query.setUsername(request.getUsername());
        query.setStatus(request.getStatus());
        return query;
    }
}
