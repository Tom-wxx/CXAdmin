package com.admin.system.config;

import com.admin.system.security.LoginUser;
import com.admin.system.security.SecurityUtils;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import java.util.Set;

/**
 * 自定义权限验证
 *
 * @author Admin
 */
@Service("ss")
public class PermissionService {

    /**
     * 所有权限标识
     */
    private static final String ALL_PERMISSION = "*:*:*";

    /**
     * 验证用户是否具备某权限
     */
    public boolean hasPermi(String permission) {
        if (permission == null || permission.isEmpty()) {
            return false;
        }
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null || CollectionUtils.isEmpty(loginUser.getPermissions())) {
            return false;
        }
        return hasPermissions(loginUser.getPermissions(), permission);
    }

    /**
     * 判断是否包含权限
     */
    private boolean hasPermissions(Set<String> permissions, String permission) {
        return permissions.contains(ALL_PERMISSION) || permissions.contains(permission);
    }

}
