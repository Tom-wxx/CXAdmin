package com.admin.system.service.impl;

import com.admin.system.common.constants.SystemConstants;
import com.admin.system.common.exception.ServiceException;
import com.admin.system.dto.RegisterDTO;
import com.admin.system.dto.ResetPasswordDTO;
import com.admin.system.entity.SysUser;
import com.admin.system.mapper.SysUserMapper;
import com.admin.system.service.IRegisterService;
import com.admin.system.utils.MailService;
import com.admin.system.utils.RedisUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Slf4j
@Service
@RequiredArgsConstructor
public class RegisterServiceImpl implements IRegisterService {

    private final SysUserMapper userMapper;
    private final RedisUtil redisUtil;
    private final PasswordEncoder passwordEncoder;
    private final MailService mailService;

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void register(RegisterDTO dto) {
        String captchaKey = SystemConstants.CAPTCHA_KEY + dto.getUuid();
        Object captcha = redisUtil.get(captchaKey);
        if (captcha == null) {
            throw new ServiceException("验证码已过期");
        }
        if (!dto.getCode().toLowerCase().equals(captcha.toString())) {
            throw new ServiceException("验证码错误");
        }

        if (userMapper.checkUsernameUnique(dto.getUsername()) != null) {
            throw new ServiceException("用户名 '" + dto.getUsername() + "' 已存在");
        }
        if (userMapper.checkEmailUnique(dto.getEmail()) != null) {
            throw new ServiceException("邮箱已被注册");
        }

        SysUser user = new SysUser();
        user.setUsername(dto.getUsername());
        user.setNickname(dto.getNickname());
        user.setEmail(dto.getEmail());
        user.setPassword(passwordEncoder.encode(dto.getPassword()));
        user.setStatus(SystemConstants.STATUS_NORMAL);
        user.setUserType("00");
        userMapper.insert(user);
        redisUtil.delete(captchaKey);
    }

    @Override
    public void forgotPassword(String email) {
        SysUser user = userMapper.checkEmailUnique(email);
        if (user != null) {
            String token = UUID.randomUUID().toString();
            String redisKey = SystemConstants.RESET_PWD_KEY + token;
            redisUtil.set(redisKey,
                    user.getUserId(),
                    SystemConstants.RESET_PWD_EXPIRATION,
                    TimeUnit.MINUTES);
            try {
                mailService.sendResetPasswordEmail(email, token);
            } catch (Exception e) {
                log.error("发送重置密码邮件失败: {}", email, e);
                redisUtil.delete(redisKey);
            }
        }
    }

    @Override
    @Transactional(rollbackFor = Exception.class)
    public void resetPassword(ResetPasswordDTO dto) {
        Object userIdObj = redisUtil.get(SystemConstants.RESET_PWD_KEY + dto.getToken());
        if (userIdObj == null) {
            throw new ServiceException("重置链接已过期或无效，请重新申请");
        }
        Long userId = ((Number) userIdObj).longValue();
        SysUser user = new SysUser();
        user.setUserId(userId);
        user.setPassword(passwordEncoder.encode(dto.getNewPassword()));
        int updated = userMapper.updateById(user);
        if (updated == 0) {
            throw new ServiceException("用户不存在或已被删除");
        }
        redisUtil.delete(SystemConstants.RESET_PWD_KEY + dto.getToken());
    }
}
