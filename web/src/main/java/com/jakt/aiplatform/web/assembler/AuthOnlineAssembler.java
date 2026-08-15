package com.jakt.aiplatform.web.assembler;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.constant.AiPlatformConstant;
import com.jakt.aiplatform.core.model.dto.AuthOnlineInfo;
import com.jakt.aiplatform.core.model.param.AuthOnlineQueryParam;
import com.jakt.aiplatform.web.param.AuthOnlineQueryRequest;
import com.jakt.aiplatform.web.result.AuthOnlineResponse;

import java.util.List;

/**
 * 在线会话域组装器：在线用户响应转换。
 */
public final class AuthOnlineAssembler {

    private AuthOnlineAssembler() {
    }

    /**
     * 在线用户 → 在线响应（token 脱敏）。
     *
     * @param info 在线用户；为空返回 null
     * @return 在线响应
     */
    public static AuthOnlineResponse toOnlineResponse(AuthOnlineInfo info) {
        if (info == null) {
            return null;
        }
        AuthOnlineResponse response = new AuthOnlineResponse();
        response.setTokenValue(maskToken(info.getTokenValue()));
        response.setUserId(info.getUserId());
        response.setUsername(info.getUsername());
        response.setNickname(info.getNickname());
        response.setLoginIp(info.getLoginIp());
        response.setLoginTime(info.getLoginTime());
        return response;
    }

    /**
     * 在线查询请求 → 查询参数（分页缺省走 PageParam 默认值）。
     *
     * @param request 查询请求；为空返回空查询参数
     * @return 查询参数
     */
    public static AuthOnlineQueryParam toQueryParam(AuthOnlineQueryRequest request) {
        if (request == null) {
            return new AuthOnlineQueryParam();
        }
        AuthOnlineQueryParam query = new AuthOnlineQueryParam();
        if (request.getPageNum() != null) {
            query.setPageNum(request.getPageNum());
        }
        if (request.getPageSize() != null) {
            query.setPageSize(request.getPageSize());
        }
        query.setKeyword(request.getKeyword());
        return query;
    }

    /** token 脱敏：保留后段，前 6 位打码。 */
    private static String maskToken(String tokenValue) {
        if (StrUtil.isBlank(tokenValue)) {
            return AiPlatformConstant.EMPTY_STRING;
        }
        return StrUtil.hide(tokenValue, 0, Math.min(6, tokenValue.length()));
    }
}
