package com.admin.system.controller;

import com.admin.system.annotation.Log;
import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysMessageConfig;
import com.admin.system.service.IMessageSendService;
import com.admin.system.service.ISysMessageConfigService;
import com.admin.system.utils.PageUtils;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.StringUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;
import java.util.Map;

/**
 * 消息配置管理Controller
 *
 * @author Admin
 */
@Api(tags = "消息配置管理")
@RestController
@RequestMapping("/system/message/config")
@RequiredArgsConstructor
public class SysMessageConfigController {

    private final ISysMessageConfigService messageConfigService;
    private final IMessageSendService messageSendService;

    /**
     * 分页查询消息配置列表
     */
    @ApiOperation("分页查询消息配置列表")
    @PreAuthorize("@ss.hasPermi('system:message:config:list')")
    @GetMapping("/list")
    public PageResult<SysMessageConfig> list(
            PageQuery pageQuery,
            @ApiParam("配置名称") @RequestParam(required = false) String configName,
            @ApiParam("消息类型") @RequestParam(required = false) String messageType,
            @ApiParam("状态") @RequestParam(required = false) String status) {

        Page<SysMessageConfig> page = pageQuery.build();

        LambdaQueryWrapper<SysMessageConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(StringUtils.isNotBlank(configName), SysMessageConfig::getConfigName, configName);
        queryWrapper.eq(StringUtils.isNotBlank(messageType), SysMessageConfig::getMessageType, messageType);
        queryWrapper.eq(StringUtils.isNotBlank(status), SysMessageConfig::getStatus, status);
        queryWrapper.eq(SysMessageConfig::getDeleted, 0);
        queryWrapper.orderByAsc(SysMessageConfig::getMessageType);
        queryWrapper.orderByDesc(SysMessageConfig::getIsDefault);
        queryWrapper.orderByDesc(SysMessageConfig::getCreateTime);

        Page<SysMessageConfig> result = messageConfigService.page(page, queryWrapper);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 根据配置ID查询详细信息
     */
    @ApiOperation("查询消息配置详情")
    @PreAuthorize("@ss.hasPermi('system:message:config:query')")
    @GetMapping("/{configId}")
    public Result<SysMessageConfig> getInfo(@ApiParam("配置ID") @PathVariable Long configId) {
        SysMessageConfig config = messageConfigService.getById(configId);
        return Result.success(config);
    }

    /**
     * 新增消息配置
     */
    @Log(title = "消息配置管理", businessType = Log.BusinessType.INSERT)
    @ApiOperation("新增消息配置")
    @PreAuthorize("@ss.hasPermi('system:message:config:add')")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysMessageConfig config) {
        // 如果设置为默认配置，需要将同类型的其他配置取消默认
        if ("1".equals(config.getIsDefault())) {
            cancelDefaultConfig(config.getMessageType());
        }

        boolean success = messageConfigService.save(config);
        return success ? Result.success() : Result.fail();
    }

    /**
     * 修改消息配置
     */
    @Log(title = "消息配置管理", businessType = Log.BusinessType.UPDATE)
    @ApiOperation("修改消息配置")
    @PreAuthorize("@ss.hasPermi('system:message:config:edit')")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysMessageConfig config) {
        // 如果设置为默认配置，需要将同类型的其他配置取消默认
        if ("1".equals(config.getIsDefault())) {
            cancelDefaultConfig(config.getMessageType(), config.getConfigId());
        }

        boolean success = messageConfigService.updateById(config);
        return success ? Result.success() : Result.fail();
    }

    /**
     * 删除消息配置
     */
    @Log(title = "消息配置管理", businessType = Log.BusinessType.DELETE)
    @ApiOperation("删除消息配置")
    @PreAuthorize("@ss.hasPermi('system:message:config:remove')")
    @DeleteMapping("/{configIds}")
    public Result<Void> remove(@ApiParam("配置ID数组") @PathVariable Long[] configIds) {
        List<Long> idList = Arrays.asList(configIds);
        boolean success = messageConfigService.removeByIds(idList);
        return success ? Result.success() : Result.fail();
    }

    /**
     * 修改消息配置状态
     */
    @Log(title = "消息配置管理", businessType = Log.BusinessType.UPDATE)
    @ApiOperation("修改消息配置状态")
    @PreAuthorize("@ss.hasPermi('system:message:config:edit')")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(@RequestBody SysMessageConfig config) {
        SysMessageConfig updateConfig = new SysMessageConfig();
        updateConfig.setConfigId(config.getConfigId());
        updateConfig.setStatus(config.getStatus());
        boolean success = messageConfigService.updateById(updateConfig);
        return success ? Result.success() : Result.fail();
    }

    /**
     * 设置为默认配置
     */
    @Log(title = "消息配置管理", businessType = Log.BusinessType.UPDATE)
    @ApiOperation("设置为默认配置")
    @PreAuthorize("@ss.hasPermi('system:message:config:edit')")
    @PutMapping("/setDefault/{configId}")
    public Result<Void> setDefault(@ApiParam("配置ID") @PathVariable Long configId) {
        SysMessageConfig config = messageConfigService.getById(configId);
        if (config == null) {
            return Result.fail("配置不存在");
        }

        // 取消同类型的其他默认配置
        cancelDefaultConfig(config.getMessageType(), configId);

        // 设置为默认
        SysMessageConfig updateConfig = new SysMessageConfig();
        updateConfig.setConfigId(configId);
        updateConfig.setIsDefault("1");
        boolean success = messageConfigService.updateById(updateConfig);
        return success ? Result.success() : Result.fail();
    }

    /**
     * 测试发送
     */
    @Log(title = "消息配置管理", businessType = Log.BusinessType.OTHER)
    @ApiOperation("测试发送消息")
    @PreAuthorize("@ss.hasPermi('system:message:config:test')")
    @PostMapping("/test")
    public Result<Void> testSend(@RequestBody Map<String, Object> params) {
        String messageType = (String) params.get("messageType");
        String receiver = (String) params.get("receiver");
        String subject = (String) params.get("subject");
        String content = (String) params.get("content");
        Long configId = params.get("configId") != null ? Long.parseLong(params.get("configId").toString()) : null;

        boolean success = messageSendService.testSend(messageType, receiver, subject, content, configId);
        return success ? Result.success("发送成功") : Result.fail("发送失败");
    }

    /**
     * 取消同类型的其他默认配置
     */
    private void cancelDefaultConfig(String messageType) {
        cancelDefaultConfig(messageType, null);
    }

    /**
     * 取消同类型的其他默认配置（排除指定ID）
     */
    private void cancelDefaultConfig(String messageType, Long excludeConfigId) {
        LambdaQueryWrapper<SysMessageConfig> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(SysMessageConfig::getMessageType, messageType);
        queryWrapper.eq(SysMessageConfig::getIsDefault, "1");
        queryWrapper.eq(SysMessageConfig::getDeleted, 0);
        if (excludeConfigId != null) {
            queryWrapper.ne(SysMessageConfig::getConfigId, excludeConfigId);
        }

        List<SysMessageConfig> configs = messageConfigService.list(queryWrapper);
        for (SysMessageConfig config : configs) {
            config.setIsDefault("0");
            messageConfigService.updateById(config);
        }
    }
}
