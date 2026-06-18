package com.admin.system.sso.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.sso.dto.SsoProviderDTO;
import com.admin.system.sso.entity.SysSsoProvider;
import com.admin.system.sso.service.ISysSsoProviderService;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.validation.Valid;

@Tag(name = "SSO 身份认证源")
@RestController
@RequestMapping("/system/sso")
@RequiredArgsConstructor
public class SysSsoProviderController {

    private final ISysSsoProviderService providerService;

    @Operation(summary = "分页列表")
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('system:sso:list')")
    public PageResult<SysSsoProvider> list(
            @RequestParam(defaultValue = "1") long current,
            @RequestParam(defaultValue = "10") long size) {
        Page<SysSsoProvider> page = providerService.lambdaQuery()
                .orderByAsc(SysSsoProvider::getOrderNum)
                .page(new Page<>(current, size));
        // 不要把密文回传给前端
        page.getRecords().forEach(p -> p.setClientSecret(null));
        return new PageResult<>(page.getRecords(), page.getTotal());
    }

    @Operation(summary = "详情")
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:sso:query')")
    public Result<SysSsoProvider> get(@PathVariable Long id) {
        SysSsoProvider p = providerService.getById(id);
        if (p != null) p.setClientSecret(null);
        return Result.success(p);
    }

    @Operation(summary = "新增")
    @PostMapping
    @PreAuthorize("@ss.hasPermi('system:sso:add')")
    public Result<Void> add(@Valid @RequestBody SsoProviderDTO dto) {
        providerService.saveProvider(dto);
        return Result.success();
    }

    @Operation(summary = "修改")
    @PutMapping
    @PreAuthorize("@ss.hasPermi('system:sso:edit')")
    public Result<Void> update(@Valid @RequestBody SsoProviderDTO dto) {
        providerService.updateProvider(dto);
        return Result.success();
    }

    @Operation(summary = "删除")
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('system:sso:remove')")
    public Result<Void> remove(@PathVariable Long id) {
        providerService.removeById(id);
        return Result.success();
    }
}
