package com.admin.system.sso.service.impl;

import com.admin.system.common.constants.SystemConstants;
import com.admin.system.config.JwtProperties;
import com.admin.system.entity.SysUser;
import com.admin.system.mapper.SysUserMapper;
import com.admin.system.security.LoginUser;
import com.admin.system.service.ISysMenuService;
import com.admin.system.sso.config.SsoProperties;
import com.admin.system.sso.dto.SsoStateData;
import com.admin.system.sso.dto.SsoUserInfo;
import com.admin.system.sso.entity.SysSsoProvider;
import com.admin.system.sso.entity.SysUserSsoBinding;
import com.admin.system.sso.mapper.SysUserSsoBindingMapper;
import com.admin.system.sso.service.ISsoLoginService;
import com.admin.system.sso.service.ISysSsoProviderService;
import com.admin.system.sso.strategy.SsoStrategy;
import com.admin.system.sso.strategy.SsoStrategyFactory;
import com.admin.system.sso.vo.SsoBindingVO;
import com.admin.system.utils.JwtUtil;
import com.admin.system.utils.RedisUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import javax.servlet.http.HttpServletResponse;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashSet;
import java.util.List;
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
        return buildAuthorizationUrlInternal(code, SsoStateData.login(code));
    }

    @Override
    public String buildBindAuthorizationUrl(String code, Long userId) {
        if (userId == null) throw new RuntimeException("绑定 SSO 需要登录态");
        return buildAuthorizationUrlInternal(code, SsoStateData.bind(code, userId));
    }

    private String buildAuthorizationUrlInternal(String code, SsoStateData stateData) {
        SysSsoProvider provider = requireEnabled(code);
        SsoStrategy strategy = strategyFactory.get(provider.getType());

        String state = UUID.randomUUID().toString().replace("-", "");
        redisUtil.set(STATE_KEY + state, stateData, STATE_TTL_MIN, TimeUnit.MINUTES);

        return strategy.buildAuthorizationUrl(provider, state, callbackUri(code));
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public String handleCallback(String code, String authCode, String state, HttpServletResponse response) {
        // 1. state 校验（一次性消费）+ 取出 mode
        String redisKey = STATE_KEY + state;
        Object cached = redisUtil.get(redisKey);
        if (!(cached instanceof SsoStateData)) {
            throw new RuntimeException("Invalid or expired SSO state");
        }
        SsoStateData stateData = (SsoStateData) cached;
        if (!code.equals(stateData.getCode())) {
            throw new RuntimeException("SSO state code mismatch");
        }
        redisUtil.delete(redisKey);

        SysSsoProvider provider = requireEnabled(code);
        SsoStrategy strategy = strategyFactory.get(provider.getType());

        // 2. 换 token + 拉 userinfo
        SsoUserInfo info = strategy.exchangeAndFetchUser(provider, authCode, callbackUri(code));

        // 3. 分支：登录 vs 绑定
        if (SsoStateData.MODE_BIND.equals(stateData.getMode())) {
            return handleBindCallback(provider, info, stateData.getUserId());
        }
        SysUser user = resolveUser(provider, info);
        issueJwtAndCookie(user, response);
        return ssoProperties.getFrontBaseUrl() + "/index";
    }

    /** 已登录用户绑定流程：禁止 IdP 账号已被他人占用；冪等允许重复绑定同一个 external_user。 */
    private String handleBindCallback(SysSsoProvider provider, SsoUserInfo info, Long userId) {
        SysUserSsoBinding existing = bindingMapper.selectOne(
                new LambdaQueryWrapper<SysUserSsoBinding>()
                        .eq(SysUserSsoBinding::getProviderId, provider.getId())
                        .eq(SysUserSsoBinding::getExternalUserId, info.getExternalUserId()));
        if (existing != null && !existing.getUserId().equals(userId)) {
            throw new RuntimeException("该 " + provider.getName() + " 账号已绑定其它用户");
        }
        if (existing == null) {
            createBinding(userId, provider.getId(), info);
        } else {
            // 同一用户重复绑定：刷新 username/email/bindTime
            existing.setExternalUsername(info.getUsername());
            existing.setEmail(info.getEmail());
            existing.setBindTime(new Date());
            bindingMapper.updateById(existing);
        }
        return ssoProperties.getFrontBaseUrl() + "/profile?tab=bindings&bind=ok";
    }

    @Override
    public List<SsoBindingVO> listMyBindings(Long userId) {
        if (userId == null) return Collections.emptyList();
        List<SysUserSsoBinding> rows = bindingMapper.selectList(
                new LambdaQueryWrapper<SysUserSsoBinding>()
                        .eq(SysUserSsoBinding::getUserId, userId)
                        .orderByDesc(SysUserSsoBinding::getBindTime));
        if (rows.isEmpty()) return Collections.emptyList();
        List<SsoBindingVO> result = new ArrayList<>(rows.size());
        for (SysUserSsoBinding b : rows) {
            SysSsoProvider p = providerService.getById(b.getProviderId());
            SsoBindingVO vo = new SsoBindingVO();
            vo.setId(b.getId());
            vo.setProviderId(b.getProviderId());
            vo.setExternalUserId(b.getExternalUserId());
            vo.setExternalUsername(b.getExternalUsername());
            vo.setEmail(b.getEmail());
            vo.setBindTime(b.getBindTime());
            if (p != null) {
                vo.setProviderCode(p.getCode());
                vo.setProviderName(p.getName());
                vo.setProviderIcon(p.getIcon());
            }
            result.add(vo);
        }
        return result;
    }

    @Override
    public void unbind(Long bindingId, Long userId) {
        SysUserSsoBinding b = bindingMapper.selectById(bindingId);
        if (b == null) throw new RuntimeException("绑定记录不存在");
        if (!b.getUserId().equals(userId)) throw new RuntimeException("不能解除他人的绑定");
        bindingMapper.deleteById(bindingId);
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

        int maxAgeSec = (int) (jwtProperties.getExpireTime() * 60);
        // 1) HttpOnly JWT，后端 JwtAuthenticationFilter 用
        response.addHeader("Set-Cookie",
                SystemConstants.TOKEN_COOKIE_NAME + "=" + token
                        + "; Path=/"
                        + "; HttpOnly"
                        + "; Max-Age=" + maxAgeSec
                        + "; SameSite=Lax");
        // 2) 非 HttpOnly 的会话标记，前端路由守卫 getToken() 用
        response.addHeader("Set-Cookie",
                SystemConstants.SESSION_COOKIE_NAME + "=1"
                        + "; Path=/"
                        + "; Max-Age=" + maxAgeSec
                        + "; SameSite=Lax");
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
