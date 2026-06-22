package com.admin.system.sso.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Data
@Component
@ConfigurationProperties(prefix = "admin.sso")
public class SsoProperties {
    /** Base64 编码的 AES-256 密钥（32 字节解码后）。 */
    private String cryptoKey;
    /** 后端公网基础地址，会拼成 {base}/sso/callback/{code} 作为 IdP 的 redirect_uri。 */
    private String callbackBaseUrl;
    /** 前端基础地址，登录成功后会跳 {front}/index。 */
    private String frontBaseUrl;
    /** 出站代理（访问 IdP token/userinfo 时使用）。host 为空表示直连。 */
    private Proxy proxy = new Proxy();

    @Data
    public static class Proxy {
        private String host;
        private Integer port;
    }
}
