package com.admin.system.sso.strategy;

import com.admin.system.sso.config.SsoProperties;
import com.admin.system.sso.dto.SsoUserInfo;
import com.admin.system.sso.entity.SysSsoProvider;
import com.admin.system.sso.util.SsoCryptoUtil;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.*;
import org.springframework.http.client.SimpleClientHttpRequestFactory;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.InetSocketAddress;
import java.net.Proxy;
import java.util.Map;
import java.util.HashMap;

@Slf4j
@Component
public class GenericOAuth2Strategy implements SsoStrategy {
    private static final String TYPE = "OAUTH2_GENERIC";

    private final SsoCryptoUtil crypto;
    private final ObjectMapper mapper = new ObjectMapper();
    private final RestTemplate http;

    public GenericOAuth2Strategy(SsoCryptoUtil crypto, SsoProperties ssoProperties) {
        this.crypto = crypto;
        this.http = buildRestTemplate(ssoProperties);
    }

    private static RestTemplate buildRestTemplate(SsoProperties props) {
        SsoProperties.Proxy proxyCfg = props.getProxy();
        SimpleClientHttpRequestFactory factory = new SimpleClientHttpRequestFactory();
        factory.setConnectTimeout(10_000);
        factory.setReadTimeout(15_000);
        if (proxyCfg != null && proxyCfg.getHost() != null && !proxyCfg.getHost().isEmpty()
                && proxyCfg.getPort() != null) {
            factory.setProxy(new Proxy(Proxy.Type.HTTP,
                    new InetSocketAddress(proxyCfg.getHost(), proxyCfg.getPort())));
            log.info("SSO RestTemplate using HTTP proxy {}:{}", proxyCfg.getHost(), proxyCfg.getPort());
        }
        return new RestTemplate(factory);
    }

    @Override
    public String supports() { return TYPE; }

    @Override
    public String buildAuthorizationUrl(SysSsoProvider p, String state, String redirectUri) {
        return UriComponentsBuilder.fromUriString(p.getAuthorizationUri())
                .queryParam("response_type", "code")
                .queryParam("client_id", p.getClientId())
                .queryParam("redirect_uri", redirectUri)
                .queryParam("scope", p.getScope() == null ? "" : p.getScope())
                .queryParam("state", state)
                .encode().toUriString();
    }

    @Override
    public SsoUserInfo exchangeAndFetchUser(SysSsoProvider p, String code, String redirectUri) {
        String accessToken = exchangeCode(p, code, redirectUri);
        JsonNode userJson = fetchUserinfo(p, accessToken);
        SsoUserInfo info = mapUser(p, userJson);
        // userinfo 拿不到邮箱时，若配了 emailsUri 则兜底取一次（GitHub 主邮箱默认不公开）
        if ((info.getEmail() == null || info.getEmail().isEmpty())
                && p.getEmailsUri() != null && !p.getEmailsUri().isEmpty()) {
            String email = fetchPrimaryEmail(p.getEmailsUri(), accessToken);
            if (email != null) info.setEmail(email);
        }
        return info;
    }

    /** 调 emailsUri，期望返回 [{email, primary, verified}] 数组。按 primary+verified > primary > verified > 首项 选 */
    private String fetchPrimaryEmail(String url, String accessToken) {
        try {
            HttpHeaders h = new HttpHeaders();
            h.setBearerAuth(accessToken);
            h.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
            ResponseEntity<String> resp = http.exchange(url, HttpMethod.GET, new HttpEntity<>(h), String.class);
            if (!resp.getStatusCode().is2xxSuccessful()) {
                log.warn("emailsUri {} returned {}", url, resp.getStatusCode());
                return null;
            }
            JsonNode arr = mapper.readTree(resp.getBody());
            if (!arr.isArray() || arr.size() == 0) return null;
            return pickEmail(arr, true, true)
                    .orElse(pickEmail(arr, true, false)
                    .orElse(pickEmail(arr, false, true)
                    .orElse(arr.get(0).path("email").asText(null))));
        } catch (Exception e) {
            log.warn("Fetch email list from {} failed: {}", url, e.getMessage());
            return null;
        }
    }

