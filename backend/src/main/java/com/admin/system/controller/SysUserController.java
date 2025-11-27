package com.admin.system.controller;

import com.admin.system.common.PageResult;
import com.admin.system.common.Result;
import com.admin.system.dto.PageQuery;
import com.admin.system.dto.UserDTO;
import com.admin.system.entity.SysUser;
import com.admin.system.service.ISysUserService;
import com.admin.system.utils.PageUtils;
import com.admin.system.vo.UserVO;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import com.admin.system.utils.ExcelUtil;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 用户信息
 *
 * @author Admin
 */
@Api(tags = "用户管理")
@RestController
@RequestMapping("/system/user")
public class SysUserController {

    @Autowired
    private ISysUserService userService;

    @Autowired
    private com.admin.system.service.ISysDeptService deptService;

    @Autowired
    private com.admin.system.service.ISysPostService postService;

    @Autowired
    private com.admin.system.service.ISysRoleService roleService;

    /**
     * 分页查询用户列表
     */
    @ApiOperation("分页查询用户列表")
    @PreAuthorize("@ss.hasPermi('system:user:list')")
    @GetMapping("/list")
    public PageResult<UserVO> list(
            PageQuery pageQuery,
            @ApiParam("用户名") @RequestParam(required = false) String username,
            @ApiParam("手机号") @RequestParam(required = false) String phone,
            @ApiParam("状态") @RequestParam(required = false) String status) {

        Page<SysUser> page = pageQuery.build();
        Page<UserVO> result = userService.selectUserPage(page, username, phone, status);
        return PageUtils.buildPageResult(result);
    }

    /**
     * 根据用户ID查询详细信息
     */
    @ApiOperation("查询用户详情")
    @PreAuthorize("@ss.hasPermi('system:user:query')")
    @GetMapping("/{userId}")
    public Result<Map<String, Object>> getInfo(@ApiParam("用户ID") @PathVariable Long userId) {
        Map<String, Object> result = new HashMap<>();

        // 获取用户基本信息
        UserVO userVO = userService.selectUserById(userId);
        result.put("user", userVO);

        // 获取用户的岗位ID列表
        List<Long> postIds = userService.selectPostIdsByUserId(userId);
        result.put("postIds", postIds);

        // 获取用户的角色ID列表
        List<Long> roleIds = userService.selectRoleIdsByUserId(userId);
        result.put("roleIds", roleIds);

        return Result.success(result);
    }

    /**
     * 新增用户
     */
    @ApiOperation("新增用户")
    @PreAuthorize("@ss.hasPermi('system:user:add')")
    @PostMapping
    public Result<Void> add(@Validated @RequestBody UserDTO userDTO) {
        userService.insertUser(userDTO);
        return Result.success("新增用户成功");
    }

    /**
     * 修改用户
     */
    @ApiOperation("修改用户")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PutMapping
    public Result<Void> edit(@Validated @RequestBody UserDTO userDTO) {
        userService.updateUser(userDTO);
        return Result.success("修改用户成功");
    }

    /**
     * 删除用户
     */
    @ApiOperation("删除用户")
    @PreAuthorize("@ss.hasPermi('system:user:remove')")
    @DeleteMapping("/{userIds}")
    public Result<Void> remove(@ApiParam("用户ID数组") @PathVariable Long[] userIds) {
        userService.deleteUserByIds(userIds);
        return Result.success("删除用户成功");
    }

    /**
     * 重置密码
     */
    @ApiOperation("重置密码")
    @PreAuthorize("@ss.hasPermi('system:user:resetPwd')")
    @PutMapping("/resetPwd")
    public Result<Void> resetPwd(
            @ApiParam("用户ID") @RequestParam Long userId,
            @ApiParam("新密码") @RequestParam String newPassword) {
        userService.resetPassword(userId, newPassword);
        return Result.success("重置密码成功");
    }

    /**
     * 修改用户状态
     */
    @ApiOperation("修改用户状态")
    @PreAuthorize("@ss.hasPermi('system:user:edit')")
    @PutMapping("/changeStatus")
    public Result<Void> changeStatus(
            @ApiParam("用户ID") @RequestParam Long userId,
            @ApiParam("状态") @RequestParam String status) {
        userService.updateUserStatus(userId, status);
        return Result.success("修改状态成功");
    }

