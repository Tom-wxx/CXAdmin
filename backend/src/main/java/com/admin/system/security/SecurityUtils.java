package com.admin.system.security;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * Security工具类
 *
 * @author Admin
 */
public class SecurityUtils {

    /**
     * 获取当前用户
     */
    public static LoginUser getLoginUser() {
        try {
            return (LoginUser) getAuthentication().getPrincipal();
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 获取Authentication
     */
    public static Authentication getAuthentication() {
        return SecurityContextHolder.getContext().getAuthentication();
    }

    /**
     * 获取用户名
     */
    public static String getUsername() {
        try {
            LoginUser loginUser = getLoginUser();
            return loginUser != null ? loginUser.getUsername() : "系统";
        } catch (Exception e) {
            return "系统";
        }
    }

    /**
     * 获取用户ID
     */
    public static Long getUserId() {
        try {
            LoginUser loginUser = getLoginUser();
            return loginUser != null ? loginUser.getUserId() : null;
        } catch (Exception e) {
            return null;
        }
    }

    /**
     * 生成BCrypt密码
     */
    public static String encryptPassword(String password) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.encode(password);
    }

    /**
     * 判断密码是否匹配
     */
    public static boolean matchesPassword(String rawPassword, String encodedPassword) {
        BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
        return passwordEncoder.matches(rawPassword, encodedPassword);
    }

}
