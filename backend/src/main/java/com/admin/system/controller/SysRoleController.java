package com.admin.system.controller;

import com.admin.common.PageResult;
import com.admin.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.dto.RoleDTO;
import com.admin.system.entity.SysRole;
import com.admin.system.service.ISysRoleService;
import com.admin.common.utils.PageUtils;
import com.admin.system.vo.RoleVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.admin.common.utils.ExcelUtil;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 角色信息
 *
 * @author Admin
 */
@Tag(name = "角色管理")
@RestController
@RequestMapping("/system/role")
@RequiredArgsConstructor
public class SysRoleController {

    private final ISysRoleService roleService;

    /**
     * 分页查询角色列表
     */
    @Operation(summary = "分页查询角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public PageResult<RoleVO> list(
            PageQuery pageQuery,
            @Parameter(description = "角色名称") @RequestParam(required = false) String roleName,
            @Parameter(description = "权限字符") @RequestParam(required = false) String roleKey,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        Page<SysRole> page = pageQuery.build();
        Page<RoleVO> result = roleService.selectRolePage(page, roleName, roleKey, status);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 查询所有角色列表
     */
    @Operation(summary = "查询所有角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/listAll")
    public Result<List<RoleVO>> listAll() {
        List<RoleVO> list = roleService.selectRoleList();
        return Result.success(list);
    }

    /**
     * 根据角色ID查询详细信息
     */
    @Operation(summary = "查询角色详情")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/{roleId}")
    public Result<RoleVO> getInfo(@Parameter(description = "角色ID") @PathVariable Long roleId) {
        RoleVO roleVO = roleService.selectRoleById(roleId);
        return Result.success(roleVO);
    }

    /**
     * 查询角色已分配的菜单ID列表
     */
    @Operation(summary = "查询角色已分配的菜单ID列表")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/menuIds/{roleId}")
    public Result<List<Long>> getMenuIds(@Parameter(description = "角色ID") @PathVariable Long roleId) {
        List<Long> menuIds = roleService.selectMenuIdsByRoleId(roleId);
        return Result.success(menuIds);
    }

    /**
     * 新增角色
     */
    @Operation(summary = "新增角色")
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody RoleDTO roleDTO) {
        roleService.insertRole(roleDTO);
        return Result.success("新增角色成功");
    }

    /**
     * 修改角色
     */
    @Operation(summary = "修改角色")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody RoleDTO roleDTO) {
        roleService.updateRole(roleDTO);
        return Result.success("修改角色成功");
    }

    /**
     * 删除角色
     */
    @Operation(summary = "删除角色")
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @DeleteMapping("/{roleIds}")
    public Result<Void> remove(@Parameter(description = "角色ID数组") @PathVariable Long[] roleIds) {
        roleService.deleteRoleByIds(roleIds);
        return Result.success("删除角色成功");
    }

    /**
     * 修改角色状态
     */
    @Operation(summary = "修改角色状态")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(
            @Parameter(description = "角色ID") @RequestParam Long roleId,
            @Parameter(description = "状态") @RequestParam String status) {
        roleService.updateRoleStatus(roleId, status);
        return Result.success("修改状态成功");
    }

    /**
     * 校验角色名称是否唯一
     */
    @Operation(summary = "校验角色名称是否唯一")
    @GetMapping("/checkRoleNameUnique")
    public Result<Boolean> checkRoleNameUnique(
            @Parameter(description = "角色名称") @RequestParam String roleName,
            @Parameter(description = "角色ID") @RequestParam(required = false) Long roleId) {
        boolean unique = roleService.checkRoleNameUnique(roleName, roleId);
        return Result.success(unique);
    }

    /**
     * 校验角色权限是否唯一
     */
    @Operation(summary = "校验角色权限是否唯一")
    @GetMapping("/checkRoleKeyUnique")
    public Result<Boolean> checkRoleKeyUnique(
            @Parameter(description = "权限字符") @RequestParam String roleKey,
            @Parameter(description = "角色ID") @RequestParam(required = false) Long roleId) {
        boolean unique = roleService.checkRoleKeyUnique(roleKey, roleId);
        return Result.success(unique);
    }

    /**
     * 保存角色菜单权限
     */
    @Operation(summary = "保存角色菜单权限")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping("/saveRoleMenus")
    public Result<Void> saveRoleMenus(
            @Parameter(description = "角色ID") @RequestParam Long roleId,
            @Parameter(description = "菜单ID数组") @RequestParam(required = false) Long[] menuIds) {
        roleService.saveRoleMenus(roleId, menuIds);
        return Result.success("分配权限成功");
    }

    /**
     * 导出角色数据
     */
    @Operation(summary = "导出角色数据")
    @PreAuthorize("@ss.hasPermi('system:role:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response,
                       @Parameter(description = "角色名称") @RequestParam(required = false) String roleName,
                       @Parameter(description = "权限字符") @RequestParam(required = false) String roleKey,
                       @Parameter(description = "状态") @RequestParam(required = false) String status) throws IOException {
        List<RoleVO> list = roleService.selectRoleListForExport(roleName, roleKey, status);
        String[] headers = {"角色ID", "角色名称", "权限字符", "显示顺序", "状态", "创建时间"};
        String[] fields = {"roleId", "roleName", "roleKey", "roleSort", "status", "createTime"};
        ExcelUtil.exportExcel(response, "角色数据", headers, fields, list);
    }
}
