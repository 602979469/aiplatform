package com.jakt.aiplatform.web.config;

import cn.dev33.satoken.interceptor.SaInterceptor;
import cn.dev33.satoken.router.SaRouter;
import cn.dev33.satoken.router.SaHttpMethod;
import cn.dev33.satoken.stp.StpUtil;
import com.jakt.aiplatform.common.framework.enums.BaseEnum;
import com.jakt.aiplatform.core.model.enums.EnableStatusEnum;
import com.jakt.aiplatform.core.model.enums.LoginLogStatusEnum;
import com.jakt.aiplatform.core.model.enums.MenuTypeEnum;
import com.jakt.aiplatform.core.model.enums.VisibleEnum;
import com.jakt.aiplatform.web.interceptor.UserContextInterceptor;
import org.springframework.context.annotation.Configuration;
import org.springframework.format.FormatterRegistry;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

/**
 * Sa-Token 拦截器配置：全局登录态兜底，白名单放行匿名端点；细粒度权限用 @SaCheckPermission。
 */
@Configuration
public class SaTokenConfigure implements WebMvcConfigurer {

    /** 用户上下文回填拦截器（注册在 SaInterceptor 之后）。 */
    private final UserContextInterceptor userContextInterceptor;

    public SaTokenConfigure(UserContextInterceptor userContextInterceptor) {
        this.userContextInterceptor = userContextInterceptor;
    }

    /** 匿名端点白名单。 */
    private static final String[] ANON_URLS = {
            "/auth/login", "/auth/register", "/error",
            "/v3/api-docs/**", "/swagger-ui/**", "/swagger-ui.html"
    };

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(new SaInterceptor(handler -> SaRouter
                        .match("/**")
                        .notMatch(ANON_URLS)
                        .notMatch(SaHttpMethod.OPTIONS)
                        .check(r -> StpUtil.checkLogin())))
                .addPathPatterns("/**");
        registry.addInterceptor(userContextInterceptor).addPathPatterns("/**");
    }

    /** 注册枚举 code 转换：查询参数（status=0 等）自动转为对应枚举。 */
    @Override
    public void addFormatters(FormatterRegistry registry) {
        registry.addConverter(String.class, EnableStatusEnum.class,
                code -> BaseEnum.fromCode(EnableStatusEnum.class, code));
        registry.addConverter(String.class, MenuTypeEnum.class,
                code -> BaseEnum.fromCode(MenuTypeEnum.class, code));
        registry.addConverter(String.class, VisibleEnum.class,
                code -> BaseEnum.fromCode(VisibleEnum.class, code));
        registry.addConverter(String.class, LoginLogStatusEnum.class,
                code -> BaseEnum.fromCode(LoginLogStatusEnum.class, code));
    }
}
