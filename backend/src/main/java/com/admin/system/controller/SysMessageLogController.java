package com.admin.system.controller;

import com.admin.system.annotation.Log;
import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysMessageLog;
import com.admin.system.service.ISysMessageLogService;
import com.admin.system.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 消息发送日志Controller
 *
 * @author Admin
 */
@Api(tags = "消息发送日志")
@RestController
@RequestMapping("/system/message/log")
@RequiredArgsConstructor
public class SysMessageLogController {

    private final ISysMessageLogService messageLogService;

    /**
     * 分页查询消息发送日志列表
     */
    @ApiOperation("分页查询消息发送日志列表")
    @PreAuthorize("@ss.hasPermi('system:message:log:list')")
    @GetMapping("/list")
    public PageResult<SysMessageLog> list(
            PageQuery pageQuery,
            @ApiParam("消息类型") @RequestParam(required = false) String messageType,
            @ApiParam("接收者") @RequestParam(required = false) String receiver,
            @ApiParam("发送状态") @RequestParam(required = false) String sendStatus,
            @ApiParam("开始时间") @RequestParam(required = false) String startTime,
            @ApiParam("结束时间") @RequestParam(required = false) String endTime) {

        Page<SysMessageLog> page = pageQuery.build();

        LambdaQueryWrapper<SysMessageLog> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(StringUtils.isNotBlank(messageType), SysMessageLog::getMessageType, messageType);
        queryWrapper.like(StringUtils.isNotBlank(receiver), SysMessageLog::getReceiver, receiver);
        queryWrapper.eq(StringUtils.isNotBlank(sendStatus), SysMessageLog::getSendStatus, sendStatus);
        queryWrapper.ge(StringUtils.isNotBlank(startTime), SysMessageLog::getCreateTime, startTime);
        queryWrapper.le(StringUtils.isNotBlank(endTime), SysMessageLog::getCreateTime, endTime);
        queryWrapper.orderByDesc(SysMessageLog::getCreateTime);

        Page<SysMessageLog> result = messageLogService.page(page, queryWrapper);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 根据日志ID查询详细信息
     */
    @ApiOperation("查询消息发送日志详情")
    @PreAuthorize("@ss.hasPermi('system:message:log:query')")
    @GetMapping("/{logId}")
    public Result<SysMessageLog> getInfo(@ApiParam("日志ID") @PathVariable Long logId) {
        SysMessageLog log = messageLogService.getById(logId);
        return Result.success(log);
    }

    /**
     * 删除消息发送日志
     */
    @Log(title = "消息发送日志", businessType = Log.BusinessType.DELETE)
    @ApiOperation("删除消息发送日志")
    @PreAuthorize("@ss.hasPermi('system:message:log:remove')")
    @DeleteMapping("/{logIds}")
    public Result<Void> remove(@ApiParam("日志ID数组") @PathVariable Long[] logIds) {
        List<Long> idList = Arrays.asList(logIds);
        boolean success = messageLogService.removeByIds(idList);
        return success ? Result.success() : Result.fail();
    }

    /**
     * 清空消息发送日志
     */
    @Log(title = "消息发送日志", businessType = Log.BusinessType.CLEAN)
    @ApiOperation("清空消息发送日志")
    @PreAuthorize("@ss.hasPermi('system:message:log:remove')")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        LambdaQueryWrapper<SysMessageLog> queryWrapper = new LambdaQueryWrapper<>();
        boolean success = messageLogService.remove(queryWrapper);
        return success ? Result.success() : Result.fail();
    }
}
