package com.admin.system.controller;

import com.admin.common.Result;
import com.admin.system.entity.SysDept;
import com.admin.system.service.ISysDeptService;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 部门信息
 *
 * @author Admin
 */
@Tag(name = "部门管理")
@RestController
@RequestMapping("/system/dept")
@RequiredArgsConstructor
public class SysDeptController {

    private final ISysDeptService deptService;

    /**
     * 获取部门列表
     */
    @Operation(summary = "获取部门列表")
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/list")
    public Result<List<SysDept>> list(SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return Result.success(depts);
    }

    /**
     * 查询部门列表（树形结构）
     */
    @Operation(summary = "查询部门树形列表")
    @PreAuthorize("@ss.hasPermi('system:dept:list')")
    @GetMapping("/treeList")
    public Result<List<SysDept>> treeList(SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return Result.success(deptService.buildDeptTree(depts));
    }

    /**
     * 根据部门编号获取详细信息
     */
    @Operation(summary = "查询部门详情")
    @PreAuthorize("@ss.hasPermi('system:dept:query')")
    @GetMapping("/{deptId}")
    public Result<SysDept> getInfo(@Parameter(description = "部门ID") @PathVariable Long deptId) {
        SysDept dept = deptService.selectDeptById(deptId);
        return Result.success(dept);
    }

    /**
     * 新增部门
     */
    @Operation(summary = "新增部门")
    @PreAuthorize("@ss.hasPermi('system:dept:add')")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysDept dept) {
        deptService.insertDept(dept);
        return Result.success("新增部门成功");
    }

    /**
     * 修改部门
     */
    @Operation(summary = "修改部门")
    @PreAuthorize("@ss.hasPermi('system:dept:edit')")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysDept dept) {
        deptService.updateDept(dept);
        return Result.success("修改部门成功");
    }

    /**
     * 删除部门
     */
    @Operation(summary = "删除部门")
    @PreAuthorize("@ss.hasPermi('system:dept:remove')")
    @DeleteMapping("/{deptId}")
    public Result<Void> remove(@Parameter(description = "部门ID") @PathVariable Long deptId) {
        deptService.deleteDeptById(deptId);
        return Result.success("删除部门成功");
    }

    /**
     * 查询部门下拉树列表
     */
    @Operation(summary = "查询部门下拉树列表")
    @GetMapping("/treeSelect")
    public Result<List<SysDept>> treeSelect(SysDept dept) {
        List<SysDept> depts = deptService.selectDeptList(dept);
        return Result.success(deptService.buildDeptTree(depts));
    }

}
