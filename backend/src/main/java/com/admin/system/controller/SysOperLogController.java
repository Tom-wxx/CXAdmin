package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysOperLog;
import com.admin.system.service.ISysOperLogService;
import com.admin.system.utils.PageUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 操作日志记录
 *
 * @author Admin
 */
@Api(tags = "操作日志管理")
@RestController
@RequestMapping("/system/operlog")
public class SysOperLogController {

    @Autowired
    private ISysOperLogService operLogService;

    /**
     * 分页查询操作日志列表
     */
    @ApiOperation("分页查询操作日志列表")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    public PageResult<SysOperLog> list(
            PageQuery pageQuery,
            @ApiParam("模块标题") @RequestParam(required = false) String title,
            @ApiParam("操作人员") @RequestParam(required = false) String operName,
            @ApiParam("业务类型") @RequestParam(required = false) Integer businessType,
            @ApiParam("操作状态") @RequestParam(required = false) Integer status,
            @ApiParam("开始时间") @RequestParam(required = false) String beginTime,
            @ApiParam("结束时间") @RequestParam(required = false) String endTime) {

        Page<SysOperLog> page = pageQuery.build();
        Page<SysOperLog> result = operLogService.selectOperLogPage(page, title, operName, businessType, status, beginTime, endTime);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 根据日志ID查询详细信息
     */
    @ApiOperation("查询操作日志详情")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:query')")
    @GetMapping("/{operId}")
    public Result<SysOperLog> getInfo(@ApiParam("日志ID") @PathVariable Long operId) {
        SysOperLog operLog = operLogService.getById(operId);
        return Result.success(operLog);
    }

    /**
     * 删除操作日志
     */
    @ApiOperation("删除操作日志")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public Result<Void> remove(@ApiParam("日志ID数组") @PathVariable Long[] operIds) {
        operLogService.deleteOperLogByIds(operIds);
        return Result.success("删除操作日志成功");
    }

    /**
     * 清空操作日志
     */
    @ApiOperation("清空操作日志")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        operLogService.cleanOperLog();
        return Result.success("清空操作日志成功");
    }
}
