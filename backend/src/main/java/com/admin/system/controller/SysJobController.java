package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysJob;
import com.admin.system.service.ISysJobService;
import com.admin.system.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.quartz.SchedulerException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 定时任务调度信息
 *
 * @author Admin
 */
@Api(tags = "定时任务管理")
@RestController
@RequestMapping("/monitor/job")
@RequiredArgsConstructor
public class SysJobController {

    private final ISysJobService jobService;

    /**
     * 分页查询定时任务列表
     */
    @ApiOperation("分页查询定时任务列表")
    @PreAuthorize("@ss.hasPermi('monitor:job:list')")
    @GetMapping("/list")
    public PageResult<SysJob> list(
            PageQuery pageQuery,
            @ApiParam("任务名称") @RequestParam(required = false) String jobName,
            @ApiParam("任务组名") @RequestParam(required = false) String jobGroup,
            @ApiParam("任务状态") @RequestParam(required = false) String status) {

        Page<SysJob> page = pageQuery.build();
        LambdaQueryWrapper<SysJob> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(jobName != null && !jobName.isEmpty(), SysJob::getJobName, jobName)
                .eq(jobGroup != null && !jobGroup.isEmpty(), SysJob::getJobGroup, jobGroup)
                .eq(status != null && !status.isEmpty(), SysJob::getStatus, status)
                .orderByDesc(SysJob::getCreateTime);

        Page<SysJob> result = jobService.page(page, queryWrapper);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 根据任务ID查询详细信息
     */
    @ApiOperation("查询定时任务详情")
    @PreAuthorize("@ss.hasPermi('monitor:job:query')")
    @GetMapping("/{jobId}")
    public Result<SysJob> getInfo(@ApiParam("任务ID") @PathVariable Long jobId) {
        SysJob job = jobService.getById(jobId);
        return Result.success(job);
    }

    /**
     * 新增定时任务
     */
    @ApiOperation("新增定时任务")
    @PreAuthorize("@ss.hasPermi('monitor:job:add')")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysJob job) throws SchedulerException {
        jobService.insertJob(job);
        return Result.success("新增定时任务成功");
    }

    /**
     * 修改定时任务
     */
    @ApiOperation("修改定时任务")
    @PreAuthorize("@ss.hasPermi('monitor:job:edit')")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysJob job) throws SchedulerException {
        jobService.updateJob(job);
        return Result.success("修改定时任务成功");
    }

    /**
     * 删除定时任务
     */
    @ApiOperation("删除定时任务")
    @PreAuthorize("@ss.hasPermi('monitor:job:remove')")
    @DeleteMapping("/{jobIds}")
    public Result<Void> remove(@ApiParam("任务ID数组") @PathVariable Long[] jobIds) throws SchedulerException {
        jobService.deleteJobByIds(jobIds);
        return Result.success("删除定时任务成功");
    }

    /**
     * 修改任务状态
     */
    @ApiOperation("修改任务状态")
    @PreAuthorize("@ss.hasPermi('monitor:job:changeStatus')")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody SysJob job) throws SchedulerException {
        jobService.changeStatus(job);
        return Result.success("修改任务状态成功");
    }

    /**
     * 立即运行任务
     */
    @ApiOperation("立即运行任务")
    @PreAuthorize("@ss.hasPermi('monitor:job:run')")
    @PutMapping("/run")
    public Result<Void> run(@RequestBody SysJob job) throws SchedulerException {
        jobService.run(job);
        return Result.success("执行任务成功");
    }
}
