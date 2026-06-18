package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysDictData;
import com.admin.system.service.ISysDictDataService;
import com.admin.system.utils.PageUtils;
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
 * 字典数据信息
 *
 * @author Admin
 */
@Tag(name = "字典数据管理")
@RestController
@RequestMapping("/system/dict/data")
@RequiredArgsConstructor
public class SysDictDataController {

    private final ISysDictDataService dictDataService;

    /**
     * 分页查询字典数据列表
     */
    @Operation(summary = "分页查询字典数据列表")
    @PreAuthorize("@ss.hasPermi('system:dict:list')")
    @GetMapping("/list")
    public PageResult<SysDictData> list(
            PageQuery pageQuery,
            @Parameter(description = "字典类型") @RequestParam(required = false) String dictType,
            @Parameter(description = "字典标签") @RequestParam(required = false) String dictLabel,
            @Parameter(description = "状态") @RequestParam(required = false) String status) {

        Page<SysDictData> page = pageQuery.build();
        Page<SysDictData> result = dictDataService.selectDictDataPage(page, dictType, dictLabel, status);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 根据字典类型查询字典数据
     */
    @Operation(summary = "根据字典类型查询字典数据")
    @GetMapping("/type/{dictType}")
    public Result<List<SysDictData>> getDictDataByType(@Parameter(description = "字典类型") @PathVariable String dictType) {
        List<SysDictData> list = dictDataService.selectDictDataByType(dictType);
        return Result.success(list);
    }

    /**
     * 根据字典编码查询详细信息
     */
    @Operation(summary = "查询字典数据详情")
    @PreAuthorize("@ss.hasPermi('system:dict:query')")
    @GetMapping("/{dictCode}")
    public Result<SysDictData> getInfo(@Parameter(description = "字典编码") @PathVariable Long dictCode) {
        SysDictData dictData = dictDataService.getById(dictCode);
        return Result.success(dictData);
    }

    /**
     * 新增字典数据
     */
    @Operation(summary = "新增字典数据")
    @PreAuthorize("@ss.hasPermi('system:dict:add')")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysDictData dictData) {
        dictDataService.insertDictData(dictData);
        return Result.success("新增字典数据成功");
    }

    /**
     * 修改字典数据
     */
    @Operation(summary = "修改字典数据")
    @PreAuthorize("@ss.hasPermi('system:dict:edit')")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysDictData dictData) {
        dictDataService.updateDictData(dictData);
        return Result.success("修改字典数据成功");
    }

    /**
     * 删除字典数据
     */
    @Operation(summary = "删除字典数据")
    @PreAuthorize("@ss.hasPermi('system:dict:remove')")
    @DeleteMapping("/{dictCodes}")
    public Result<Void> remove(@Parameter(description = "字典编码数组") @PathVariable Long[] dictCodes) {
        dictDataService.deleteDictDataByIds(dictCodes);
        return Result.success("删除字典数据成功");
    }
}
