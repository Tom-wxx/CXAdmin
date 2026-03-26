package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysDictType;
import com.admin.system.service.ISysDictTypeService;
import com.admin.system.utils.PageUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 字典类型信息
 *
 * @author Admin
 */
@Api(tags = "字典类型管理")
@RestController
@RequestMapping("/system/dict/type")
@RequiredArgsConstructor
public class SysDictTypeController {

    private final ISysDictTypeService dictTypeService;

    /**
     * 分页查询字典类型列表
     */
    @ApiOperation("分页查询字典类型列表")
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public PageResult<SysDictType> list(
            PageQuery pageQuery,
            @ApiParam("字典名称") @RequestParam(required = false) String dictName,
            @ApiParam("字典类型") @RequestParam(required = false) String dictType,
            @ApiParam("状态") @RequestParam(required = false) String status) {

        Page<SysDictType> page = pageQuery.build();
        Page<SysDictType> result = dictTypeService.selectDictTypePage(page, dictName, dictType, status);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 查询所有字典类型
     */
    @ApiOperation("查询所有字典类型")
    @GetMapping("/listAll")
    public Result<List<SysDictType>> listAll() {
        List<SysDictType> list = dictTypeService.selectDictTypeList();
        return Result.success(list);
    }

    /**
     * 根据字典ID查询详细信息
     */
    @ApiOperation("查询字典类型详情")
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping("/{dictId}")
    public Result<SysDictType> getInfo(@ApiParam("字典ID") @PathVariable Long dictId) {
        SysDictType dictType = dictTypeService.getById(dictId);
        return Result.success(dictType);
    }

    /**
     * 新增字典类型
     */
    @ApiOperation("新增字典类型")
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysDictType dictType) {
        dictTypeService.insertDictType(dictType);
        return Result.success("新增字典类型成功");
    }

    /**
     * 修改字典类型
     */
    @ApiOperation("修改字典类型")
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysDictType dictType) {
        dictTypeService.updateDictType(dictType);
        return Result.success("修改字典类型成功");
    }

    /**
     * 删除字典类型
     */
    @ApiOperation("删除字典类型")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @DeleteMapping("/{dictIds}")
    public Result<Void> remove(@ApiParam("字典ID数组") @PathVariable Long[] dictIds) {
        dictTypeService.deleteDictTypeByIds(dictIds);
        return Result.success("删除字典类型成功");
    }

    /**
     * 校验字典类型是否唯一
     */
    @ApiOperation("校验字典类型是否唯一")
    @GetMapping("/checkDictTypeUnique")
    public Result<Boolean> checkDictTypeUnique(
            @ApiParam("字典类型") @RequestParam String dictType,
            @ApiParam("字典ID") @RequestParam(required = false) Long dictId) {
        boolean unique = dictTypeService.checkDictTypeUnique(dictType, dictId);
        return Result.success(unique);
    }
}
