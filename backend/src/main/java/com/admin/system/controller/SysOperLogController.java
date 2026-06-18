package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysOperLog;
import com.admin.system.service.ISysOperLogService;
import com.admin.system.utils.PageUtils;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.v3.oas.annotations.tags.Tag;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import com.admin.system.utils.ExcelUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 操作日志记录
 *
 * @author Admin
 */
@Tag(name = "操作日志管理")
@RestController
@RequestMapping("/system/operlog")
@RequiredArgsConstructor
public class SysOperLogController {

    private final ISysOperLogService operLogService;

    /**
     * 分页查询操作日志列表
     */
    @Operation(summary = "分页查询操作日志列表")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:list')")
    @GetMapping("/list")
    public PageResult<SysOperLog> list(
            PageQuery pageQuery,
            @Parameter(description = "模块标题") @RequestParam(required = false) String title,
            @Parameter(description = "操作人员") @RequestParam(required = false) String operName,
            @Parameter(description = "业务类型") @RequestParam(required = false) Integer businessType,
            @Parameter(description = "操作状态") @RequestParam(required = false) Integer status,
            @Parameter(description = "开始时间") @RequestParam(required = false) String beginTime,
            @Parameter(description = "结束时间") @RequestParam(required = false) String endTime) {

        Page<SysOperLog> page = pageQuery.build();
        Page<SysOperLog> result = operLogService.selectOperLogPage(page, title, operName, businessType, status, beginTime, endTime);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 根据日志ID查询详细信息
     */
    @Operation(summary = "查询操作日志详情")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:query')")
    @GetMapping("/{operId}")
    public Result<SysOperLog> getInfo(@Parameter(description = "日志ID") @PathVariable Long operId) {
        SysOperLog operLog = operLogService.getById(operId);
        return Result.success(operLog);
    }

    /**
     * 删除操作日志
     */
    @Operation(summary = "删除操作日志")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/{operIds}")
    public Result<Void> remove(@Parameter(description = "日志ID数组") @PathVariable Long[] operIds) {
        operLogService.deleteOperLogByIds(operIds);
        return Result.success("删除操作日志成功");
    }

    /**
     * 清空操作日志
     */
    @Operation(summary = "清空操作日志")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:remove')")
    @DeleteMapping("/clean")
    public Result<Void> clean() {
        operLogService.cleanOperLog();
        return Result.success("清空操作日志成功");
    }

    /**
     * 导出操作日志
     */
    @Operation(summary = "导出操作日志")
    @PreAuthorize("@ss.hasPermi('monitor:operlog:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response,
                       @Parameter(description = "模块标题") @RequestParam(required = false) String title,
                       @Parameter(description = "操作人员") @RequestParam(required = false) String operName,
                       @Parameter(description = "业务类型") @RequestParam(required = false) Integer businessType,
                       @Parameter(description = "操作状态") @RequestParam(required = false) Integer status,
                       @Parameter(description = "开始时间") @RequestParam(required = false) String beginTime,
                       @Parameter(description = "结束时间") @RequestParam(required = false) String endTime) throws IOException {
        List<SysOperLog> list = operLogService.selectOperLogList(title, operName, businessType, status, beginTime, endTime);
        String[] headers = {"日志ID", "模块标题", "业务类型", "操作人员", "请求方式", "操作状态", "操作IP", "操作时间"};
        String[] fields = {"operId", "title", "businessType", "operName", "requestMethod", "status", "operIp", "operTime"};
        ExcelUtil.exportExcel(response, "操作日志", headers, fields, list);
    }
}
