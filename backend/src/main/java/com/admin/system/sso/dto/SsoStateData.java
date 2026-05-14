package com.admin.system.sso.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.io.Serializable;

/**
 * 存进 Redis 的 SSO state 数据。
 * mode=login：未登录态发起的登录授权，回调时新建/查找用户
 * mode=bind：已登录用户为自己加绑一个 IdP，回调时把 external_user 写到 sys_user_sso_binding
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
public class SsoStateData implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String MODE_LOGIN = "login";
    public static final String MODE_BIND = "bind";

    /** provider code */
    private String code;
    /** "login" | "bind" */
    private String mode;
    /** 仅 mode=bind 时有值：发起绑定的当前登录用户 ID */
    private Long userId;

    public static SsoStateData login(String code) {
        return new SsoStateData(code, MODE_LOGIN, null);
    }

    public static SsoStateData bind(String code, Long userId) {
        return new SsoStateData(code, MODE_BIND, userId);
    }
}
