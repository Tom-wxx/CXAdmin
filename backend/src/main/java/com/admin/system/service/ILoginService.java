package com.admin.system.service;

import com.admin.system.dto.LoginDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

/**
 * 登录服务
 *
 * @author Admin
 */
public interface ILoginService {

    /**
     * 登录
     */
    Map<String, Object> login(LoginDTO loginDTO);

    /**
     * 退出登录
     */
    void logout();

    /**
     * 获取验证码
     */
    Map<String, Object> getCaptcha();

    /**
     * 获取登录用户信息
     */
    Map<String, Object> getInfo();

    /**
     * 获取路由信息
     */
    Map<String, Object> getRouters();

    /**
     * 上传头像
     */
    Map<String, Object> uploadAvatar(MultipartFile file) throws Exception;

}
