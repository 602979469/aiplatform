package com.jakt.aiplatform.web.filter;

import cn.hutool.core.util.StrUtil;
import com.jakt.aiplatform.core.model.context.UserContext;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * 用户上下文过滤器：从请求头读取当前用户写入 UserContext（缺省 1/admin，方便本地联调）。
 * 后续接入真实登录体系时只改本类。
 */
@Component
@Order(Ordered.HIGHEST_PRECEDENCE + 1)
public class UserContextFilter extends OncePerRequestFilter {

    private static final String USER_ID_HEADER = "X-User-Id";

    private static final String USER_NAME_HEADER = "X-User-Name";

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain)
            throws ServletException, IOException {
        String userIdHeader = request.getHeader(USER_ID_HEADER);
        String userNameHeader = request.getHeader(USER_NAME_HEADER);
        Long userId = StrUtil.isBlank(userIdHeader) ? 1L : parseUserId(userIdHeader);
        String userName = StrUtil.isBlank(userNameHeader) ? "admin" : userNameHeader;
        UserContext.set(userId, userName);
        try {
            filterChain.doFilter(request, response);
        } finally {
            UserContext.clear();
        }
    }

    private Long parseUserId(String userIdHeader) {
        try {
            return Long.parseLong(userIdHeader);
        } catch (NumberFormatException e) {
            return 1L;
        }
    }
}
