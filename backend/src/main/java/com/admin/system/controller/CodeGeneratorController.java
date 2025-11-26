package com.admin.system.controller;

import com.admin.system.common.Result;
import com.admin.system.generator.GenConfig;
import com.admin.system.generator.TableInfo;
import com.admin.system.service.ICodeGeneratorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.List;

/**
 * 代码生成器Controller
 *
 * @author Admin
 */
@RestController
@RequestMapping("/tool/generator")
public class CodeGeneratorController {

    @Autowired
    private ICodeGeneratorService codeGeneratorService;

    /**
     * 查询数据库表列表
     */
    @PreAuthorize("@ss.hasPermi('tool:generator:list')")
    @GetMapping("/list")
    public Result<List<TableInfo>> list(@RequestParam(required = false) String tableName) {
        List<TableInfo> tableList = codeGeneratorService.getTableList(tableName);
        return Result.success(tableList);
    }

    /**
     * 根据表名查询表信息
     */
    @PreAuthorize("@ss.hasPermi('tool:generator:query')")
    @GetMapping("/{tableName}")
    public Result<TableInfo> getInfo(@PathVariable String tableName) {
        TableInfo tableInfo = codeGeneratorService.getTableInfo(tableName);
        return Result.success(tableInfo);
    }

    /**
     * 生成代码
     */
    @PreAuthorize("@ss.hasPermi('tool:generator:code')")
    @PostMapping("/generate")
    public void generateCode(@RequestBody GenConfig config, HttpServletResponse response) throws IOException {
        byte[] data = codeGeneratorService.generateCode(config.getTableName(), config);

        response.reset();
        response.setHeader("Content-Disposition", "attachment; filename=\"" + config.getTableName() + ".zip\"");
        response.addHeader("Content-Length", "" + data.length);
        response.setContentType("application/octet-stream; charset=UTF-8");

        response.getOutputStream().write(data);
        response.getOutputStream().flush();
    }
}
