package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysLoginLog;
import com.admin.system.service.ISysLoginLogService;
import com.admin.system.utils.PageUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import com.admin.system.utils.ExcelUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 登录日志记录
 *
 * @author Admin
 */
@Api(tags = "登录日志管理")
@RestController
@RequestMapping("/system/loginlog")
@RequiredArgsConstructor
public class SysLoginLogController {

    private final ISysLoginLogService loginLogService;

    /**
     * 分页查询登录日志列表
     */
    @ApiOperation("分页查询登录日志列表")
    @PreAuthorize("@ss.hasPermi('monitor:loginlog:list')")
    @GetMapping("/list")
    public PageResult<SysLoginLog> list(
            PageQuery pageQuery,
            @ApiParam("用户账号") @RequestParam(required = false) String username,
            @ApiParam("登录IP地址") @RequestParam(required = false) String ipaddr,
            @ApiParam("登录状态") @RequestParam(required = false) String status,
            @ApiParam("开始时间") @RequestParam(required = false) String beginTime,
            @ApiParam("结束时间") @RequestParam(required = false) String endTime) {

        Page<SysLoginLog> page = pageQuery.build();
        Page<SysLoginLog> result = loginLogService.selectLoginLogPage(page, username, ipaddr, status, beginTime, endTime);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 根据日志ID查询详细信息
     */
    @ApiOperation("查询登录日志详情")
    @PreAuthorize("@ss.hasPermi('monitor:loginlog:query')")
    @GetMapping("/{infoId}")
    public Result<SysLoginLog> getInfo(@ApiParam("日志ID") @PathVariable Long infoId) {
        SysLoginLog loginLog = loginLogService.getById(infoId);
        return Result.success(loginLog);
    }

    /**
     * 删除登录日志
     */
    @ApiOperation("删除登录日志")
    @PreAuthorize("@ss.hasPermi('monitor:loginlog:remove')")
    @DeleteMapping("/{infoIds}")
    public Result<Void> remove(@ApiParam("日志ID数组") @PathVariable Long[] infoIds) {
        loginLogService.deleteLoginLogByIds(infoIds);
        return Result.success("删除登录日志成功");
    }

    /**
     * 清空登录日志
     */
    @ApiOperation("清空登录日志")
    @PreAuthorize("@ss.hasPermi('monitor:loginlog:remove')")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        loginLogService.cleanLoginLog();
        return Result.success("清空登录日志成功");
    }

    /**
     * 导出登录日志
     */
    @ApiOperation("导出登录日志")
    @PreAuthorize("@ss.hasPermi('monitor:loginlog:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response,
                       @ApiParam("用户账号") @RequestParam(required = false) String username,
                       @ApiParam("登录IP地址") @RequestParam(required = false) String ipaddr,
                       @ApiParam("登录状态") @RequestParam(required = false) String status,
                       @ApiParam("开始时间") @RequestParam(required = false) String beginTime,
                       @ApiParam("结束时间") @RequestParam(required = false) String endTime) throws IOException {
        List<SysLoginLog> list = loginLogService.selectLoginLogList(username, ipaddr, status, beginTime, endTime);
        String[] headers = {"访问ID", "用户账号", "登录IP", "登录地点", "浏览器", "操作系统", "登录状态", "登录时间"};
        String[] fields = {"infoId", "username", "ipaddr", "loginLocation", "browser", "os", "status", "loginTime"};
        ExcelUtil.exportExcel(response, "登录日志", headers, fields, list);
    }
}
