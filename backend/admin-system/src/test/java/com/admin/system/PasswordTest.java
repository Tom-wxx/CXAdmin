package com.admin.system;

import org.junit.jupiter.api.Test;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 密码测试工具
 */
public class PasswordTest {

    @Test
    public void testPassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        String rawPassword = "admin123";
        String encodedPassword = "$2a$10$7JB720yubVSZvUI0rEqK/.VqGOZTH.ulu33dHOiBE/TU3YqX0FJxy";

        // 验证密码是否匹配
        boolean matches = encoder.matches(rawPassword, encodedPassword);
        System.out.println("密码匹配结果: " + matches);

        // 生成新的密码
        String newEncoded = encoder.encode(rawPassword);
        System.out.println("新生成的密码: " + newEncoded);
        System.out.println("新密码验证: " + encoder.matches(rawPassword, newEncoded));
    }

    @Test
    public void generatePassword() {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
        String password = "admin123";
        String encoded = encoder.encode(password);
        System.out.println("admin123 的加密密码: " + encoded);
    }
}
