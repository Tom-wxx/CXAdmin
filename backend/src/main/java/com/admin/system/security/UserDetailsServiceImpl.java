package com.admin.system.security;

import com.admin.system.entity.SysUser;
import com.admin.system.service.ISysMenuService;
import com.admin.system.service.ISysUserService;
import com.admin.system.common.constants.SystemConstants;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

/**
 * 用户验证处理
 *
 * @author Admin
 */
@Service
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

    private final ISysUserService userService;
    private final ISysMenuService menuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.selectUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }
        if (SystemConstants.STATUS_DISABLE.equals(user.getStatus())) {
            throw new RuntimeException("用户已被停用");
        }
        // 获取用户权限
        Set<String> permissions = menuService.selectMenuPermsByUserId(user.getUserId());
        return new LoginUser(user, permissions);
    }

}
