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
    /** 前端回调的基础地址，会拼成 {base}/sso/callback。 */
    private String callbackBaseUrl;
}
