package com.admin.system.sso.controller;

import com.admin.system.common.Result;
import com.admin.system.sso.service.ISsoLoginService;
import com.admin.system.sso.service.ISysSsoProviderService;
import com.admin.system.sso.vo.SsoProviderPublicVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

@Tag(name = "SSO 登录")
@RestController
@RequiredArgsConstructor
public class SsoLoginController {

    private final ISsoLoginService ssoLoginService;
    private final ISysSsoProviderService providerService;

    @Operation(summary = "启用的 IdP 列表（登录页用）")
    @GetMapping("/sso/providers")
    public Result<List<SsoProviderPublicVO>> providers() {
        return Result.success(providerService.listEnabled());
    }

    @Operation(summary = "发起 SSO 授权（302 跳到 IdP）")
    @GetMapping("/sso/authorize/{code}")
    public void authorize(@PathVariable String code, HttpServletResponse response) throws IOException {
        response.sendRedirect(ssoLoginService.buildAuthorizationUrl(code));
    }

    @Operation(summary = "IdP 回调（302 跳前端）")
    @GetMapping("/sso/callback/{code}")
    public void callback(@PathVariable String code,
                         @RequestParam("code") String authCode,
                         @RequestParam("state") String state,
                         HttpServletResponse response) throws IOException {
        String redirect = ssoLoginService.handleCallback(code, authCode, state, response);
        response.sendRedirect(redirect);
    }
}
