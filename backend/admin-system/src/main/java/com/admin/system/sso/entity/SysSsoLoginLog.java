package com.admin.system.sso.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * SSO 审计日志：每一次 authorize / callback / bind / unbind 都记一条。
 */
@Data
@TableName("sys_sso_login_log")
public class SysSsoLoginLog implements Serializable {
    private static final long serialVersionUID = 1L;

    public static final String ACTION_AUTHORIZE = "authorize";
    public static final String ACTION_CALLBACK  = "callback";
    public static final String ACTION_BIND      = "bind";
    public static final String ACTION_UNBIND    = "unbind";

    public static final String STATUS_SUCCESS = "success";
    public static final String STATUS_FAIL    = "fail";

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long providerId;
    private String providerCode;
    private Long userId;
    private String externalUserId;

    /** authorize / callback / bind / unbind */
    private String action;
    /** login / bind（仅 callback 时有值）*/
    private String mode;
    /** success / fail */
    private String status;

    private String ip;
    private String userAgent;
    private String errorMessage;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date createTime;
}
