package com.admin.system.security;

import com.admin.system.common.constants.SystemConstants;
import com.admin.system.config.JwtProperties;
import com.admin.system.utils.JwtUtil;
import com.admin.system.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.concurrent.TimeUnit;

/**
 * JWT认证过滤器
 *
 * @author Admin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    private final JwtProperties jwtProperties;
    private final RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String token = getTokenFromRequest(request);

        log.debug("请求URI: {}, Token存在: {}", requestURI, StringUtils.hasText(token));

        if (StringUtils.hasText(token)) {
            try {
                LoginUser loginUser = getLoginUser(token);
                if (loginUser != null) {
                    log.debug("从Redis获取到用户: {}", loginUser.getUsername());
                    if (SecurityContextHolder.getContext().getAuthentication() == null) {
                        verifyToken(loginUser);
                        UsernamePasswordAuthenticationToken authenticationToken =
                                new UsernamePasswordAuthenticationToken(loginUser, null, loginUser.getAuthorities());
                        authenticationToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                        SecurityContextHolder.getContext().setAuthentication(authenticationToken);
                        log.debug("用户认证成功: {}", loginUser.getUsername());
                    }
                } else {
                    log.warn("Redis中未找到token对应的用户信息");
                }
            } catch (Exception e) {
                log.error("Token认证过程中发生异常: {}", e.getMessage());
                // Redis不可用或其他异常时，不设置认证信息，请求将被Spring Security拦截为未认证
                SecurityContextHolder.clearContext();
            }
        }
        chain.doFilter(request, response);
    }

    /**
     * 从请求中获取Token
     */
    private String getTokenFromRequest(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer ")) {
            return bearerToken.substring(7);
        }
        return null;
    }

    /**
     * 获取登录用户信息
     */
    private LoginUser getLoginUser(String token) {
        String userKey = SystemConstants.LOGIN_TOKEN_KEY + token;
        return (LoginUser) redisUtil.get(userKey);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     */
    private void verifyToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= SystemConstants.TOKEN_REFRESH_THRESHOLD) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     */
    private void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + jwtProperties.getExpireTime() * 60 * 1000);
        String userKey = SystemConstants.LOGIN_TOKEN_KEY + loginUser.getToken();
        redisUtil.set(userKey, loginUser, jwtProperties.getExpireTime(), TimeUnit.MINUTES);
    }

}
