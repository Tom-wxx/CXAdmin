package com.admin.system.sso.dto;

import lombok.Data;

@Data
public class SsoUserInfo {
    /** IdP 上用户唯一 ID（必填） */
    private String externalUserId;
    /** 用户名（建议唯一，自动注册时拼前缀去冲突） */
    private String username;
    private String nickname;
    private String email;
    private String avatar;
}
