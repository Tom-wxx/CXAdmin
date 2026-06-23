package com.admin.framework.config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Component;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

/**
 * 启动期安全守卫：当激活 {@code prod} profile 时，拒绝仍在使用「仓库内置 dev 默认密钥/口令」启动，
 * 并校验 JWT 密钥强度。把"忘记覆盖密钥"从隐患变为可见的快速失败。
 *
 * <p>非 prod（如 dev）不做任何拦截，保证本地开箱即用。</p>
 *
 * @author Admin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class EnvironmentGuard {

    private final Environment env;

    /** 已知且入库的 dev 默认值：prod 下若仍是这些值即视为未覆盖。 */
    private static final Map<String, String> FORBIDDEN_DEFAULTS = new LinkedHashMap<>();

    static {
        FORBIDDEN_DEFAULTS.put("jwt.secret",
                "abcdefghijklmnopqrstuvwxyz123456ABCDEFGHIJKLMNOPQRSTUVWXYZ789012");
        FORBIDDEN_DEFAULTS.put("admin.sso.crypto-key",
                "q1bzST8qQgcOxwv9f6l/vGVODv5NrlauMkDMr762jUQ=");
        FORBIDDEN_DEFAULTS.put("spring.datasource.druid.password", "root");
        FORBIDDEN_DEFAULTS.put("spring.datasource.druid.stat-view-servlet.login-password", "admin");
    }

    /** HS512 要求密钥 ≥ 64 字节。 */
    private static final int MIN_JWT_SECRET_BYTES = 64;

    @PostConstruct
    public void verify() {
        if (!isProdActive()) {
            return;
        }

        List<String> violations = new ArrayList<>();

        FORBIDDEN_DEFAULTS.forEach((key, badDefault) -> {
            String value = env.getProperty(key);
            if (badDefault.equals(value)) {
                violations.add("配置项 [" + key + "] 仍为仓库内置默认值，必须经环境变量覆盖");
            }
        });

        String jwtSecret = env.getProperty("jwt.secret");
        if (jwtSecret == null || jwtSecret.getBytes(StandardCharsets.UTF_8).length < MIN_JWT_SECRET_BYTES) {
            violations.add("配置项 [jwt.secret] 强度不足：HS512 要求 ≥ " + MIN_JWT_SECRET_BYTES + " 字节");
        }

        if (!violations.isEmpty()) {
            String msg = "生产环境(prod)安全自检未通过，已阻止启动：\n  - "
                    + String.join("\n  - ", violations)
                    + "\n请通过环境变量注入强随机密钥/口令后重试。";
            log.error(msg);
            throw new IllegalStateException(msg);
        }

        log.info("生产环境(prod)安全自检通过：密钥/口令均非默认值。");
    }

    private boolean isProdActive() {
        for (String profile : env.getActiveProfiles()) {
            if ("prod".equalsIgnoreCase(profile)) {
                return true;
            }
        }
        return false;
    }
}
