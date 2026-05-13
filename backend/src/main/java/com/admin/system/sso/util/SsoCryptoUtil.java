package com.admin.system.sso.util;

import com.admin.system.sso.config.SsoProperties;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.ByteBuffer;
import java.security.SecureRandom;
import java.util.Base64;

@Component
@RequiredArgsConstructor
public class SsoCryptoUtil {
    private static final int IV_LEN = 12;
    private static final int TAG_LEN = 128;
    private static final SecureRandom RNG = new SecureRandom();

    private final SsoProperties props;

    public String encrypt(String plaintext) {
        if (plaintext == null) return null;
        try {
            byte[] iv = new byte[IV_LEN];
            RNG.nextBytes(iv);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.ENCRYPT_MODE, key(), new GCMParameterSpec(TAG_LEN, iv));
            byte[] ct = cipher.doFinal(plaintext.getBytes("UTF-8"));
            ByteBuffer buf = ByteBuffer.allocate(IV_LEN + ct.length);
            buf.put(iv).put(ct);
            return Base64.getEncoder().encodeToString(buf.array());
        } catch (Exception e) {
            throw new RuntimeException("SSO secret encryption failed", e);
        }
    }

    public String decrypt(String ciphertext) {
        if (ciphertext == null || ciphertext.isEmpty()) return null;
        try {
            byte[] all = Base64.getDecoder().decode(ciphertext);
            byte[] iv = new byte[IV_LEN];
            byte[] ct = new byte[all.length - IV_LEN];
            System.arraycopy(all, 0, iv, 0, IV_LEN);
            System.arraycopy(all, IV_LEN, ct, 0, ct.length);
            Cipher cipher = Cipher.getInstance("AES/GCM/NoPadding");
            cipher.init(Cipher.DECRYPT_MODE, key(), new GCMParameterSpec(TAG_LEN, iv));
            return new String(cipher.doFinal(ct), "UTF-8");
        } catch (Exception e) {
            throw new RuntimeException("SSO secret decryption failed", e);
        }
    }

    private SecretKeySpec key() {
        byte[] k = Base64.getDecoder().decode(props.getCryptoKey());
        if (k.length != 32) throw new IllegalStateException("admin.sso.crypto-key must decode to 32 bytes");
        return new SecretKeySpec(k, "AES");
    }
}
