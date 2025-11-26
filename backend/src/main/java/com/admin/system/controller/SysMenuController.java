package com.admin.system.controller;

import com.admin.system.common.Result;
import com.admin.system.dto.MenuDTO;
import com.admin.system.service.ISysMenuService;
import com.admin.system.vo.MenuVO;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 菜单信息
 *
 * @author Admin
 */
@Api(tags = "菜单管理")
@RestController
@RequestMapping("/system/menu")
public class SysMenuController {

    @Autowired
    private ISysMenuService menuService;

    /**
     * 查询菜单列表（树形结构）
     */
    @ApiOperation("查询菜单列表")
    @PreAuthorize("@ss.hasPermi('system:menu:list')")
    @GetMapping("/list")
    public Result<List<MenuVO>> list(
            @ApiParam("菜单名称") @RequestParam(required = false) String menuName,
            @ApiParam("状态") @RequestParam(required = false) String status) {
        List<MenuVO> list = menuService.selectMenuTreeList(menuName, status);
        return Result.success(list);
    }

    /**
     * 根据菜单ID查询详细信息
     */
    @ApiOperation("查询菜单详情")
    @PreAuthorize("@ss.hasPermi('system:menu:query')")
    @GetMapping("/{menuId}")
    public Result<MenuVO> getInfo(@ApiParam("菜单ID") @PathVariable Long menuId) {
        MenuVO menuVO = menuService.selectMenuById(menuId);
        return Result.success(menuVO);
    }

    /**
     * 新增菜单
     */
    @ApiOperation("新增菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:add')")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody MenuDTO menuDTO) {
        menuService.insertMenu(menuDTO);
        return Result.success("新增菜单成功");
    }

    /**
     * 修改菜单
     */
    @ApiOperation("修改菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:edit')")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody MenuDTO menuDTO) {
        menuService.updateMenu(menuDTO);
        return Result.success("修改菜单成功");
    }

    /**
     * 删除菜单
     */
    @ApiOperation("删除菜单")
    @PreAuthorize("@ss.hasPermi('system:menu:remove')")
    @DeleteMapping("/{menuId}")
    public Result<Void> remove(
            @ApiParam("菜单ID") @PathVariable Long menuId,
            @ApiParam("是否强制删除") @RequestParam(defaultValue = "false") Boolean force) {
        menuService.deleteMenuById(menuId, force);
        return Result.success("删除菜单成功");
    }

    /**
     * 校验菜单名称是否唯一
     */
    @ApiOperation("校验菜单名称是否唯一")
    @GetMapping("/checkMenuNameUnique")
    public Result<Boolean> checkMenuNameUnique(
            @ApiParam("菜单名称") @RequestParam String menuName,
            @ApiParam("菜单ID") @RequestParam(required = false) Long menuId,
            @ApiParam("父菜单ID") @RequestParam(required = false) Long parentId) {
        boolean unique = menuService.checkMenuNameUnique(menuName, menuId, parentId);
        return Result.success(unique);
    }

    /**
     * 获取菜单下拉树列表
     */
    @ApiOperation("获取菜单下拉树列表")
    @GetMapping("/treeselect")
    public Result<List<MenuVO>> treeselect() {
        List<MenuVO> list = menuService.selectMenuTreeList(null, "0");
        return Result.success(list);
    }
}
