package com.jakt.aiplatform.web.interceptor;

import cn.dev33.satoken.stp.StpUtil;
import cn.hutool.core.convert.Convert;
import com.jakt.aiplatform.core.model.context.AuthSessionKeys;
import com.jakt.aiplatform.common.framework.context.UserContext;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;
import org.springframework.web.servlet.HandlerInterceptor;

/**
 * 用户上下文拦截器：SaInterceptor 完成登录校验后回填 UserContext（依赖 Sa-Token 请求上下文）。
 */
@Component
public class UserContextInterceptor implements HandlerInterceptor {

    @Override
    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) {
        Object loginId = StpUtil.getLoginIdDefaultNull();
        if (loginId != null) {
            Long userId = Convert.toLong(loginId);
            String userName = Convert.toStr(StpUtil.getSession().get(AuthSessionKeys.USERNAME), "");
            UserContext.set(userId, userName);
        }
        return true;
    }

    @Override
    public void afterCompletion(HttpServletRequest request, HttpServletResponse response,
                                Object handler, Exception ex) {
        UserContext.clear();
    }
}
