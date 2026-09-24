package org.njupt.njuptphysim.common.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.web.SecurityFilterChain;

/**
 * Spring Security 放行配置。
 *
 * <p>项目鉴权由自研 JWT 拦截器（LoginInterceptor + PermitInterceptor）完成，
 * pom 中的 spring-boot-starter-security 并未使用；一旦其 jar 在类路径上，
 * 默认的 Basic 认证会拦截全部请求导致 401。此处显式放行所有请求并关闭 CSRF，
 * 使应用行为与该依赖存在与否无关。</p>
 */
@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth.anyRequest().permitAll());
        return http.build();
    }
}
