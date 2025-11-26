package com.admin.system.security;

import com.admin.system.config.JwtProperties;
import com.admin.system.utils.JwtUtil;
import com.admin.system.utils.RedisUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
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
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private RedisUtil redisUtil;

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        String requestURI = request.getRequestURI();
        String token = getTokenFromRequest(request);

        log.debug("请求URI: {}, Token存在: {}", requestURI, StringUtils.hasText(token));

        if (StringUtils.hasText(token)) {
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
                log.warn("Redis中未找到token对应的用户信息，Token: {}", token.substring(0, Math.min(20, token.length())));
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
        String userKey = "login_tokens:" + token;
        return (LoginUser) redisUtil.get(userKey);
    }

    /**
     * 验证令牌有效期，相差不足20分钟，自动刷新缓存
     */
    private void verifyToken(LoginUser loginUser) {
        long expireTime = loginUser.getExpireTime();
        long currentTime = System.currentTimeMillis();
        if (expireTime - currentTime <= 20 * 60 * 1000) {
            refreshToken(loginUser);
        }
    }

    /**
     * 刷新令牌有效期
     */
    private void refreshToken(LoginUser loginUser) {
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + jwtProperties.getExpireTime() * 60 * 1000);
        String userKey = "login_tokens:" + loginUser.getToken();
        redisUtil.set(userKey, loginUser, jwtProperties.getExpireTime(), TimeUnit.MINUTES);
    }

}
