package com.admin.system.sso.service;

import javax.servlet.http.HttpServletResponse;

public interface ISsoLoginService {
    /** 构造跳转 URL（含 state，state 写 Redis 5min）。 */
    String buildAuthorizationUrl(String code);
    /** 处理回调：换 token / 拉 userinfo / 查或建用户 / 签 JWT / 写 Cookie。返回前端重定向地址。 */
    String handleCallback(String code, String authCode, String state, HttpServletResponse response);
}
