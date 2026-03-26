package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysConfig;
import com.admin.system.service.ISysConfigService;
import com.admin.system.utils.PageUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

/**
 * 参数配置信息
 *
 * @author Admin
 */
@Api(tags = "参数配置管理")
@RestController
@RequestMapping("/system/config")
@RequiredArgsConstructor
public class SysConfigController {

    private final ISysConfigService configService;

    /**
     * 分页查询参数配置列表
     */
    @ApiOperation("分页查询参数配置列表")
    @PreAuthorize("@ss.hasPermi('system:config:list')")
    @GetMapping("/list")
    public PageResult<SysConfig> list(
            PageQuery pageQuery,
            @ApiParam("参数名称") @RequestParam(required = false) String configName,
            @ApiParam("参数键名") @RequestParam(required = false) String configKey,
            @ApiParam("系统内置") @RequestParam(required = false) String configType) {

        Page<SysConfig> page = pageQuery.build();
        Page<SysConfig> result = configService.selectConfigPage(page, configName, configKey, configType);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 根据参数键名查询参数值
     */
    @ApiOperation("根据参数键名查询参数值")
    @GetMapping("/configKey/{configKey}")
    public Result<String> getConfigKey(@ApiParam("参数键名") @PathVariable String configKey) {
        String configValue = configService.selectConfigByKey(configKey);
        return Result.success(configValue);
    }

    /**
     * 根据参数ID查询详细信息
     */
    @ApiOperation("查询参数配置详情")
    @PreAuthorize("@ss.hasPermi('system:config:query')")
    @GetMapping("/{configId}")
    public Result<SysConfig> getInfo(@ApiParam("参数ID") @PathVariable Long configId) {
        SysConfig config = configService.getById(configId);
        return Result.success(config);
    }

    /**
     * 新增参数配置
     */
    @ApiOperation("新增参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:add')")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysConfig config) {
        configService.insertConfig(config);
        return Result.success("新增参数配置成功");
    }

    /**
     * 修改参数配置
     */
    @ApiOperation("修改参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:edit')")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysConfig config) {
        configService.updateConfig(config);
        return Result.success("修改参数配置成功");
    }

    /**
     * 删除参数配置
     */
    @ApiOperation("删除参数配置")
    @PreAuthorize("@ss.hasPermi('system:config:remove')")
    @DeleteMapping("/{configIds}")
    public Result<Void> remove(@ApiParam("参数ID数组") @PathVariable Long[] configIds) {
        configService.deleteConfigByIds(configIds);
        return Result.success("删除参数配置成功");
    }

    /**
     * 校验参数键名是否唯一
     */
    @ApiOperation("校验参数键名是否唯一")
    @GetMapping("/checkConfigKeyUnique")
    public Result<Boolean> checkConfigKeyUnique(
            @ApiParam("参数键名") @RequestParam String configKey,
            @ApiParam("参数ID") @RequestParam(required = false) Long configId) {
        boolean unique = configService.checkConfigKeyUnique(configKey, configId);
        return Result.success(unique);
    }
}
