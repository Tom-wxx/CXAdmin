package com.admin.system.sso.vo;

import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.util.Date;

/**
 * 个人中心 / 管理后台展示用：一条 IdP 绑定记录 + IdP 元信息。
 */
@Data
public class SsoBindingVO {
    private Long id;
    private Long providerId;
    private String providerCode;
    private String providerName;
    private String providerIcon;
    private String externalUserId;
    private String externalUsername;
    private String email;
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date bindTime;
}
