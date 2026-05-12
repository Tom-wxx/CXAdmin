package com.admin.system.controller;

import com.admin.system.common.Result;
import com.admin.system.common.constants.SystemConstants;
import com.admin.system.config.JwtProperties;
import com.admin.system.dto.LoginDTO;
import com.admin.system.service.ILoginService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import javax.validation.Valid;
import java.util.Map;

@Api(tags = "登录管理")
@RestController
@RequiredArgsConstructor
public class LoginController {

    private final ILoginService loginService;
    private final JwtProperties jwtProperties;

    @ApiOperation("用户登录")
    @PostMapping("/login")
    public Result<Map<String, Object>> login(@Valid @RequestBody LoginDTO loginDTO, HttpServletResponse response) {
        Map<String, Object> result = loginService.login(loginDTO);
        String token = (String) result.remove("token");
        // Build the Set-Cookie header manually to include SameSite=Lax (CSRF mitigation)
        String cookieHeader = SystemConstants.TOKEN_COOKIE_NAME + "=" + token
                + "; Path=/"
                + "; HttpOnly"
                + "; Max-Age=" + (jwtProperties.getExpireTime() * 60)
                + "; SameSite=Lax";
        response.addHeader("Set-Cookie", cookieHeader);
        return Result.success(result);
    }

    @ApiOperation("退出登录")
    @PostMapping("/logout")
    public Result<Void> logout(HttpServletResponse response) {
        loginService.logout();
        response.addHeader("Set-Cookie", SystemConstants.TOKEN_COOKIE_NAME + "=; Path=/; HttpOnly; Max-Age=0; SameSite=Lax");
        return Result.success("退出成功");
    }

    @ApiOperation("获取验证码")
    @GetMapping("/captcha")
    public Result<Map<String, Object>> getCaptcha() {
        return Result.success(loginService.getCaptcha());
    }

    @ApiOperation("获取用户信息")
    @GetMapping("/getInfo")
    public Result<Map<String, Object>> getInfo() {
        return Result.success(loginService.getInfo());
    }

    @ApiOperation("获取路由信息")
    @GetMapping("/getRouters")
    public Result<Map<String, Object>> getRouters() {
        return Result.success(loginService.getRouters());
    }

    @ApiOperation("上传头像")
    @PostMapping("/profile/avatar")
    public Result<Map<String, Object>> uploadAvatar(
            @ApiParam("头像文件") @RequestParam("file") MultipartFile file) {
        try {
            return Result.success("头像上传成功", loginService.uploadAvatar(file));
        } catch (Exception e) {
            return Result.fail("头像上传失败: " + e.getMessage());
        }
    }
}
