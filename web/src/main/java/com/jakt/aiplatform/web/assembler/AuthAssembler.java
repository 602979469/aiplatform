package com.jakt.aiplatform.web.assembler;

import com.jakt.aiplatform.core.model.dto.AuthLoginInfo;
import com.jakt.aiplatform.core.model.dto.AuthUserInfo;
import com.jakt.aiplatform.web.result.AuthLoginResponse;
import com.jakt.aiplatform.web.result.AuthUserInfoResponse;

/**
 * 认证域组装器：登录结果与当前用户信息。
 */
public final class AuthAssembler {

    private AuthAssembler() {
    }

    /**
     * 登录结果 → 登录响应。
     *
     * @param info 登录结果；为空返回 null
     * @return 登录响应
     */
    public static AuthLoginResponse toLoginResponse(AuthLoginInfo info) {
        if (info == null) {
            return null;
        }
        AuthLoginResponse response = new AuthLoginResponse();
        response.setTokenName(info.getTokenName());
        response.setTokenValue(info.getTokenValue());
        response.setUserId(info.getUserId());
        return response;
    }

    /**
     * 当前用户信息 → 用户信息响应。
     *
     * @param info 当前用户信息；为空返回 null
     * @return 用户信息响应
     */
    public static AuthUserInfoResponse toUserInfoResponse(AuthUserInfo info) {
        if (info == null) {
            return null;
        }
        AuthUserInfoResponse response = new AuthUserInfoResponse();
        response.setUser(AuthUserAssembler.toUserResponse(info.getUser()));
        response.setRoles(info.getRoles());
        response.setPerms(info.getPerms());
        return response;
    }
}
