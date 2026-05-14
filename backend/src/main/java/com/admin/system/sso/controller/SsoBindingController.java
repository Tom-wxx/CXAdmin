package com.admin.system.sso.controller;

import com.admin.system.common.Result;
import com.admin.system.security.SecurityUtils;
import com.admin.system.sso.service.ISsoLoginService;
import com.admin.system.sso.vo.SsoBindingVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 当前用户的 SSO 绑定管理（个人中心用）。
 * 所有端点都需要登录态——SecurityConfig 里没把 /sso/bind/** 和 /sso/bindings/** 放进 permitAll。
 */
@Api(tags = "SSO 绑定管理")
@RestController
@RequiredArgsConstructor
@RequestMapping("/sso")
public class SsoBindingController {

    private final ISsoLoginService ssoLoginService;

    @ApiOperation("当前用户已绑定的 IdP 列表")
    @GetMapping("/bindings/me")
    public Result<List<SsoBindingVO>> myBindings() {
        return Result.success(ssoLoginService.listMyBindings(SecurityUtils.getUserId()));
    }

    @ApiOperation("解绑：仅能解自己的")
    @DeleteMapping("/bindings/{id}")
    public Result<Void> unbind(@PathVariable Long id) {
        ssoLoginService.unbind(id, SecurityUtils.getUserId());
        return Result.success("解绑成功");
    }

    @ApiOperation("已登录用户发起绑定，返回 IdP 授权 URL（前端 window.location 跳）")
    @GetMapping("/bind/{code}")
    public Result<String> startBind(@PathVariable String code) {
        return Result.success(ssoLoginService.buildBindAuthorizationUrl(code, SecurityUtils.getUserId()));
    }
}
