package com.admin.system.controller;

import com.admin.system.common.Result;
import com.admin.system.dto.LoginDTO;
import com.admin.system.service.ILoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.validation.Valid;
import java.util.Map;

/**
 * 登录控制器
 *
 * @author Admin
 */
@Api(tags = "登录管理")
@RestController
public class LoginController {

    @Autowired
    private ILoginService loginService;

    /**
     * 登录
     */
    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO) {
        Map<String, Object> result = loginService.login(loginDTO);
        return Result.success(result);
    }

    /**
     * 退出登录
     */
    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result<Void> logout() {
        loginService.logout();
        return Result.success("退出成功");
    }

    /**
     * 获取验证码
     */
    @ApiOperation("获取验证码")
    @GetMapping("/captcha")
    public Result<Map<String, Object>> getCaptcha() {
        Map<String, Object> result = loginService.getCaptcha();
        return Result.success(result);
    }

    /**
     * 获取用户信息
     */
    @ApiOperation("获取用户信息")
    @GetMapping("/getInfo")
    public Result<Map<String, Object>> getInfo() {
        Map<String, Object> result = loginService.getInfo();
        return Result.success(result);
    }

    /**
     * 获取路由信息
     */
    @ApiOperation("获取路由信息")
    @GetMapping("/getRouters")
    public Result<Map<String, Object>> getRouters() {
        Map<String, Object> result = loginService.getRouters();
        return Result.success(result);
    }

    /**
     * 上传头像
     */
    @ApiOperation("上传头像")
    @PostMapping("/profile/avatar")
    public Result<Map<String, Object>> uploadAvatar(
            @ApiParam("头像文件") @RequestParam("file") MultipartFile file) {
        try {
            Map<String, Object> result = loginService.uploadAvatar(file);
            return Result.success("头像上传成功", result);
        } catch (Exception e) {
            return Result.fail("头像上传失败: " + e.getMessage());
        }
    }

}
