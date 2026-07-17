package com.admin.system.controller;

import com.admin.common.PageResult;
import com.admin.common.Result;
import com.admin.system.service.IOnlineUserService;
import com.admin.system.vo.OnlineUserVO;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;


/**
 * 在线用户监控
 *
 * @author Admin
 */
@Tag(name = "在线用户监控")
@RestController
@RequestMapping("/monitor/online")
@RequiredArgsConstructor
public class OnlineUserController {

    private final IOnlineUserService onlineUserService;

    /**
     * 查询在线用户列表（分页）
     */
    @Operation(summary = "查询在线用户列表")
    @PreAuthorize("@ss.hasPermi('monitor:online:list')")
    @GetMapping("/list")
    public PageResult<OnlineUserVO> list(
            @Parameter(description = "用户名") @RequestParam(required = false) String username,
            @Parameter(description = "登录地址") @RequestParam(required = false) String ipaddr,
            @Parameter(description = "当前页") @RequestParam(defaultValue = "1") Integer current,
            @Parameter(description = "每页大小") @RequestParam(defaultValue = "10") Integer size) {

        return onlineUserService.selectOnlineUserListPage(username, ipaddr, current, size);
    }

    /**
     * 强制退出用户
     */
    @Operation(summary = "强制退出用户")
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @DeleteMapping("/{tokenId}")
    public Result<Void> forceLogout(@Parameter(description = "会话编号") @PathVariable String tokenId) {
        onlineUserService.forceLogout(tokenId);
        return Result.success("强制退出成功");
    }

    /**
     * 批量强制退出用户
     */
    @Operation(summary = "批量强制退出用户")
    @PreAuthorize("@ss.hasPermi('monitor:online:forceLogout')")
    @DeleteMapping("/batch/{tokenIds}")
    public Result<Void> batchForceLogout(@Parameter(description = "会话编号数组") @PathVariable String[] tokenIds) {
        onlineUserService.batchForceLogout(tokenIds);
        return Result.success("批量强制退出成功");
    }
}
