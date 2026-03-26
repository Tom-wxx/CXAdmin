package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysNotification;
import com.admin.system.security.SecurityUtils;
import com.admin.system.service.ISysNotificationService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 站内通知控制器
 *
 * @author Admin
 */
@RestController
@RequestMapping("/system/notification")
@Tag(name = "站内通知管理")
@RequiredArgsConstructor
public class SysNotificationController {

    private final ISysNotificationService notificationService;

    /**
     * 分页查询当前用户的通知列表
     */
    @GetMapping("/list")
    @Operation(summary = "分页查询通知列表")
    public PageResult<SysNotification> list(PageQuery pageQuery, SysNotification notification) {
        Long userId = SecurityUtils.getUserId();
        LambdaQueryWrapper<SysNotification> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysNotification::getUserId, userId);

        // 通知类型筛选
        if (notification.getType() != null && !notification.getType().isEmpty()) {
            queryWrapper.eq(SysNotification::getType, notification.getType());
        }

        // 状态筛选
        if (notification.getStatus() != null && !notification.getStatus().isEmpty()) {
            queryWrapper.eq(SysNotification::getStatus, notification.getStatus());
        }

        // 标题搜索
        if (notification.getTitle() != null && !notification.getTitle().isEmpty()) {
            queryWrapper.like(SysNotification::getTitle, notification.getTitle());
        }

        queryWrapper.orderByDesc(SysNotification::getCreateTime);

        Page<SysNotification> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<SysNotification> result = notificationService.page(page, queryWrapper);

        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    /**
     * 查询通知详情
     */
    @GetMapping("/{id}")
    @Operation(summary = "查询通知详情")
    public Result<SysNotification> getById(@PathVariable Long id) {
        Long userId = SecurityUtils.getUserId();
        SysNotification notification = notificationService.getById(id);

        // 验证通知是否属于当前用户
        if (notification == null || !notification.getUserId().equals(userId)) {
            return Result.fail("通知不存在");
        }

        // 如果是未读消息，自动标记为已读
        if ("unread".equals(notification.getStatus())) {
            notificationService.markAsRead(id, userId);
            notification.setStatus("read");
        }

        return Result.success(notification);
    }

    /**
     * 获取未读消息数量
     */
    @GetMapping("/unread/count")
    @Operation(summary = "获取未读消息数量")
    public Result<Map<String, Object>> getUnreadCount() {
        Long userId = SecurityUtils.getUserId();
        Long count = notificationService.getUnreadCount(userId);

        Map<String, Object> data = new HashMap<>();
        data.put("count", count);

        return Result.success(data);
    }

    /**
     * 标记消息为已读
     */
    @PutMapping("/read/{id}")
    @Operation(summary = "标记消息为已读")
    public Result<Void> markAsRead(@PathVariable Long id) {
        Long userId = SecurityUtils.getUserId();
        boolean success = notificationService.markAsRead(id, userId);
        return success ? Result.success() : Result.fail("操作失败");
    }

    /**
     * 批量标记消息为已读
     */
    @PutMapping("/read/batch")
    @Operation(summary = "批量标记消息为已读")
    public Result<Void> batchMarkAsRead(@RequestBody Long[] ids) {
        Long userId = SecurityUtils.getUserId();
        boolean success = notificationService.batchMarkAsRead(ids, userId);
        return success ? Result.success() : Result.fail("操作失败");
    }

    /**
     * 全部标记为已读
     */
    @PutMapping("/read/all")
    @Operation(summary = "全部标记为已读")
    public Result<Void> markAllAsRead() {
        Long userId = SecurityUtils.getUserId();
        boolean success = notificationService.markAllAsRead(userId);
        return success ? Result.success() : Result.fail("操作失败");
    }

    /**
     * 删除通知
     */
    @DeleteMapping("/{ids}")
    @Operation(summary = "删除通知")
    public Result<Void> delete(@PathVariable Long[] ids) {
        Long userId = SecurityUtils.getUserId();

        // 验证所有通知是否属于当前用户
        for (Long id : ids) {
            SysNotification notification = notificationService.getById(id);
            if (notification == null || !notification.getUserId().equals(userId)) {
                return Result.fail("无权删除该通知");
            }
        }

        boolean success = notificationService.removeByIds(Arrays.asList(ids));
        return success ? Result.success() : Result.fail("删除失败");
    }

    /**
     * 发送通知（管理员功能）
     */
    @PostMapping("/send")
    @PreAuthorize("@ss.hasPermi('system:notification:send')")
    @Operation(summary = "发送通知")
    public Result<Void> sendNotification(@Validated @RequestBody SysNotification notification) {
        Long senderId = SecurityUtils.getUserId();
        String senderName = SecurityUtils.getUsername();

        notification.setSenderId(senderId);
        notification.setSenderName(senderName);

        boolean success = notificationService.sendNotification(notification);
        return success ? Result.success() : Result.fail("发送失败");
    }

    /**
     * 批量发送通知（管理员功能）
     */
    @PostMapping("/send/batch")
    @PreAuthorize("@ss.hasPermi('system:notification:send')")
    @Operation(summary = "批量发送通知")
    public Result<Void> sendNotificationToUsers(@RequestBody Map<String, Object> params) {
        Long senderId = SecurityUtils.getUserId();
        String senderName = SecurityUtils.getUsername();

        SysNotification notification = new SysNotification();
        notification.setTitle((String) params.get("title"));
        notification.setContent((String) params.get("content"));
        notification.setType((String) params.get("type"));
        notification.setPriority((String) params.get("priority"));
        notification.setSenderId(senderId);
        notification.setSenderName(senderName);

        @SuppressWarnings("unchecked")
        List<Long> userIds = (List<Long>) params.get("userIds");

        boolean success = notificationService.sendNotificationToUsers(notification, userIds);
        return success ? Result.success() : Result.fail("发送失败");
    }

}
