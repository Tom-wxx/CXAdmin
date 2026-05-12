package com.admin.system.common.constants;

/**
 * 系统常量
 *
 * @author Admin
 */
public final class SystemConstants {

    private SystemConstants() {
    }

    // ==================== Redis Key 前缀 ====================

    /**
     * 登录用户 Redis key 前缀
     */
    public static final String LOGIN_TOKEN_KEY = "login_tokens:";

    /**
     * 验证码 Redis key 前缀
     */
    public static final String CAPTCHA_KEY = "captcha:";

    /**
     * 登录重试计数 Redis key 前缀
     */
    public static final String LOGIN_RETRY_KEY = "login_retry:";

    // ==================== 用户状态 ====================

    /**
     * 用户正常状态
     */
    public static final String STATUS_NORMAL = "0";

    /**
     * 用户停用状态
     */
    public static final String STATUS_DISABLE = "1";

    // ==================== 通用状态 ====================

    /**
     * 操作成功
     */
    public static final int OPER_SUCCESS = 0;

    /**
     * 操作失败
     */
    public static final int OPER_FAIL = 1;

    // ==================== 系统配置 ====================

    /**
     * 系统内置配置
     */
    public static final String CONFIG_TYPE_BUILTIN = "Y";

    // ==================== 菜单类型 ====================

    /**
     * 菜单类型 - 目录
     */
    public static final String MENU_TYPE_DIR = "M";

    /**
     * 菜单类型 - 菜单
     */
    public static final String MENU_TYPE_MENU = "C";

    /**
     * 菜单类型 - 按钮
     */
    public static final String MENU_TYPE_BUTTON = "F";

    // ==================== 超级管理员 ====================

    /**
     * 超级管理员用户ID
     */
    public static final long SUPER_ADMIN_ID = 1L;

    /**
     * 超级管理员用户名
     */
    public static final String SUPER_ADMIN_USERNAME = "admin";

    /**
     * 默认密码
     */
    public static final String DEFAULT_PASSWORD = "123456";

    // ==================== 验证码 ====================

    /**
     * 验证码有效期（分钟）
     */
    public static final int CAPTCHA_EXPIRATION = 2;

    // ==================== 令牌刷新 ====================

    /**
     * 令牌刷新阈值（毫秒），20分钟
     */
    public static final long TOKEN_REFRESH_THRESHOLD = 20 * 60 * 1000L;

    // ==================== Cookie ====================

    /**
     * HttpOnly Cookie 名称
     */
    public static final String TOKEN_COOKIE_NAME = "Admin-Token";
}
