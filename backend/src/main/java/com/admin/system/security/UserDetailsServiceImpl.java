package com.admin.system.security;

import com.admin.system.entity.SysUser;
import com.admin.system.service.ISysMenuService;
import com.admin.system.service.ISysUserService;
import org.springframework.beans.factory.annotation.Autowired;
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
public class UserDetailsServiceImpl implements UserDetailsService {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysMenuService menuService;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        SysUser user = userService.selectUserByUsername(username);
        if (user == null) {
            throw new UsernameNotFoundException("用户不存在：" + username);
        }
        if ("1".equals(user.getStatus())) {
            throw new RuntimeException("用户已被停用");
        }
        // 获取用户权限
        Set<String> permissions = menuService.selectMenuPermsByUserId(user.getUserId());
        return new LoginUser(user, permissions);
    }

}
