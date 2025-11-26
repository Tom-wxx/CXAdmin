package com.admin.system.config;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

/**
 * JWT配置属性
 *
 * @author Admin
 */
@Data
@Component
@ConfigurationProperties(prefix = "jwt")
public class JwtProperties {

    /**
     * 密钥
     */
    private String secret;

    /**
     * 过期时间（分钟）
     */
    private Integer expireTime;

    /**
     * 刷新时间（分钟）
     */
    private Integer refreshTime;

}
