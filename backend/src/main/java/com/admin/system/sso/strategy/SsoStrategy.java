package com.admin.system.sso.strategy;

import com.admin.system.sso.dto.SsoUserInfo;
import com.admin.system.sso.entity.SysSsoProvider;

public interface SsoStrategy {
    /** 该策略支持的 provider.type 值。 */
    String supports();

    /**
     * 构造跳转到 IdP 的授权 URL。
     * @param codeChallenge PKCE 的 S256 challenge；null = 不启用 PKCE
     */
    String buildAuthorizationUrl(SysSsoProvider provider, String state, String redirectUri, String codeChallenge);

    /**
     * 用 code 换 access_token，再拉 userinfo，归一化返回。
     * @param codeVerifier PKCE 的明文 verifier；null = 不启用 PKCE
     */
    SsoUserInfo exchangeAndFetchUser(SysSsoProvider provider, String code, String redirectUri, String codeVerifier);
}
