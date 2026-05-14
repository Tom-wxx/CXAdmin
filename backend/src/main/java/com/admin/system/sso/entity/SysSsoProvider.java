package com.admin.system.sso.entity;

import com.admin.system.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_sso_provider")
public class SysSsoProvider extends BaseEntity {
    private static final long serialVersionUID = 1L;

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    private String code;
    private String name;
    private String type;
    private String icon;
    private String clientId;
    /** 数据库中是 AES-GCM 密文；明文仅在写入时由 Controller 加密后落库。 */
    private String clientSecret;
    private String authorizationUri;
    private String tokenUri;
    private String userinfoUri;
    /** 可选：当 userinfo 不返回邮箱时调用，期望返回 [{email, primary, verified}] 数组。例如 GitHub: https://api.github.com/user/emails */
    private String emailsUri;
    private String scope;
    /** 是否启用 PKCE（S256）。仅 OAuth2 / OIDC 有效；部分 IdP（如 GitHub OAuth Apps）不支持需保持 0 */
    private Integer enablePkce;
    private String userFieldMapping;
    private Long defaultRoleId;
    private Long defaultDeptId;
    private Integer enabled;
    private Integer orderNum;
}
