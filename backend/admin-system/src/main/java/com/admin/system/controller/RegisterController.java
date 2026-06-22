package com.admin.system.controller;

import com.admin.common.Result;
import com.admin.system.dto.ForgotPasswordDTO;
import com.admin.system.dto.RegisterDTO;
import com.admin.system.dto.ResetPasswordDTO;
import com.admin.system.service.IRegisterService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

@Tag(name = "注册管理")
@RestController
@RequiredArgsConstructor
public class RegisterController {

    private final IRegisterService registerService;

    @Operation(summary = "用户注册")
    @PostMapping("/register")
    public Result<Void> register(@Valid @RequestBody RegisterDTO dto) {
        registerService.register(dto);
        return Result.success("注册成功");
    }

    @Operation(summary = "忘记密码")
    @PostMapping("/forgot-password")
    public Result<Void> forgotPassword(@Valid @RequestBody ForgotPasswordDTO dto) {
        registerService.forgotPassword(dto.getEmail());
        return Result.success("如该邮箱已注册，重置链接将发送至您的邮箱");
    }

    @Operation(summary = "重置密码")
    @PostMapping("/reset-password")
    public Result<Void> resetPassword(@Valid @RequestBody ResetPasswordDTO dto) {
        registerService.resetPassword(dto);
        return Result.success("密码重置成功");
    }
}
