package com.admin.system.service;

import com.admin.system.common.constants.SystemConstants;
import com.admin.system.common.exception.ServiceException;
import com.admin.system.dto.RegisterDTO;
import com.admin.system.dto.ResetPasswordDTO;
import com.admin.system.entity.SysUser;
import com.admin.system.mapper.SysUserMapper;
import com.admin.system.service.impl.RegisterServiceImpl;
import com.admin.system.utils.MailService;
import com.admin.system.utils.RedisUtil;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.password.PasswordEncoder;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class RegisterServiceTest {

    @InjectMocks
    private RegisterServiceImpl registerService;

    @Mock private SysUserMapper userMapper;
    @Mock private RedisUtil redisUtil;
    @Mock private PasswordEncoder passwordEncoder;
    @Mock private MailService mailService;

    private RegisterDTO validDto() {
        RegisterDTO dto = new RegisterDTO();
        dto.setUuid("uuid1");
        dto.setCode("abcd");
        dto.setUsername("newuser");
        dto.setPassword("pass123");
        dto.setNickname("Nick");
        dto.setEmail("new@example.com");
        return dto;
    }

    @Test
    void register_whenCaptchaExpired_throws() {
        RegisterDTO dto = validDto();
        when(redisUtil.get(SystemConstants.CAPTCHA_KEY + "uuid1")).thenReturn(null);
        ServiceException ex = assertThrows(ServiceException.class, () -> registerService.register(dto));
        assertEquals("验证码已过期", ex.getMessage());
    }

    @Test
    void register_whenCaptchaWrong_throws() {
        RegisterDTO dto = validDto();
        when(redisUtil.get(SystemConstants.CAPTCHA_KEY + "uuid1")).thenReturn("xyzw");
        ServiceException ex = assertThrows(ServiceException.class, () -> registerService.register(dto));
        assertEquals("验证码错误", ex.getMessage());
    }

    @Test
    void register_whenUsernameExists_throws() {
        RegisterDTO dto = validDto();
        when(redisUtil.get(SystemConstants.CAPTCHA_KEY + "uuid1")).thenReturn("abcd");
        when(userMapper.checkUsernameUnique("newuser")).thenReturn(new SysUser());
        ServiceException ex = assertThrows(ServiceException.class, () -> registerService.register(dto));
        assertTrue(ex.getMessage().contains("已存在"));
    }

    @Test
    void register_whenEmailExists_throws() {
        RegisterDTO dto = validDto();
        when(redisUtil.get(SystemConstants.CAPTCHA_KEY + "uuid1")).thenReturn("abcd");
        when(userMapper.checkUsernameUnique("newuser")).thenReturn(null);
        when(userMapper.checkEmailUnique("new@example.com")).thenReturn(new SysUser());
        ServiceException ex = assertThrows(ServiceException.class, () -> registerService.register(dto));
        assertEquals("邮箱已被注册", ex.getMessage());
    }

    @Test
    void register_whenValid_insertsUser() {
        RegisterDTO dto = validDto();
        when(redisUtil.get(SystemConstants.CAPTCHA_KEY + "uuid1")).thenReturn("abcd");
        when(userMapper.checkUsernameUnique("newuser")).thenReturn(null);
        when(userMapper.checkEmailUnique("new@example.com")).thenReturn(null);
        when(passwordEncoder.encode("pass123")).thenReturn("encoded");
        when(userMapper.insert(any(SysUser.class))).thenReturn(1);

        assertDoesNotThrow(() -> registerService.register(dto));
        verify(userMapper).insert(argThat(u ->
            "newuser".equals(u.getUsername()) &&
            "Nick".equals(u.getNickname()) &&
            "new@example.com".equals(u.getEmail()) &&
            "encoded".equals(u.getPassword())
        ));
    }

    @Test
    void resetPassword_whenTokenExpired_throws() {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken("expired");
        dto.setNewPassword("newpass123");
        when(redisUtil.get(SystemConstants.RESET_PWD_KEY + "expired")).thenReturn(null);
        ServiceException ex = assertThrows(ServiceException.class, () -> registerService.resetPassword(dto));
        assertTrue(ex.getMessage().contains("已过期"));
    }

    @Test
    void forgotPassword_whenEmailNotFound_doesNothingAndSilent() {
        when(userMapper.checkEmailUnique("unknown@example.com")).thenReturn(null);
        assertDoesNotThrow(() -> registerService.forgotPassword("unknown@example.com"));
        verify(redisUtil, never()).set(anyString(), any(), anyLong(), any());
        verify(mailService, never()).sendResetPasswordEmail(anyString(), anyString());
    }

    @Test
    void resetPassword_whenTokenValid_updatesPasswordAndDeletesKey() {
        ResetPasswordDTO dto = new ResetPasswordDTO();
        dto.setToken("valid-token");
        dto.setNewPassword("newpass123");
        when(redisUtil.get(SystemConstants.RESET_PWD_KEY + "valid-token")).thenReturn(42L);
        when(passwordEncoder.encode("newpass123")).thenReturn("encoded-new");
        when(userMapper.updateById(any(SysUser.class))).thenReturn(1);

        assertDoesNotThrow(() -> registerService.resetPassword(dto));
        verify(userMapper).updateById(argThat(u ->
            u.getUserId().equals(42L) && "encoded-new".equals(u.getPassword())
        ));
        verify(redisUtil).delete(SystemConstants.RESET_PWD_KEY + "valid-token");
    }
}
