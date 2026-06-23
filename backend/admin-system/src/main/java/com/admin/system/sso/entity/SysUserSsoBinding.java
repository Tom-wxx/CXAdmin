package com.admin.system.sso.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.util.Date;

@Data
@TableName("sys_user_sso_binding")
public class SysUserSsoBinding {
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private Long userId;
    private Long providerId;
    private String externalUserId;
    private String externalUsername;
    private String email;
    private Date bindTime;
}
