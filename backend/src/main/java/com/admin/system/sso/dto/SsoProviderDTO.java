package com.admin.system.sso.dto;

import lombok.Data;
import javax.validation.constraints.NotBlank;

@Data
public class SsoProviderDTO {
    private Long id;
    @NotBlank private String code;
    @NotBlank private String name;
    @NotBlank private String type;
    private String icon;
    @NotBlank private String clientId;
    /** 明文，由 Service 调用 SsoCryptoUtil 加密后入库；修改时若为空表示不变更 */
    private String clientSecret;
    @NotBlank private String authorizationUri;
    @NotBlank private String tokenUri;
    @NotBlank private String userinfoUri;
    private String scope;
    private String userFieldMapping;
    private Long defaultRoleId;
    private Long defaultDeptId;
    private Integer enabled;
    private Integer orderNum;
    private String remark;
}
