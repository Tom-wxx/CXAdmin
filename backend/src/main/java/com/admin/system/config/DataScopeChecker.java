package com.admin.system.config;

import com.admin.system.annotation.DataScope;
import com.admin.system.entity.SysUser;
import com.admin.system.mapper.SysUserMapper;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

/**
 * 数据权限校验器：判断「目标用户」是否落在「当前登录用户的数据范围」内。
 *
 * <p>独立成 Bean，是为了让 {@link DataScope} 切面在被 {@code SysUserServiceImpl} 等
 * <b>跨 Bean</b> 调用时生效（同类自调用不会走 AOP 代理，切面不会触发）。
 * 本类复用与用户列表完全相同的数据范围 SQL 片段，确保单资源校验与列表过滤逻辑一致、不分叉。</p>
 *
 * @author Admin
 */
@Component
@RequiredArgsConstructor
public class DataScopeChecker {

    private final SysUserMapper userMapper;

    /**
     * 统计目标用户在当前登录用户数据范围内的可见数量（0 表示越权）。
     * 超级管理员经切面放行（无过滤片段），结果即为该用户是否存在。
     */
    @DataScope(deptAlias = "d", userAlias = "u")
    public long countUserInScope(SysUser query) {
        return userMapper.countUserInScope(query);
    }
}
