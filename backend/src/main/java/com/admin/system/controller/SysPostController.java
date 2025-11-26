package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.entity.SysPost;
import com.admin.system.service.ISysPostService;
import com.admin.system.utils.PageUtils;
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
 * 岗位信息
 *
 * @author Admin
 */
@Api(tags = "岗位管理")
@RestController
@RequestMapping("/system/post")
public class SysPostController {

    @Autowired
    private ISysPostService postService;

    /**
     * 获取岗位列表
     */
    @ApiOperation("获取岗位列表")
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/list")
    public Result<List<SysPost>> list(SysPost post) {
        List<SysPost> list = postService.selectPostList(post);
        return Result.success(list);
    }

    /**
     * 分页查询岗位列表
     */
    @ApiOperation("分页查询岗位列表")
    @PreAuthorize("@ss.hasPermi('system:post:list')")
    @GetMapping("/page")
    public PageResult<SysPost> page(
            PageQuery pageQuery,
            @ApiParam("岗位编码") @RequestParam(required = false) String postCode,
            @ApiParam("岗位名称") @RequestParam(required = false) String postName,
            @ApiParam("状态") @RequestParam(required = false) String status) {

        Page<SysPost> page = pageQuery.build();
        Page<SysPost> result = postService.selectPostPage(page, postCode, postName, status);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 根据岗位编号获取详细信息
     */
    @ApiOperation("查询岗位详情")
    @PreAuthorize("@ss.hasPermi('system:post:query')")
    @GetMapping("/{postId}")
    public Result<SysPost> getInfo(@ApiParam("岗位ID") @PathVariable Long postId) {
        SysPost post = postService.selectPostById(postId);
        return Result.success(post);
    }

    /**
     * 新增岗位
     */
    @ApiOperation("新增岗位")
    @PreAuthorize("@ss.hasPermi('system:post:add')")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody SysPost post) {
        postService.insertPost(post);
        return Result.success("新增岗位成功");
    }

    /**
     * 修改岗位
     */
    @ApiOperation("修改岗位")
    @PreAuthorize("@ss.hasPermi('system:post:edit')")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody SysPost post) {
        postService.updatePost(post);
        return Result.success("修改岗位成功");
    }

    /**
     * 删除岗位
     */
    @ApiOperation("删除岗位")
    @PreAuthorize("@ss.hasPermi('system:post:remove')")
    @DeleteMapping("/{postIds}")
    public Result<Void> remove(@ApiParam("岗位ID数组") @PathVariable Long[] postIds) {
        postService.deletePostByIds(postIds);
        return Result.success("删除岗位成功");
    }

    /**
     * 校验岗位名称是否唯一
     */
    @ApiOperation("校验岗位名称是否唯一")
    @GetMapping("/checkPostNameUnique")
    public Result<Boolean> checkPostNameUnique(@RequestParam String postName,
                                                @RequestParam(required = false) Long postId) {
        SysPost post = new SysPost();
        post.setPostName(postName);
        post.setPostId(postId);
        boolean unique = postService.checkPostNameUnique(post);
        return Result.success(unique);
    }

    /**
     * 校验岗位编码是否唯一
     */
    @ApiOperation("校验岗位编码是否唯一")
    @GetMapping("/checkPostCodeUnique")
    public Result<Boolean> checkPostCodeUnique(@RequestParam String postCode,
                                                @RequestParam(required = false) Long postId) {
        SysPost post = new SysPost();
        post.setPostCode(postCode);
        post.setPostId(postId);
        boolean unique = postService.checkPostCodeUnique(post);
        return Result.success(unique);
    }

}
