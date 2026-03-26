package com.admin.system.controller;

import com.admin.system.common.Result;
import com.admin.system.service.IServerMonitorService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

/**
 * 服务器监控控制器
 *
 * @author Admin
 */
@Api(tags = "服务器监控")
@RestController
@RequestMapping("/monitor/server")
@RequiredArgsConstructor
public class ServerMonitorController {

    private final IServerMonitorService serverMonitorService;

    /**
     * 获取服务器监控信息
     */
    @ApiOperation("获取服务器监控信息")
    @PreAuthorize("@ss.hasPermi('monitor:server:list')")
    @GetMapping
    public Result<Map<String, Object>> getServerInfo() {
        Map<String, Object> serverInfo = serverMonitorService.getServerInfo();
        return Result.success(serverInfo);
    }
}
