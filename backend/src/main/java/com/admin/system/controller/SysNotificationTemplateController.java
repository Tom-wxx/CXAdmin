package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysNotificationTemplate;
import com.admin.system.service.ISysNotificationTemplateService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.Arrays;
import java.util.List;

/**
 * 通知模板控制器
 *
 * @author Admin
 */
@RestController
@RequestMapping("/system/notification/template")
@Tag(name = "通知模板管理")
@RequiredArgsConstructor
public class SysNotificationTemplateController {

    private final ISysNotificationTemplateService templateService;

    /**
     * 分页查询模板列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('system:notification:template:list')")
    @Operation(summary = "分页查询模板列表")
    public PageResult<SysNotificationTemplate> list(PageQuery pageQuery, SysNotificationTemplate template) {
        LambdaQueryWrapper<SysNotificationTemplate> queryWrapper = new LambdaQueryWrapper<>();

        // 模板名称搜索
        if (template.getTemplateName() != null && !template.getTemplateName().isEmpty()) {
            queryWrapper.like(SysNotificationTemplate::getTemplateName, template.getTemplateName());
        }

        // 模板编码搜索
        if (template.getTemplateCode() != null && !template.getTemplateCode().isEmpty()) {
            queryWrapper.like(SysNotificationTemplate::getTemplateCode, template.getTemplateCode());
        }

        // 类型筛选
        if (template.getType() != null && !template.getType().isEmpty()) {
            queryWrapper.eq(SysNotificationTemplate::getType, template.getType());
        }

        // 状态筛选
        if (template.getStatus() != null && !template.getStatus().isEmpty()) {
            queryWrapper.eq(SysNotificationTemplate::getStatus, template.getStatus());
        }

        queryWrapper.orderByDesc(SysNotificationTemplate::getCreateTime);

        Page<SysNotificationTemplate> page = new Page<>(pageQuery.getPageNum(), pageQuery.getPageSize());
        Page<SysNotificationTemplate> result = templateService.page(page, queryWrapper);

        return new PageResult<>(result.getRecords(), result.getTotal());
    }

    /**
     * 查询所有模板
     */
    @GetMapping("/all")
    @PreAuthorize("@ss.hasPermi('system:notification:template:list')")
    @Operation(summary = "查询所有模板")
    public Result<List<SysNotificationTemplate>> getAllTemplates() {
        List<SysNotificationTemplate> list = templateService.list();
        return Result.success(list);
    }

    /**
     * 查询模板详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:notification:template:query')")
    @Operation(summary = "查询模板详情")
    public Result<SysNotificationTemplate> getById(@PathVariable Long id) {
        SysNotificationTemplate template = templateService.getById(id);
        return Result.success(template);
    }

    /**
     * 新增模板
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:notification:template:add')")
    @Operation(summary = "新增模板")
    public Result<Void> add(@Validated @RequestBody SysNotificationTemplate template) {
        // 检查模板编码是否已存在
        SysNotificationTemplate existing = templateService.getByTemplateCode(template.getTemplateCode());
        if (existing != null) {
            return Result.fail("模板编码已存在");
        }

        boolean success = templateService.save(template);
        return success ? Result.success() : Result.fail("新增失败");
    }

    /**
     * 修改模板
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('system:notification:template:edit')")
    @Operation(summary = "修改模板")
    public Result<Void> update(@Validated @RequestBody SysNotificationTemplate template) {
        // 检查模板编码是否重复（排除自己）
        SysNotificationTemplate existing = templateService.getByTemplateCode(template.getTemplateCode());
        if (existing != null && !existing.getId().equals(template.getId())) {
            return Result.fail("模板编码已存在");
        }

        boolean success = templateService.updateById(template);
        return success ? Result.success() : Result.fail("修改失败");
    }

    /**
     * 删除模板
     */
    @DeleteMapping("/{ids}")
    @PreAuthorize("@ss.hasPermi('system:notification:template:remove')")
    @Operation(summary = "删除模板")
    public Result<Void> delete(@PathVariable Long[] ids) {
        boolean success = templateService.removeByIds(Arrays.asList(ids));
        return success ? Result.success() : Result.fail("删除失败");
    }

    /**
     * 修改模板状态
     */
    @PutMapping("/status/{id}/{status}")
    @PreAuthorize("@ss.hasPermi('system:notification:template:edit')")
    @Operation(summary = "修改模板状态")
    public Result<Void> changeStatus(@PathVariable Long id, @PathVariable String status) {
        SysNotificationTemplate template = new SysNotificationTemplate();
        template.setId(id);
        template.setStatus(status);

        boolean success = templateService.updateById(template);
        return success ? Result.success() : Result.fail("修改失败");
    }

}
