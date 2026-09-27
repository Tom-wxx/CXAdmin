package com.admin.framework.config;

import com.admin.framework.security.JwtAuthenticationFilter;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;
import org.springframework.security.web.header.writers.XXssProtectionHeaderWriter;

/**
 * Spring Security配置（Spring Security 6/7：SecurityFilterChain + Lambda DSL）
 *
 * @author Admin
 */
@Configuration
@EnableWebSecurity
@EnableMethodSecurity(prePostEnabled = true, securedEnabled = true)
@RequiredArgsConstructor
public class SecurityConfig {

    private final JwtAuthenticationFilter jwtAuthenticationFilter;

    /**
     * 密码加密
     */
    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    /**
     * 认证管理器：基于自动装配的 DaoAuthenticationProvider（UserDetailsServiceImpl + PasswordEncoder）。
     * LoginServiceImpl 注入此 Bean 调用 authenticate()。
     */
    @Bean
    public AuthenticationManager authenticationManager(AuthenticationConfiguration configuration) throws Exception {
        return configuration.getAuthenticationManager();
    }

    @Bean
    public SecurityFilterChain filterChain(HttpSecurity http) throws Exception {
        http
                // CSRF禁用，因为不使用session
                .csrf(csrf -> csrf.disable())
                // 安全响应头配置
                .headers(headers -> headers
                        // 允许同源iframe嵌入（Druid监控需要）
                        .frameOptions(frame -> frame.sameOrigin())
                        // 防止MIME类型嗅探
                        .contentTypeOptions(Customizer.withDefaults())
                        // XSS保护
                        .xssProtection(xss -> xss.headerValue(
                                XXssProtectionHeaderWriter.HeaderValue.ENABLED_MODE_BLOCK)))
                // 基于token，所以不需要session
                .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
                // 禁用默认的 logout 行为（我们使用自定义的 /logout 接口）
                .logout(logout -> logout.disable())
                // 过滤请求
                .authorizeHttpRequests(auth -> auth
                        // 允许匿名访问的接口
                        .requestMatchers(
                                "/login",
                                "/captcha",
                                "/register",
                                "/forgot-password",
                                "/reset-password",
                                // Actuator 端点匹配 servlet path（容器已去除 context-path /api）
                                "/actuator/health",
                                "/actuator/info",
                                "/uploads/**",
                                // springdoc / Swagger UI
                                "/swagger-ui.html",
                                "/swagger-ui/**",
                                "/v3/api-docs/**",
                                "/webjars/**",
                                "/druid/**",
                                // SSO 公开端点
                                "/sso/providers",
                                "/sso/authorize/**",
                                "/sso/callback/**"
                        ).permitAll()
                        .requestMatchers(HttpMethod.GET, "/system/config/public/login-pet").permitAll()
                        // 静态资源，可匿名访问
                        .requestMatchers(HttpMethod.GET, "/", "/*.html", "/**/*.html", "/**/*.css", "/**/*.js", "/profile/**").permitAll()
                        // 除上面外的所有请求全部需要鉴权认证
                        .anyRequest().authenticated())
                // 添加JWT filter
                .addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class);

        return http.build();
    }

}
