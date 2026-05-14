package com.admin.system.sso.controller;

import com.admin.system.common.PageResult;
import com.admin.system.sso.entity.SysSsoLoginLog;
import com.admin.system.sso.service.ISsoAuditLogService;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.Date;

@Api(tags = "SSO 审计日志")
@RestController
@RequestMapping("/system/sso/log")
@RequiredArgsConstructor
public class SysSsoLogController {

    private final ISsoAuditLogService auditLog;

    @ApiOperation("分页查询 SSO 审计日志")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('system:sso:log:list')")
    public PageResult<SysSsoLoginLog> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size,
            @RequestParam(required = false) Long providerId,
            @RequestParam(required = false) String action,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) Long userId,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date from,
            @RequestParam(required = false) @DateTimeFormat(pattern = "yyyy-MM-dd HH:mm:ss") Date to) {
        IPage<SysSsoLoginLog> page = auditLog.listPage(
                new Page<>(current, size), providerId, action, status, userId, from, to);
        return new PageResult<>(page.getRecords(), page.getTotal());
    }
}
