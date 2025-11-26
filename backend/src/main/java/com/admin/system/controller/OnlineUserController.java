package com.admin.system.controller;

import com.admin.system.common.Result;
import com.admin.system.service.IOnlineUserService;
import com.admin.system.vo.OnlineUserVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 在线用户监控
 *
 * @author Admin
 */
@Api(tags = "在线用户监控")
@RestController
@RequestMapping("/monitor/online")
public class OnlineUserController {

    @Autowired
    private IOnlineUserService onlineUserService;

    /**
     * 查询在线用户列表
     */
    @ApiOperation("查询在线用户列表")
    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    public Result<List<OnlineUserVO>> list(
            @ApiParam("用户名") @RequestParam(required = false) String username,
            @ApiParam("登录地址") @RequestParam(required = false) String ipaddr) {

        List<OnlineUserVO> list = onlineUserService.selectOnlineUserList(username, ipaddr);
        return Result.success(list);
    }

    /**
     * 强制退出用户
     */
    @ApiOperation("强制退出用户")
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @DeleteMapping("/{tokenId}")
    public Result<Void> forceLogout(@ApiParam("会话编号") @PathVariable String tokenId) {
        onlineUserService.forceLogout(tokenId);
        return Result.success("强制退出成功");
    }

    /**
     * 批量强制退出用户
     */
    @ApiOperation("批量强制退出用户")
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @DeleteMapping("/batch/{tokenIds}")
    public Result<Void> batchForceLogout(@ApiParam("会话编号数组") @PathVariable String[] tokenIds) {
        onlineUserService.batchForceLogout(tokenIds);
        return Result.success("批量强制退出成功");
    }
}
