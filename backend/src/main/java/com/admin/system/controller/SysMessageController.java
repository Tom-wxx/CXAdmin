package com.admin.system.controller;

import com.admin.common.annotation.Log;
import com.admin.common.PageResult;
import com.admin.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysMessage;
import com.admin.system.service.ISysMessageService;
import com.admin.common.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 消息模板管理Controller
 *
 * @author Admin
 */
@Tag(name = "消息模板管理")
@RestController
@RequestMapping("/system/message")
@RequiredArgsConstructor
public class SysMessageController {

    private final ISysMessageService messageService;

    /**
     * 分页查询消息模板列表
     */
    @Operation(summary = "分页查询消息模板列表")
    @PreAuthorize("@ss.hasPermi('system:message:list')")
    @GetMapping("/list")
    public PageResult<SysMessage> list(
            PageQuery pageQuery,
            @Parameter(description = "消息名称") @RequestParam(required = false) String messageName,
            @Parameter(description = "消息编码") @RequestParam(required = false) String messageCode,
            @Parameter(description = "消息类型") @RequestParam(required = false) String messageType,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        Page<SysMessage> page = pageQuery.build();

        LambdaQueryWrapper<SysMessage> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(messageName), SysMessage::getMessageName, messageName);
        queryWrapper.like(StringUtils.isNotBlank(messageCode), SysMessage::getMessageCode, messageCode);
        queryWrapper.eq(StringUtils.isNotBlank(messageType), SysMessage::getMessageType, messageType);
        queryWrapper.eq(StringUtils.isNotBlank(status), SysMessage::getStatus, status);
        queryWrapper.eq(SysMessage::getDeleted, 0);
        queryWrapper.orderByDesc(SysMessage::getCreateTime);

        Page<SysMessage> result = messageService.page(page, queryWrapper);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 根据消息ID查询详细信息
     */
    @Operation(summary = "查询消息模板详情")
    @PreAuthorize("@ss.hasPermi('system:message:query')")
    @GetMapping("/{messageId}")
    public Result<SysMessage> getInfo(@Parameter(description = "消息ID") @PathVariable Long messageId) {
        SysMessage message = messageService.getById(messageId);
        return Result.success(message);
    }

    /**
     * 新增消息模板
     */
    @Log(title = "消息模板管理", businessType = Log.BusinessType.INSERT)
    @Operation(summary = "新增消息模板")
    @PreAuthorize("@ss.hasPermi('system:message:add')")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysMessage message) {
        // 检查消息编码是否已存在
        SysMessage existMessage = messageService.getByMessageCode(message.getMessageCode());
        if (existMessage != null) {
            return Result.fail("消息编码已存在");
        }

        boolean success = messageService.save(message);
        return success ? Result.success() : Result.fail();
    }

    /**
     * 修改消息模板
     */
    @Log(title = "消息模板管理", businessType = Log.BusinessType.UPDATE)
    @Operation(summary = "修改消息模板")
    @PreAuthorize("@ss.hasPermi('system:message:edit')")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysMessage message) {
        // 检查消息编码是否已被其他记录使用
        SysMessage existMessage = messageService.getByMessageCode(message.getMessageCode());
        if (existMessage != null && !existMessage.getMessageId().equals(message.getMessageId())) {
            return Result.fail("消息编码已存在");
        }

        boolean success = messageService.updateById(message);
        return success ? Result.success() : Result.fail();
    }

    /**
     * 删除消息模板
     */
    @Log(title = "消息模板管理", businessType = Log.BusinessType.DELETE)
    @Operation(summary = "删除消息模板")
    @PreAuthorize("@ss.hasPermi('system:message:remove')")
    @DeleteMapping("/{messageIds}")
    public Result<Void> remove(@Parameter(description = "消息ID数组") @PathVariable Long[] messageIds) {
        List<Long> idList = Arrays.asList(messageIds);
        boolean success = messageService.removeByIds(idList);
        return success ? Result.success() : Result.fail();
    }

    /**
     * 修改消息模板状态
     */
    @Log(title = "消息模板管理", businessType = Log.BusinessType.UPDATE)
    @Operation(summary = "修改消息模板状态")
    @PreAuthorize("@ss.hasPermi('system:message:edit')")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody SysMessage message) {
        SysMessage updateMessage = new SysMessage();
        updateMessage.setMessageId(message.getMessageId());
        updateMessage.setStatus(message.getStatus());
        boolean success = messageService.updateById(updateMessage);
        return success ? Result.success() : Result.fail();
    }
}
