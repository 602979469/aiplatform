package com.jakt.aiplatform.web.assembler;

import com.jakt.aiplatform.common.util.tools.ConvertUtil;
import com.jakt.aiplatform.core.model.domain.AuthUser;
import com.jakt.aiplatform.core.model.param.AuthUserQueryParam;
import com.jakt.aiplatform.web.param.AuthUserQueryRequest;
import com.jakt.aiplatform.web.param.AuthUserCreateRequest;
import com.jakt.aiplatform.web.param.AuthUserUpdateRequest;
import com.jakt.aiplatform.web.result.AuthUserResponse;

import java.util.List;

/**
 * 用户域组装器：DTO↔Model 与用户响应转换。
 */
public final class AuthUserAssembler {

    private AuthUserAssembler() {
    }

    /**
     * 用户 → 用户响应（不含密码）。
     *
     * @param user 用户；为空返回 null
     * @return 用户响应
     */
    public static AuthUserResponse toUserResponse(AuthUser user) {
        if (user == null) {
            return null;
        }
        AuthUserResponse response = new AuthUserResponse();
        response.setUserId(user.getUserId());
        response.setUsername(user.getUsername());
        response.setNickname(user.getNickname());
        response.setEmail(user.getEmail());
        response.setAvatar(user.getAvatar());
        response.setStatus(user.getStatus());
        response.setRemark(user.getRemark());
        response.setCreateTime(user.getCreateTime());
        return response;
    }

    /**
     * 新增用户请求 → 用户领域模型。
     *
     * @param request 新增用户请求
     * @return 用户领域模型
     */
    public static AuthUser toUser(AuthUserCreateRequest request) {
        if (request == null) {
            return null;
        }
        AuthUser user = new AuthUser();
        user.setUsername(request.getUsername());
        user.setPassword(request.getPassword());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());
        return user;
    }

    /**
     * 修改用户请求 → 用户领域模型（部分更新，只带非空字段）。
     *
     * @param request 修改用户请求
     * @return 用户领域模型
     */
    public static AuthUser toUser(AuthUserUpdateRequest request) {
        if (request == null) {
            return null;
        }
        AuthUser user = new AuthUser();
        user.setUserId(request.getUserId());
        user.setNickname(request.getNickname());
        user.setEmail(request.getEmail());
        user.setStatus(request.getStatus());
        user.setRemark(request.getRemark());
        return user;
    }

    /**
     * 用户查询请求 → 查询参数（分页缺省走 PageParam 默认值）。
     *
     * @param request 查询请求；为空返回空查询参数
     * @return 查询参数
     */
    public static AuthUserQueryParam toQueryParam(AuthUserQueryRequest request) {
        if (request == null) {
            return new AuthUserQueryParam();
        }
        AuthUserQueryParam query = new AuthUserQueryParam();
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
