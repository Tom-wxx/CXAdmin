package com.admin.system.controller;

import com.admin.common.PageResult;
import com.admin.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysDictType;
import com.admin.system.service.ISysDictTypeService;
import com.admin.common.utils.PageUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
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
@Tag(name = "字典类型管理")
@RestController
@RequestMapping("/system/dict/type")
@RequiredArgsConstructor
public class SysDictTypeController {

    private final ISysDictTypeService dictTypeService;

    /**
     * 分页查询字典类型列表
     */
    @Operation(summary = "分页查询字典类型列表")
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public PageResult<SysDictType> list(
            PageQuery pageQuery,
            @Parameter(description = "字典名称") @RequestParam(required = false) String dictName,
            @Parameter(description = "字典类型") @RequestParam(required = false) String dictType,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        Page<SysDictType> page = pageQuery.build();
        Page<SysDictType> result = dictTypeService.selectDictTypePage(page, dictName, dictType, status);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 查询所有字典类型
     */
    @Operation(summary = "查询所有字典类型")
    @GetMapping("/listAll")
    public Result<List<SysDictType>> listAll() {
        List<SysDictType> list = dictTypeService.selectDictTypeList();
        return Result.success(list);
    }

    /**
     * 根据字典ID查询详细信息
     */
    @Operation(summary = "查询字典类型详情")
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping("/{dictId}")
    public Result<SysDictType> getInfo(@Parameter(description = "字典ID") @PathVariable Long dictId) {
        SysDictType dictType = dictTypeService.getById(dictId);
        return Result.success(dictType);
    }

    /**
     * 新增字典类型
     */
    @Operation(summary = "新增字典类型")
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysDictType dictType) {
        dictTypeService.insertDictType(dictType);
        return Result.success("新增字典类型成功");
    }

    /**
     * 修改字典类型
     */
    @Operation(summary = "修改字典类型")
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysDictType dictType) {
        dictTypeService.updateDictType(dictType);
        return Result.success("修改字典类型成功");
    }

    /**
     * 删除字典类型
     */
    @Operation(summary = "删除字典类型")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @DeleteMapping("/{dictIds}")
    public Result<Void> remove(@Parameter(description = "字典ID数组") @PathVariable Long[] dictIds) {
        dictTypeService.deleteDictTypeByIds(dictIds);
        return Result.success("删除字典类型成功");
    }

    /**
     * 校验字典类型是否唯一
     */
    @Operation(summary = "校验字典类型是否唯一")
    @GetMapping("/checkDictTypeUnique")
    public Result<Boolean> checkDictTypeUnique(
            @Parameter(description = "字典类型") @RequestParam String dictType,
            @Parameter(description = "字典ID") @RequestParam(required = false) Long dictId) {
        boolean unique = dictTypeService.checkDictTypeUnique(dictType, dictId);
        return Result.success(unique);
    }
}