    private java.util.Optional<String> pickEmail(JsonNode arr, boolean requirePrimary, boolean requireVerified) {
        for (JsonNode n : arr) {
            boolean primaryOk = !requirePrimary || n.path("primary").asBoolean(false);
            boolean verifiedOk = !requireVerified || n.path("verified").asBoolean(false);
            if (primaryOk && verifiedOk) {
                String e = n.path("email").asText(null);
                if (e != null && !e.isEmpty()) return java.util.Optional.of(e);
            }
        }
        return java.util.Optional.empty();
    }

    private String exchangeCode(SysSsoProvider p, String code, String redirectUri) {
        HttpHeaders h = new HttpHeaders();
        h.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        h.set(HttpHeaders.ACCEPT, MediaType.APPLICATION_JSON_VALUE);  // GitHub 默认返回 form，加 Accept 强制 JSON

        MultiValueMap<String, String> form = new LinkedMultiValueMap<>();
        form.add("grant_type", "authorization_code");
        form.add("code", code);
        form.add("redirect_uri", redirectUri);
        form.add("client_id", p.getClientId());
        form.add("client_secret", crypto.decrypt(p.getClientSecret()));

        ResponseEntity<String> resp = http.exchange(
                p.getTokenUri(), HttpMethod.POST, new HttpEntity<>(form, h), String.class);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("IdP token endpoint returned " + resp.getStatusCode());
        }
        try {
            JsonNode node = mapper.readTree(resp.getBody());
            String token = node.path("access_token").asText(null);
            if (token == null) throw new RuntimeException("No access_token in response: " + resp.getBody());
            return token;
        } catch (Exception e) {
            throw new RuntimeException("Parse token response failed: " + e.getMessage(), e);
        }
    }

    private JsonNode fetchUserinfo(SysSsoProvider p, String accessToken) {
        HttpHeaders h = new HttpHeaders();
        h.setBearerAuth(accessToken);
        h.setAccept(java.util.Collections.singletonList(MediaType.APPLICATION_JSON));
        ResponseEntity<String> resp = http.exchange(
                p.getUserinfoUri(), HttpMethod.GET, new HttpEntity<>(h), String.class);
        if (!resp.getStatusCode().is2xxSuccessful()) {
            throw new RuntimeException("IdP userinfo endpoint returned " + resp.getStatusCode());
        }
        try {
            return mapper.readTree(resp.getBody());
        } catch (Exception e) {
            throw new RuntimeException("Parse userinfo failed: " + e.getMessage(), e);
        }
    }

    private SsoUserInfo mapUser(SysSsoProvider p, JsonNode user) {
        Map<String, String> mapping = parseMapping(p.getUserFieldMapping());
        SsoUserInfo u = new SsoUserInfo();
        u.setExternalUserId(text(user, mapping.getOrDefault("id", "sub")));
        u.setUsername(text(user, mapping.getOrDefault("username", "preferred_username")));
        u.setNickname(text(user, mapping.getOrDefault("nickname", "name")));
        u.setEmail(text(user, mapping.getOrDefault("email", "email")));
        u.setAvatar(text(user, mapping.getOrDefault("avatar", "picture")));
        if (u.getExternalUserId() == null) {
            throw new RuntimeException("IdP did not return required user id (mapping key 'id')");
        }
        return u;
    }

    private Map<String, String> parseMapping(String json) {
        if (json == null || json.isEmpty()) return new HashMap<>();
        try {
            return mapper.readValue(json, new com.fasterxml.jackson.core.type.TypeReference<Map<String, String>>(){});
        } catch (Exception e) {
            log.warn("Invalid user_field_mapping JSON, fallback to defaults: {}", e.getMessage());
            return new HashMap<>();
        }
    }

    private String text(JsonNode root, String path) {
        if (path == null) return null;
        JsonNode n = root.path(path);
        return n.isMissingNode() || n.isNull() ? null : n.asText();
    }
}
