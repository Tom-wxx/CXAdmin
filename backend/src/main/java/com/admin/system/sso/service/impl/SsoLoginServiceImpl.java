package com.admin.system.sso.service.impl;

import com.admin.system.common.constants.SystemConstants;
import com.admin.system.config.JwtProperties;
import com.admin.system.entity.SysUser;
import com.admin.system.mapper.SysUserMapper;
import com.admin.system.security.LoginUser;
import com.admin.system.service.ISysMenuService;
import com.admin.system.sso.config.SsoProperties;
import com.admin.system.sso.dto.SsoUserInfo;
import com.admin.system.sso.entity.SysSsoProvider;
import com.admin.system.sso.entity.SysUserSsoBinding;
import com.admin.system.sso.mapper.SysUserSsoBindingMapper;
import com.admin.system.sso.service.ISsoLoginService;
import com.admin.system.sso.service.ISysSsoProviderService;
import com.admin.system.sso.strategy.SsoStrategy;
import com.admin.system.sso.strategy.SsoStrategyFactory;
import com.admin.system.utils.JwtUtil;
import com.admin.system.utils.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class SsoLoginServiceImpl implements ISsoLoginService {

    private static final String STATE_KEY = "sso:state:";
    private static final int STATE_TTL_MIN = 5;

    private final ISysSsoProviderService providerService;
    private final SsoStrategyFactory strategyFactory;
    private final SsoProperties ssoProperties;
    private final RedisUtil redisUtil;
    private final SysUserMapper userMapper;
    private final SysUserSsoBindingMapper bindingMapper;
    private final ISysMenuService menuService;
    private final PasswordEncoder passwordEncoder;
    private final JwtProperties jwtProperties;

    @Override
    public String buildAuthorizationUrl(String code) {
        SysSsoProvider provider = requireEnabled(code);
        SsoStrategy strategy = strategyFactory.get(provider.getType());

        String state = UUID.randomUUID().toString().replace("-", "");
        redisUtil.set(STATE_KEY + state, code, STATE_TTL_MIN, TimeUnit.MINUTES);

        String redirectUri = callbackUri(code);
        return strategy.buildAuthorizationUrl(provider, state, redirectUri);
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleCallback(String code, String authCode, String state, HttpServletResponse response) {
        // 1. state 校验（一次性消费）
        String redisKey = STATE_KEY + state;
        Object cachedCode = redisUtil.get(redisKey);
        if (cachedCode == null || !code.equals(cachedCode.toString())) {
            throw new RuntimeException("Invalid or expired SSO state");
        }
        redisUtil.delete(redisKey);

        SysSsoProvider provider = requireEnabled(code);
        SsoStrategy strategy = strategyFactory.get(provider.getType());

        // 2. 换 token + 拉 userinfo
        SsoUserInfo info = strategy.exchangeAndFetchUser(provider, authCode, callbackUri(code));

        // 3. 找/建用户
        SysUser user = resolveUser(provider, info);

        // 4. 签 JWT，写 Redis，Set-Cookie
        issueJwtAndCookie(user, response);

        return ssoProperties.getCallbackBaseUrl() + "/index";
    }

    private SysUser resolveUser(SysSsoProvider provider, SsoUserInfo info) {
        // 优先级 1：已有绑定
        SysUserSsoBinding binding = bindingMapper.selectOne(
                new LambdaQueryWrapper<SysUserSsoBinding>()
                        .eq(SysUserSsoBinding::getProviderId, provider.getId())
                        .eq(SysUserSsoBinding::getExternalUserId, info.getExternalUserId()));
        if (binding != null) {
            SysUser u = userMapper.selectById(binding.getUserId());
            if (u != null) return u;
            log.warn("SSO binding {} points to missing user {}, falling through to email/auto-register",
                    binding.getId(), binding.getUserId());
        }

        // 优先级 2：邮箱命中（仅当 IdP 提供了邮箱）
        if (info.getEmail() != null && !info.getEmail().isEmpty()) {
            SysUser existing = userMapper.checkEmailUnique(info.getEmail());
            if (existing != null) {
                createBinding(existing.getUserId(), provider.getId(), info);
                return existing;
            }
        }

        // 优先级 3：自动注册
        return autoRegister(provider, info);
    }

    private SysUser autoRegister(SysSsoProvider provider, SsoUserInfo info) {
        SysUser u = new SysUser();
        // username = code_externalId，避免与本地账号冲突
        u.setUsername(provider.getCode() + "_" + info.getExternalUserId());
        u.setNickname(info.getNickname() != null ? info.getNickname() : info.getUsername());
        u.setEmail(info.getEmail());
        u.setAvatar(info.getAvatar());
        u.setUserType("01");  // 01=外部 SSO 用户（与本地 00 区分）
        u.setStatus(SystemConstants.STATUS_NORMAL);
        u.setDeptId(provider.getDefaultDeptId());
        // 随机密码占位（不允许密码登录，因 username 含下划线 + 后端登录走密码路径会被锁）
        u.setPassword(passwordEncoder.encode(UUID.randomUUID().toString()));
        userMapper.insert(u);

        // 赋默认角色
        if (provider.getDefaultRoleId() != null) {
            userMapper.batchUserRole(u.getUserId(), Collections.singletonList(provider.getDefaultRoleId()));
        }
        createBinding(u.getUserId(), provider.getId(), info);
        return u;
    }

    private void createBinding(Long userId, Long providerId, SsoUserInfo info) {
        SysUserSsoBinding b = new SysUserSsoBinding();
        b.setUserId(userId);
        b.setProviderId(providerId);
        b.setExternalUserId(info.getExternalUserId());
        b.setExternalUsername(info.getUsername());
        b.setEmail(info.getEmail());
        b.setBindTime(new Date());
        bindingMapper.insert(b);
    }

    private void issueJwtAndCookie(SysUser user, HttpServletResponse response) {
        LoginUser loginUser = new LoginUser(user, new HashSet<>(
                menuService.selectMenuPermsByUserId(user.getUserId())));

        String token = JwtUtil.generateToken(user.getUsername(),
                jwtProperties.getSecret(), jwtProperties.getExpireTime());
        loginUser.setToken(token);
        loginUser.setLoginTime(System.currentTimeMillis());
        loginUser.setExpireTime(loginUser.getLoginTime() + jwtProperties.getExpireTime() * 60_000L);

        redisUtil.set(SystemConstants.LOGIN_TOKEN_KEY + token,
                loginUser, jwtProperties.getExpireTime(), TimeUnit.MINUTES);

        String cookieHeader = SystemConstants.TOKEN_COOKIE_NAME + "=" + token
                + "; Path=/"
                + "; HttpOnly"
                + "; Max-Age=" + (jwtProperties.getExpireTime() * 60)
                + "; SameSite=Lax";
        response.addHeader("Set-Cookie", cookieHeader);
    }

    private SysSsoProvider requireEnabled(String code) {
        SysSsoProvider p = providerService.getEnabledByCode(code);
        if (p == null) throw new RuntimeException("SSO provider not found or disabled: " + code);
        return p;
    }

    private String callbackUri(String code) {
        return ssoProperties.getCallbackBaseUrl() + "/sso/callback/" + code;
    }
}
