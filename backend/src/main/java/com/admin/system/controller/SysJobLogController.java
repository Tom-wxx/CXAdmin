package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysJobLog;
import com.admin.system.service.ISysJobLogService;
import com.admin.system.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

/**
 * 定时任务调度日志信息
 *
 * @author Admin
 */
@Api(tags = "定时任务日志管理")
@RestController
@RequestMapping("/monitor/jobLog")
public class SysJobLogController {

    @Autowired
    private ISysJobLogService jobLogService;

    /**
     * 分页查询定时任务日志列表
     */
    @ApiOperation("分页查询定时任务日志列表")
    @PreAuthorize("@ss.hasPermi('monitor:job:list')")
    @GetMapping("/list")
    public PageResult<SysJobLog> list(
            PageQuery pageQuery,
            @ApiParam("任务名称") @RequestParam(required = false) String jobName,
            @ApiParam("任务组名") @RequestParam(required = false) String jobGroup,
            @ApiParam("执行状态") @RequestParam(required = false) String status) {

        Page<SysJobLog> page = pageQuery.build();
        LambdaQueryWrapper<SysJobLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(jobName != null && !jobName.isEmpty(), SysJobLog::getJobName, jobName)
                .eq(jobGroup != null && !jobGroup.isEmpty(), SysJobLog::getJobGroup, jobGroup)
                .eq(status != null && !status.isEmpty(), SysJobLog::getStatus, status)
                .orderByDesc(SysJobLog::getCreateTime);

        Page<SysJobLog> result = jobLogService.page(page, queryWrapper);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 根据日志ID查询详细信息
     */
    @ApiOperation("查询任务日志详情")
    @PreAuthorize("@ss.hasPermi('monitor:job:query')")
    @GetMapping("/{jobLogId}")
    public Result<SysJobLog> getInfo(@ApiParam("日志ID") @PathVariable Long jobLogId) {
        SysJobLog jobLog = jobLogService.getById(jobLogId);
        return Result.success(jobLog);
    }

    /**
     * 删除任务日志
     */
    @ApiOperation("删除任务日志")
    @PreAuthorize("@ss.hasPermi('monitor:job:remove')")
    @DeleteMapping("/{jobLogIds}")
    public Result<Void> remove(@ApiParam("日志ID数组") @PathVariable Long[] jobLogIds) {
        jobLogService.deleteJobLogByIds(jobLogIds);
        return Result.success("删除任务日志成功");
    }

    /**
     * 清空任务日志
     */
    @ApiOperation("清空任务日志")
    @PreAuthorize("@ss.hasPermi('monitor:job:remove')")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        jobLogService.cleanJobLog();
        return Result.success("清空任务日志成功");
    }
}
