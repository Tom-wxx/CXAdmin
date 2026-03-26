package com.admin.system.service.impl;

import com.admin.system.common.constants.SystemConstants;
import com.admin.system.config.JwtProperties;
import com.admin.system.dto.LoginDTO;
import com.admin.system.entity.SysFile;
import com.admin.system.entity.SysLoginLog;
import com.admin.system.entity.SysMenu;
import com.admin.system.entity.SysUser;
import com.admin.system.security.LoginUser;
import com.admin.system.security.SecurityUtils;
import com.admin.system.service.ILoginService;
import com.admin.system.service.ISysFileService;
import com.admin.system.service.ISysLoginLogService;
import com.admin.system.service.ISysMenuService;
import com.admin.system.service.ISysUserService;
import com.admin.system.utils.CaptchaUtil;
import com.admin.system.utils.IpUtil;
import com.admin.system.utils.JwtUtil;
import com.admin.system.utils.RedisUtil;
import com.admin.system.vo.RouterVo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletRequest;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 登录服务实现
 *
 * @author Admin
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class LoginServiceImpl implements ILoginService {

    private final AuthenticationManager authenticationManager;
    private final RedisUtil redisUtil;
    private final JwtProperties jwtProperties;
    private final ISysUserService userService;
    private final ISysMenuService menuService;
    private final ISysFileService fileService;
    private final ISysLoginLogService loginLogService;

    /**
     * 登录失败最大重试次数
     */
    private static final int MAX_LOGIN_RETRY_COUNT = 5;

    /**
     * 登录失败锁定时间（分钟）
     */
    private static final int LOGIN_LOCK_TIME_MINUTES = 10;

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        String username = loginDTO.getUsername();
        String msg = "登录成功";
        Integer status = SystemConstants.OPER_SUCCESS;

        try {
            // 验证码校验
            validateCaptcha(loginDTO.getCode(), loginDTO.getUuid());

            // 登录限流检查
            checkLoginRetryLimit(username);

            // 用户认证
            Authentication authentication = authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, loginDTO.getPassword())
            );

            // 认证成功，清除登录失败计数
            clearLoginRetryCount(username);

            LoginUser loginUser = (LoginUser) authentication.getPrincipal();

            // 清除存储到Redis之前的敏感信息
            if (loginUser.getUser() != null) {
                loginUser.getUser().setPassword(null);
            }

            // 生成token
            String token = JwtUtil.generateToken(
                    loginUser.getUsername(),
                    jwtProperties.getSecret(),
                    jwtProperties.getExpireTime()
            );

            loginUser.setToken(token);
            loginUser.setLoginTime(System.currentTimeMillis());
            loginUser.setExpireTime(loginUser.getLoginTime() + jwtProperties.getExpireTime() * 60 * 1000);

            // 保存用户信息到Redis
            String userKey = SystemConstants.LOGIN_TOKEN_KEY + token;
            redisUtil.set(userKey, loginUser, jwtProperties.getExpireTime(), TimeUnit.MINUTES);

            // 记录登录日志
            recordLoginLog(username, status, msg);

            // 返回token
            Map<String, Object> result = new HashMap<>();
            result.put("token", token);
            return result;
        } catch (Exception e) {
            status = SystemConstants.OPER_FAIL;
            msg = e.getMessage();
            // 认证失败时增加失败计数（验证码错误不计入限流）
            if (!(e instanceof RuntimeException && ("验证码已过期".equals(e.getMessage()) || "验证码错误".equals(e.getMessage())))) {
                incrementLoginRetryCount(username);
            }
            // 记录登录失败日志
            recordLoginLog(username, status, msg);
            throw e;
        }
    }

    /**
     * 检查登录重试次数是否超过限制
     */
    private void checkLoginRetryLimit(String username) {
        String retryKey = SystemConstants.LOGIN_RETRY_KEY + username;
        Object retryCount = redisUtil.get(retryKey);
        if (retryCount != null && Integer.parseInt(retryCount.toString()) >= MAX_LOGIN_RETRY_COUNT) {
            throw new RuntimeException("登录失败次数过多，请" + LOGIN_LOCK_TIME_MINUTES + "分钟后再试");
        }
    }

    /**
     * 增加登录失败计数
     */
    private void incrementLoginRetryCount(String username) {
        try {
            String retryKey = SystemConstants.LOGIN_RETRY_KEY + username;
            Long count = redisUtil.increment(retryKey);
            if (count != null && count == 1L) {
                // 首次失败，设置过期时间
                redisUtil.expire(retryKey, LOGIN_LOCK_TIME_MINUTES, TimeUnit.MINUTES);
            }
        } catch (Exception e) {
            // Redis不可用时不阻断登录流程
            log.warn("记录登录失败次数异常: {}", e.getMessage());
        }
    }

    /**
     * 清除登录失败计数
     */
    private void clearLoginRetryCount(String username) {
        try {
            String retryKey = SystemConstants.LOGIN_RETRY_KEY + username;
            redisUtil.delete(retryKey);
        } catch (Exception e) {
            log.warn("清除登录失败计数异常: {}", e.getMessage());
        }
    }

    @Override
    public void logout() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            String userKey = SystemConstants.LOGIN_TOKEN_KEY + loginUser.getToken();
            redisUtil.delete(userKey);
        }
    }

    @Override
    public Map<String, Object> getCaptcha() {
        // 生成验证码
        String uuid = UUID.randomUUID().toString();
        String[] captcha = CaptchaUtil.generateCaptcha();
        String code = captcha[0]; // 验证码文本
        String base64Image = captcha[1]; // 验证码图片

        // 保存验证码到Redis，有效期2分钟
        String captchaKey = SystemConstants.CAPTCHA_KEY + uuid;
        redisUtil.set(captchaKey, code.toLowerCase(), SystemConstants.CAPTCHA_EXPIRATION, TimeUnit.MINUTES);

        Map<String, Object> result = new HashMap<>();
        result.put("uuid", uuid);
        result.put("img", base64Image);
        return result;
    }

    @Override
    public Map<String, Object> getInfo() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        SysUser user = loginUser.getUser();

        Map<String, Object> result = new HashMap<>();
        result.put("user", user);
        result.put("roles", user.getRoles());
        result.put("permissions", loginUser.getPermissions());
        return result;
    }

    @Override
    public Map<String, Object> getRouters() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        Long userId = loginUser.getUserId();

        List<SysMenu> menus = menuService.selectMenuTreeByUserId(userId);
        List<RouterVo> routers = menuService.buildMenus(menus);

        Map<String, Object> result = new HashMap<>();
        result.put("menus", routers);
        return result;
    }

    @Override
    public Map<String, Object> uploadAvatar(MultipartFile file) throws Exception {
        // 获取当前登录用户
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser == null) {
            throw new RuntimeException("用户未登录");
        }

        // 验证文件类型
        String contentType = file.getContentType();
        if (contentType == null || !contentType.startsWith("image/")) {
            throw new RuntimeException("只能上传图片文件");
        }

        // 验证文件大小（限制2MB）
        if (file.getSize() > 2 * 1024 * 1024) {
            throw new RuntimeException("头像文件大小不能超过2MB");
        }

        // 上传文件
        SysFile sysFile = fileService.uploadFile(file);

        // 设置文件用途为"头像展示"
        SysFile updateFile = new SysFile();
        updateFile.setFileId(sysFile.getFileId());
        updateFile.setRemark("头像展示 - " + loginUser.getUser().getNickname());
        fileService.updateById(updateFile);

        // 直接更新用户头像，不先查询（避免查询不存在的字段）
        SysUser user = new SysUser();
        user.setUserId(loginUser.getUserId());
        user.setAvatar(sysFile.getFileUrl());
        userService.updateById(user);

        // 更新Redis中的用户信息
        loginUser.getUser().setAvatar(sysFile.getFileUrl());
        String userKey = SystemConstants.LOGIN_TOKEN_KEY + loginUser.getToken();
        redisUtil.set(userKey, loginUser, jwtProperties.getExpireTime(), TimeUnit.MINUTES);

        // 返回结果
        Map<String, Object> result = new HashMap<>();
        result.put("avatar", sysFile.getFileUrl());
        result.put("fileId", sysFile.getFileId());
        return result;
    }

    /**
     * 验证码校验
     */
    private void validateCaptcha(String code, String uuid) {
        String captchaKey = SystemConstants.CAPTCHA_KEY + uuid;
        Object captcha = redisUtil.get(captchaKey);
        if (captcha == null) {
            throw new RuntimeException("验证码已过期");
        }
        // 不区分大小写比较
        if (!code.toLowerCase().equals(captcha.toString())) {
            throw new RuntimeException("验证码错误");
        }
        redisUtil.delete(captchaKey);
    }

    /**
     * 记录登录日志
     */
    private void recordLoginLog(String username, Integer status, String msg) {
        try {
            SysLoginLog loginLog = new SysLoginLog();
            loginLog.setUsername(username);
            loginLog.setStatus(String.valueOf(status));
            loginLog.setMsg(msg);
            loginLog.setLoginTime(new Date());

            // 获取IP地址
            ServletRequestAttributes attributes = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attributes != null) {
                HttpServletRequest request = attributes.getRequest();
                loginLog.setIpaddr(IpUtil.getIpAddr(request));
            }

            // 异步保存登录日志
            loginLogService.save(loginLog);
        } catch (Exception e) {
            // 记录日志失败不影响登录流程
            log.warn("记录登录日志失败: {}", e.getMessage());
        }
    }

}