    /**
     * 校验用户名是否唯一
     */
    @ApiOperation("校验用户名是否唯一")
    @GetMapping("/checkUsernameUnique")
    public Result<Boolean> checkUsernameUnique(
            @ApiParam("用户名") @RequestParam String username,
            @ApiParam("用户ID") @RequestParam(required = false) Long userId) {
        boolean unique = userService.checkUsernameUnique(username, userId);
        return Result.success(unique);
    }

    /**
     * 校验手机号是否唯一
     */
    @ApiOperation("校验手机号是否唯一")
    @GetMapping("/checkPhoneUnique")
    public Result<Boolean> checkPhoneUnique(
            @ApiParam("手机号") @RequestParam String phone,
            @ApiParam("用户ID") @RequestParam(required = false) Long userId) {
        boolean unique = userService.checkPhoneUnique(phone, userId);
        return Result.success(unique);
    }

    /**
     * 校验邮箱是否唯一
     */
    @ApiOperation("校验邮箱是否唯一")
    @GetMapping("/checkEmailUnique")
    public Result<Boolean> checkEmailUnique(
            @ApiParam("邮箱") @RequestParam String email,
            @ApiParam("用户ID") @RequestParam(required = false) Long userId) {
        boolean unique = userService.checkEmailUnique(email, userId);
        return Result.success(unique);
    }

    /**
     * 获取用户表单选项数据（部门、岗位、角色）
     */
    @ApiOperation("获取用户表单选项")
    @GetMapping("/formOptions")
    public Result<Map<String, Object>> getFormOptions() {
        Map<String, Object> result = new HashMap<>();

        // 获取所有部门（树形结构）
        com.admin.system.entity.SysDept deptQuery = new com.admin.system.entity.SysDept();
        result.put("depts", deptService.buildDeptTree(deptService.selectDeptList(deptQuery)));

        // 获取所有岗位
        com.admin.system.entity.SysPost postQuery = new com.admin.system.entity.SysPost();
        result.put("posts", postService.selectPostList(postQuery));

        // 获取所有角色
        result.put("roles", roleService.selectRoleList());

        return Result.success(result);
    }

    /**
     * 导出用户数据
     */
    @ApiOperation("导出用户数据")
    @PreAuthorize("@ss.hasPermi('system:user:export')")
    @PostMapping("/export")
    public void export(HttpServletResponse response,
                       @ApiParam("用户名") @RequestParam(required = false) String username,
                       @ApiParam("手机号") @RequestParam(required = false) String phone,
                       @ApiParam("状态") @RequestParam(required = false) String status) throws IOException {
        // 查询用户列表（不分页）
        List<UserVO> list = userService.selectUserList(username, phone, status);

        // 定义导出的表头和字段
        String[] headers = {"用户ID", "用户名", "昵称", "部门", "手机号", "邮箱", "状态", "创建时间"};
        String[] fields = {"userId", "username", "nickname", "deptName", "phone", "email", "status", "createTime"};

        ExcelUtil.exportExcel(response, "用户数据", headers, fields, list);
    }

    /**
     * 下载用户导入模板
     */
    @ApiOperation("下载用户导入模板")
    @GetMapping("/importTemplate")
    public void importTemplate(HttpServletResponse response) throws IOException {
        String[] headers = {"用户名", "昵称", "部门ID", "手机号", "邮箱", "性别(0男1女)", "密码"};
        ExcelUtil.exportTemplate(response, "用户导入模板", headers);
    }

    /**
     * 导入用户数据
     */
    @ApiOperation("导入用户数据")
    @PreAuthorize("@ss.hasPermi('system:user:import')")
    @PostMapping("/import")
    public Result<Map<String, Object>> importData(
            @ApiParam("Excel文件") @RequestParam("file") MultipartFile file,
            @ApiParam("是否更新已存在用户") @RequestParam(defaultValue = "false") boolean updateSupport) {
        Map<String, Object> result = userService.importUsers(file, updateSupport);
        return Result.success("导入完成", result);
    }

}
