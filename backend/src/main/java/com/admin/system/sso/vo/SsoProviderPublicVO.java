package com.admin.system.sso.vo;

import lombok.Data;

@Data
public class SsoProviderPublicVO {
    private String code;
    private String name;
    private String icon;
    private Integer orderNum;
}
