package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysNotice;
import com.admin.system.service.ISysNoticeService;
import com.admin.system.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 通知公告信息
 *
 * @author Admin
 */
@Api(tags = "通知公告管理")
@RestController
@RequestMapping("/system/notice")
@RequiredArgsConstructor
public class SysNoticeController {

    private final ISysNoticeService noticeService;

    /**
     * 分页查询通知公告列表
     */
    @ApiOperation("分页查询通知公告列表")
    @PreAuthorize("@ss.hasPermi('system:notice:list')")
    @GetMapping("/list")
    public PageResult<SysNotice> list(
            PageQuery pageQuery,
            @ApiParam("公告标题") @RequestParam(required = false) String noticeTitle,
            @ApiParam("公告类型") @RequestParam(required = false) String noticeType,
            @ApiParam("公告状态") @RequestParam(required = false) String status) {

        Page<SysNotice> page = pageQuery.build();
        LambdaQueryWrapper<SysNotice> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(noticeTitle != null && !noticeTitle.isEmpty(), SysNotice::getNoticeTitle, noticeTitle)
                .eq(noticeType != null && !noticeType.isEmpty(), SysNotice::getNoticeType, noticeType)
                .eq(status != null && !status.isEmpty(), SysNotice::getStatus, status)
                .orderByDesc(SysNotice::getCreateTime);

        Page<SysNotice> result = noticeService.page(page, queryWrapper);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 根据公告ID查询详细信息
     */
    @ApiOperation("查询通知公告详情")
    @PreAuthorize("@ss.hasPermi('system:notice:query')")
    @GetMapping("/{noticeId}")
    public Result<SysNotice> getInfo(@ApiParam("公告ID") @PathVariable Long noticeId) {
        SysNotice notice = noticeService.getById(noticeId);
        return Result.success(notice);
    }

    /**
     * 新增通知公告
     */
    @ApiOperation("新增通知公告")
    @PreAuthorize("@ss.hasPermi('system:notice:add')")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysNotice notice) {
        noticeService.insertNotice(notice);
        return Result.success("新增通知公告成功");
    }

    /**
     * 修改通知公告
     */
    @ApiOperation("修改通知公告")
    @PreAuthorize("@ss.hasPermi('system:notice:edit')")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysNotice notice) {
        noticeService.updateNotice(notice);
        return Result.success("修改通知公告成功");
    }

    /**
     * 删除通知公告
     */
    @ApiOperation("删除通知公告")
    @PreAuthorize("@ss.hasPermi('system:notice:remove')")
    @DeleteMapping("/{noticeIds}")
    public Result<Void> remove(@ApiParam("公告ID数组") @PathVariable Long[] noticeIds) {
        noticeService.deleteNoticeByIds(noticeIds);
        return Result.success("删除通知公告成功");
    }
}
