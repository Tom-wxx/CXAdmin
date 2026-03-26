package com.admin.modules.workflow.controller;

import com.admin.modules.workflow.entity.ProcessDefinition;
import com.admin.modules.workflow.mapper.ProcessDefinitionMapper;
import com.admin.system.common.Result;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.Date;
import java.util.List;

/**
 * 流程定义控制器
 *
 * @author Admin
 */
@RestController
@RequestMapping("/workflow/definition")
@Tag(name = "流程定义管理")
public class ProcessDefinitionController {

    @Autowired
    private ProcessDefinitionMapper processDefinitionMapper;

    /**
     * 获取流程定义列表
     */
    @GetMapping("/list")
    @PreAuthorize("@ss.hasPermi('workflow:definition:list')")
    @Operation(summary = "流程定义列表")
    public Result<List<ProcessDefinition>> list(ProcessDefinition query) {
        LambdaQueryWrapper<ProcessDefinition> queryWrapper = new LambdaQueryWrapper<>();
        if (query.getProcessName() != null && !query.getProcessName().isEmpty()) {
            queryWrapper.like(ProcessDefinition::getProcessName, query.getProcessName());
        }
        if (query.getProcessType() != null && !query.getProcessType().isEmpty()) {
            queryWrapper.eq(ProcessDefinition::getProcessType, query.getProcessType());
        }
        if (query.getStatus() != null && !query.getStatus().isEmpty()) {
            queryWrapper.eq(ProcessDefinition::getStatus, query.getStatus());
        }
        queryWrapper.orderByDesc(ProcessDefinition::getCreateTime);
        List<ProcessDefinition> list = processDefinitionMapper.selectList(queryWrapper);
        return Result.success(list);
    }

    /**
     * 获取流程定义详情
     */
    @GetMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('workflow:definition:query')")
    @Operation(summary = "流程定义详情")
    public Result<ProcessDefinition> getById(@PathVariable Long id) {
        ProcessDefinition definition = processDefinitionMapper.selectById(id);
        return Result.success(definition);
    }

    /**
     * 新增流程定义
     */
    @PostMapping
    @PreAuthorize("@ss.hasPermi('workflow:definition:add')")
    @Operation(summary = "新增流程定义")
    public Result<Void> add(@RequestBody ProcessDefinition processDefinition) {
        processDefinition.setCreateTime(new Date());
        processDefinition.setStatus("draft");
        processDefinitionMapper.insert(processDefinition);
        return Result.success("新增成功");
    }

    /**
     * 修改流程定义
     */
    @PutMapping
    @PreAuthorize("@ss.hasPermi('workflow:definition:edit')")
    @Operation(summary = "修改流程定义")
    public Result<Void> update(@RequestBody ProcessDefinition processDefinition) {
        processDefinition.setUpdateTime(new Date());
        processDefinitionMapper.updateById(processDefinition);
        return Result.success("修改成功");
    }

    /**
     * 删除流程定义
     */
    @DeleteMapping("/{id}")
    @PreAuthorize("@ss.hasPermi('workflow:definition:remove')")
    @Operation(summary = "删除流程定义")
    public Result<Void> remove(@PathVariable Long id) {
        processDefinitionMapper.deleteById(id);
        return Result.success("删除成功");
    }

    /**
     * 发布流程定义
     */
    @PostMapping("/publish/{id}")
    @PreAuthorize("@ss.hasPermi('workflow:definition:publish')")
    @Operation(summary = "发布流程定义")
    public Result<Void> publish(@PathVariable Long id) {
        ProcessDefinition definition = processDefinitionMapper.selectById(id);
        if (definition == null) {
            return Result.fail("流程定义不存在");
        }
        definition.setStatus("published");
        definition.setUpdateTime(new Date());
        processDefinitionMapper.updateById(definition);
        return Result.success("发布成功");
    }

    /**
     * 停用流程定义
     */
    @PostMapping("/disable/{id}")
    @PreAuthorize("@ss.hasPermi('workflow:definition:edit')")
    @Operation(summary = "停用流程定义")
    public Result<Void> disable(@PathVariable Long id) {
        ProcessDefinition definition = processDefinitionMapper.selectById(id);
        if (definition == null) {
            return Result.fail("流程定义不存在");
        }
        definition.setStatus("disabled");
        definition.setUpdateTime(new Date());
        processDefinitionMapper.updateById(definition);
        return Result.success("已停用");
    }
}
