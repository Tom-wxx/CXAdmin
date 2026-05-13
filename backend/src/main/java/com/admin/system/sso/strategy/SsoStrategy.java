package com.admin.system.sso.strategy;

import com.admin.system.sso.dto.SsoUserInfo;
import com.admin.system.sso.entity.SysSsoProvider;

public interface SsoStrategy {
    /** 该策略支持的 provider.type 值。 */
    String supports();

    /** 构造跳转到 IdP 的授权 URL（含 state、redirect_uri）。 */
    String buildAuthorizationUrl(SysSsoProvider provider, String state, String redirectUri);

    /** 用 code 换 access_token，再拉 userinfo，归一化返回。 */
    SsoUserInfo exchangeAndFetchUser(SysSsoProvider provider, String code, String redirectUri);
}
