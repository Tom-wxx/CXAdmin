package com.admin.system.service.impl;

import com.admin.system.config.JwtProperties;
import com.admin.system.dto.LoginDTO;
import com.admin.system.entity.SysFile;
import com.admin.system.entity.SysMenu;
import com.admin.system.entity.SysUser;
import com.admin.system.security.LoginUser;
import com.admin.system.security.SecurityUtils;
import com.admin.system.service.ILoginService;
import com.admin.system.service.ISysFileService;
import com.admin.system.service.ISysMenuService;
import com.admin.system.service.ISysUserService;
import com.admin.system.utils.JwtUtil;
import com.admin.system.utils.RedisUtil;
import com.admin.system.vo.RouterVo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 登录服务实现
 *
 * @author Admin
 */
@Service
public class LoginServiceImpl implements ILoginService {

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private RedisUtil redisUtil;

    @Autowired
    private JwtProperties jwtProperties;

    @Autowired
    private ISysUserService userService;

    @Autowired
    private ISysMenuService menuService;

    @Autowired
    private ISysFileService fileService;

    @Override
    public Map<String, Object> login(LoginDTO loginDTO) {
        // 验证码校验
        validateCaptcha(loginDTO.getCode(), loginDTO.getUuid());

        // 用户认证
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(loginDTO.getUsername(), loginDTO.getPassword())
        );

        LoginUser loginUser = (LoginUser) authentication.getPrincipal();

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
        String userKey = "login_tokens:" + token;
        redisUtil.set(userKey, loginUser, jwtProperties.getExpireTime(), TimeUnit.MINUTES);

        // 返回token
        Map<String, Object> result = new HashMap<>();
        result.put("token", token);
        return result;
    }

    @Override
    public void logout() {
        LoginUser loginUser = SecurityUtils.getLoginUser();
        if (loginUser != null) {
            String userKey = "login_tokens:" + loginUser.getToken();
            redisUtil.delete(userKey);
        }
    }

    @Override
    public Map<String, Object> getCaptcha() {
        // 生成验证码
        String uuid = UUID.randomUUID().toString();
        String[] captcha = com.admin.system.util.CaptchaUtil.generateCaptcha();
        String code = captcha[0]; // 验证码文本
        String base64Image = captcha[1]; // 验证码图片

        // 保存验证码到Redis，有效期2分钟
        String captchaKey = "captcha:" + uuid;
        redisUtil.set(captchaKey, code.toLowerCase(), 2, TimeUnit.MINUTES);

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

        // 直接更新用户头像，不先查询（避免查询不存在的字段）
        SysUser user = new SysUser();
        user.setUserId(loginUser.getUserId());
        user.setAvatar(sysFile.getFileUrl());
        userService.updateById(user);

        // 更新Redis中的用户信息
        loginUser.getUser().setAvatar(sysFile.getFileUrl());
        String userKey = "login_tokens:" + loginUser.getToken();
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
        String captchaKey = "captcha:" + uuid;
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

}
