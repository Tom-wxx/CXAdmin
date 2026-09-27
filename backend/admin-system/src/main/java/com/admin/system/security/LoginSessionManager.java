package com.admin.system.security;

import com.admin.common.config.JwtProperties;
import com.admin.common.constants.SystemConstants;
import com.admin.common.utils.RedisUtil;
import com.admin.system.entity.SysUser;
import com.admin.system.mapper.SysMenuMapper;
import com.admin.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;
import org.springframework.transaction.support.TransactionSynchronization;
import org.springframework.transaction.support.TransactionSynchronizationManager;

import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 登录会话管理：会话读写的唯一入口，并维护「用户 → token 集合」索引。
 *
 * <p>会话本体是 {@code login_tokens:{token}}（见 ADR 0001）；索引 {@code login_user_tokens:{userId}}
 * 让我们能按用户批量踢下线或刷新权限 —— 停用、删除、改密、改角色后旧会话不再继续生效。</p>
 *
 * <p>失效/刷新动作在当前事务提交后执行（无事务则立即执行），保证读到的是已提交的数据，
 * 且业务回滚时不会误伤会话。</p>
 *
 * @author Admin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class LoginSessionManager {

    private final RedisUtil redisUtil;
    private final JwtProperties jwtProperties;
    private final SysUserMapper userMapper;
    private final SysMenuMapper menuMapper;

    /**
     * 写入（或续期）会话，并登记到该用户的会话索引
     */
    public void save(LoginUser loginUser) {
        long ttl = jwtProperties.getExpireTime();
        redisUtil.set(SystemConstants.LOGIN_TOKEN_KEY + loginUser.getToken(), loginUser, ttl, TimeUnit.MINUTES);
        String indexKey = indexKey(loginUser.getUserId());
        redisUtil.sAdd(indexKey, loginUser.getToken());
        // 每次写入都把索引 TTL 重置为完整时长，所以它总不短于该用户最晚过期的那个会话
        redisUtil.expire(indexKey, ttl, TimeUnit.MINUTES);
    }

    /**
     * 删除单个会话（登出）
     */
    public void remove(LoginUser loginUser) {
        redisUtil.delete(SystemConstants.LOGIN_TOKEN_KEY + loginUser.getToken());
        redisUtil.sRemove(indexKey(loginUser.getUserId()), loginUser.getToken());
    }

    /**
     * 踢掉该用户全部会话（管理员重置密码、找回密码）
     */
    public void removeByUserId(Long userId) {
        removeByUserId(userId, null);
    }

    /**
     * 踢掉该用户除 {@code keepToken} 外的全部会话（本人改密时保留当前会话）
     */
    public void removeByUserId(Long userId, String keepToken) {
        afterCommit(() -> {
            Set<String> tokens = tokensOf(userId);
            if (keepToken != null) {
                tokens.remove(keepToken);
            }
            deleteSessions(userId, tokens);
        });
    }

    /**
     * 按数据库最新状态同步该用户的全部会话：用户已删除/停用则踢下线，否则重载用户信息与权限
     */
    public void syncByUserId(Long userId) {
        afterCommit(() -> doSync(userId));
    }

    /**
     * 同步某角色下所有用户的会话（角色菜单、角色状态变更后）
     */
    public void syncByRoleId(Long roleId) {
        afterCommit(() -> userMapper.selectUserIdsByRoleId(roleId).forEach(this::doSync));
    }

    private void doSync(Long userId) {
        Set<String> tokens = tokensOf(userId);
        if (tokens.isEmpty()) {
            return;
        }

        // selectById 带逻辑删除条件：已删除用户返回 null
        SysUser user = userMapper.selectById(userId);
        if (user == null || SystemConstants.STATUS_DISABLE.equals(user.getStatus())) {
            deleteSessions(userId, tokens);
            return;
        }
        user.setPassword(null);
        Set<String> permissions = menuMapper.selectMenuPermsByUserId(userId);

        for (String token : tokens) {
            String key = SystemConstants.LOGIN_TOKEN_KEY + token;
            Long ttl = redisUtil.getExpire(key);
            if (!(redisUtil.get(key) instanceof LoginUser loginUser) || ttl == null || ttl <= 0) {
                // 会话已过期或被单独踢掉，顺手清理索引里的残留
                redisUtil.sRemove(indexKey(userId), token);
                continue;
            }
            loginUser.setUser(user);
            loginUser.setDeptId(user.getDeptId());
            loginUser.setPermissions(permissions);
            // 保留剩余有效期，刷新权限不应顺带延长会话
            redisUtil.set(key, loginUser, ttl, TimeUnit.SECONDS);
        }
    }

    private void deleteSessions(Long userId, Set<String> tokens) {
        if (tokens.isEmpty()) {
            return;
        }
        List<String> keys = tokens.stream()
                .map(token -> SystemConstants.LOGIN_TOKEN_KEY + token)
                .collect(Collectors.toList());
        redisUtil.delete(keys);
        redisUtil.sRemove(indexKey(userId), tokens.toArray());
        log.info("已失效用户 {} 的 {} 个会话", userId, tokens.size());
    }

    private Set<String> tokensOf(Long userId) {
        Set<Object> members = redisUtil.sMembers(indexKey(userId));
        if (members == null) {
            return new HashSet<>();
        }
        return members.stream().map(String::valueOf).collect(Collectors.toSet());
    }

    private String indexKey(Long userId) {
        return SystemConstants.LOGIN_USER_TOKENS_KEY + userId;
    }

    private void afterCommit(Runnable action) {
        Runnable guarded = () -> {
            try {
                action.run();
            } catch (Exception e) {
                // 业务数据已提交，这里失败只能告警：会话最迟在自然过期时失效
                log.error("会话同步失败，旧会话将保留至自然过期: {}", e.getMessage(), e);
            }
        };
        if (TransactionSynchronizationManager.isSynchronizationActive()) {
            TransactionSynchronizationManager.registerSynchronization(new TransactionSynchronization() {
                @Override
                public void afterCommit() {
                    guarded.run();
                }
            });
        } else {
            guarded.run();
        }
    }
}
