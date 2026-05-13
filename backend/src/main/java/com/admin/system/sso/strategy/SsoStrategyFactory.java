package com.admin.system.sso.strategy;

import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class SsoStrategyFactory {
    private final List<SsoStrategy> strategies;
    private final Map<String, SsoStrategy> byType = new HashMap<>();

    @PostConstruct
    void init() {
        for (SsoStrategy s : strategies) {
            byType.put(s.supports(), s);
        }
    }

    public SsoStrategy get(String type) {
        SsoStrategy s = byType.get(type);
        if (s == null) throw new RuntimeException("No SSO strategy for type: " + type);
        return s;
    }
}
