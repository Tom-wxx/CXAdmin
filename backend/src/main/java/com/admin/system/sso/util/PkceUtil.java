package com.admin.system.sso.util;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.SecureRandom;
import java.util.Base64;

/**
 * PKCE（RFC 7636）辅助工具。仅支持 S256 method（明文 plain 是反模式，不支持）。
 *
 * 用法：
 *   String verifier  = PkceUtil.generateCodeVerifier();
 *   String challenge = PkceUtil.s256Challenge(verifier);
 *
 *   // 授权 URL：?code_challenge={challenge}&code_challenge_method=S256
 *   // Token POST：&code_verifier={verifier}
 */
public final class PkceUtil {

    private static final SecureRandom RND = new SecureRandom();
    private static final Base64.Encoder URL_ENCODER = Base64.getUrlEncoder().withoutPadding();

    private PkceUtil() {}

    /** 生成 43-128 字符的随机 verifier（这里固定 96 chars / 64 字节，落在 RFC 推荐范围中段）。 */
    public static String generateCodeVerifier() {
        byte[] bytes = new byte[64];
        RND.nextBytes(bytes);
        return URL_ENCODER.encodeToString(bytes);
    }

    /** SHA-256(verifier) 然后 base64url，无 padding。 */
    public static String s256Challenge(String verifier) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            byte[] hash = md.digest(verifier.getBytes(StandardCharsets.US_ASCII));
            return URL_ENCODER.encodeToString(hash);
        } catch (Exception e) {
            throw new IllegalStateException("SHA-256 unavailable", e);
        }
    }
}
