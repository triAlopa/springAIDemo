package com.chen.config;

import com.chen.interceptor.LoginInterceptor;
import com.chen.interceptor.RefreshTokenInterceptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.web.server.ServerWebExchange;
import org.springframework.web.server.WebFilter;
import org.springframework.web.server.WebFilterChain;
import org.springframework.web.servlet.config.annotation.InterceptorRegistry;
import org.springframework.web.servlet.config.annotation.ResourceHandlerRegistry;
import org.springframework.web.servlet.config.annotation.WebMvcConfigurer;

@Configuration
public class WebConfig implements WebMvcConfigurer {


    @Autowired
    private LoginInterceptor loginInterceptor;

    @Autowired
    private RefreshTokenInterceptor refreshTokenInterceptor;

    @Override
    public void addInterceptors(InterceptorRegistry registry) {
        registry.addInterceptor(refreshTokenInterceptor)
                .order(Ordered.HIGHEST_PRECEDENCE)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/register",
                        "/user/emailCode/**",      // 正确：匹配所有/emailCode/下的路径
                        "/user/login",             // 只能匹配 /user/login
                        "/user/login/**",// 匹配 /user/login/xxx，但不包括 /user/login 本身

                        // Swagger UI
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-ui/index.html",

                        // Swagger API文档
                        "/v3/api-docs",
                        "/v3/api-docs/**",  // 匹配所有分组文档

                        "/resources/**",
                        "/resources",

                        // Swagger资源配置
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/swagger-resources/configuration/ui",
                        "/swagger-resources/configuration/security",

                        // Springfox兼容（如果使用）
                        "/swagger**",

                        // Webjars资源
                        "/webjars/**",

                        // Knife4j（增强版Swagger）
                        "/doc.html",
                        "/favicon.ico"
                );
        registry.addInterceptor(loginInterceptor)
                .addPathPatterns("/**")
                .excludePathPatterns(
                        "/user/register",
                        "/user/emailCode/**",      // 正确：匹配所有/emailCode/下的路径
                        "/user/login",             // 只能匹配 /user/login
                        "/user/login/**",// 匹配 /user/login/xxx，但不包括 /user/login 本身

                        // Swagger UI
                        "/swagger-ui.html",
                        "/swagger-ui/**",
                        "/swagger-ui/index.html",

                        // Swagger API文档
                        "/v3/api-docs",
                        "/v3/api-docs/**",  // 匹配所有分组文档

                        "/resources/**",
                        "/resources",

                        // Swagger资源配置
                        "/swagger-resources",
                        "/swagger-resources/**",
                        "/swagger-resources/configuration/ui",
                        "/swagger-resources/configuration/security",

                        // Springfox兼容（如果使用）
                        "/swagger**",

                        // Webjars资源
                        "/webjars/**",

                        // Knife4j（增强版Swagger）
                        "/doc.html",
                        "/favicon.ico"
                );
    }

    @Bean
    @Order(Ordered.HIGHEST_PRECEDENCE)
    public WebFilter connectionCloseFilter() {
        return (ServerWebExchange exchange, WebFilterChain chain) -> {
            exchange.getResponse().getHeaders().add("Connection", "close");
            return chain.filter(exchange);
        };
    }

    /**
     * 设置静态资源映射
     *
     * @param registry
     */
    @Override
    public void addResourceHandlers(ResourceHandlerRegistry registry) {
        registry.addResourceHandler("/doc.html").addResourceLocations("classpath:/META-INF/resources/");
        registry.addResourceHandler("/webjars/**").addResourceLocations("classpath:/META-INF/resources/webjars/");
    }
}
