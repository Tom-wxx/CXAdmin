package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.dto.RoleDTO;
import com.admin.system.entity.SysRole;
import com.admin.system.service.ISysRoleService;
import com.admin.system.utils.PageUtils;
import com.admin.system.vo.RoleVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 角色信息
 *
 * @author Admin
 */
@Api(tags = "角色管理")
@RestController
@RequestMapping("/system/role")
public class SysRoleController {

    @Autowired
    private ISysRoleService roleService;

    /**
     * 分页查询角色列表
     */
    @ApiOperation("分页查询角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:list')")
    @GetMapping("/list")
    public PageResult<RoleVO> list(
            PageQuery pageQuery,
            @ApiParam("角色名称") @RequestParam(required = false) String roleName,
            @ApiParam("权限字符") @RequestParam(required = false) String roleKey,
            @ApiParam("状态") @RequestParam(required = false) String status) {

        Page<SysRole> page = pageQuery.build();
        Page<RoleVO> result = roleService.selectRolePage(page, roleName, roleKey, status);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 查询所有角色列表
     */
    @ApiOperation("查询所有角色列表")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/listAll")
    public Result<List<RoleVO>> listAll() {
        List<RoleVO> list = roleService.selectRoleList();
        return Result.success(list);
    }

    /**
     * 根据角色ID查询详细信息
     */
    @ApiOperation("查询角色详情")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/{roleId}")
    public Result<RoleVO> getInfo(@ApiParam("角色ID") @PathVariable Long roleId) {
        RoleVO roleVO = roleService.selectRoleById(roleId);
        return Result.success(roleVO);
    }

    /**
     * 查询角色已分配的菜单ID列表
     */
    @ApiOperation("查询角色已分配的菜单ID列表")
    @PreAuthorize("@ss.hasPermi('system:role:query')")
    @GetMapping("/menuIds/{roleId}")
    public Result<List<Long>> getMenuIds(@ApiParam("角色ID") @PathVariable Long roleId) {
        List<Long> menuIds = roleService.selectMenuIdsByRoleId(roleId);
        return Result.success(menuIds);
    }

    /**
     * 新增角色
     */
    @ApiOperation("新增角色")
    @PreAuthorize("@ss.hasPermi('system:role:add')")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody RoleDTO roleDTO) {
        roleService.insertRole(roleDTO);
        return Result.success("新增角色成功");
    }

    /**
     * 修改角色
     */
    @ApiOperation("修改角色")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody RoleDTO roleDTO) {
        roleService.updateRole(roleDTO);
        return Result.success("修改角色成功");
    }

    /**
     * 删除角色
     */
    @ApiOperation("删除角色")
    @PreAuthorize("@ss.hasPermi('system:role:remove')")
    @DeleteMapping("/{roleIds}")
    public Result<Void> remove(@ApiParam("角色ID数组") @PathVariable Long[] roleIds) {
        roleService.deleteRoleByIds(roleIds);
        return Result.success("删除角色成功");
    }

    /**
     * 修改角色状态
     */
    @ApiOperation("修改角色状态")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(
            @ApiParam("角色ID") @RequestParam Long roleId,
            @ApiParam("状态") @RequestParam String status) {
        roleService.updateRoleStatus(roleId, status);
        return Result.success("修改状态成功");
    }

    /**
     * 校验角色名称是否唯一
     */
    @ApiOperation("校验角色名称是否唯一")
    @GetMapping("/checkRoleNameUnique")
    public Result<Boolean> checkRoleNameUnique(
            @ApiParam("角色名称") @RequestParam String roleName,
            @ApiParam("角色ID") @RequestParam(required = false) Long roleId) {
        boolean unique = roleService.checkRoleNameUnique(roleName, roleId);
        return Result.success(unique);
    }

    /**
     * 校验角色权限是否唯一
     */
    @ApiOperation("校验角色权限是否唯一")
    @GetMapping("/checkRoleKeyUnique")
    public Result<Boolean> checkRoleKeyUnique(
            @ApiParam("权限字符") @RequestParam String roleKey,
            @ApiParam("角色ID") @RequestParam(required = false) Long roleId) {
        boolean unique = roleService.checkRoleKeyUnique(roleKey, roleId);
        return Result.success(unique);
    }

    /**
     * 保存角色菜单权限
     */
    @ApiOperation("保存角色菜单权限")
    @PreAuthorize("@ss.hasPermi('system:role:edit')")
    @PutMapping("/saveRoleMenus")
    public Result<Void> saveRoleMenus(
            @ApiParam("角色ID") @RequestParam Long roleId,
            @ApiParam("菜单ID数组") @RequestParam(required = false) Long[] menuIds) {
        roleService.saveRoleMenus(roleId, menuIds);
        return Result.success("分配权限成功");
    }
}
