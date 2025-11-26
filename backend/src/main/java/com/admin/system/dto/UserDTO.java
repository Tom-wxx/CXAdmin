package com.admin.system.dto;

import lombok.Data;
import javax.validation.constraints.Email;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 用户DTO
 *
 * @author Admin
 */
@Data
public class UserDTO {

    /**
     * 用户ID
     */
    private Long userId;

    /**
     * 部门ID
     */
    private Long deptId;

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    @Size(min = 2, max = 30, message = "用户名长度在2-30之间")
    private String username;

    /**
     * 昵称
     */
    @NotBlank(message = "昵称不能为空")
    @Size(max = 30, message = "昵称长度不能超过30")
    private String nickname;

    /**
     * 密码
     */
    @Size(min = 6, max = 20, message = "密码长度在6-20之间")
    private String password;

    /**
     * 邮箱
     */
    @Email(message = "邮箱格式不正确")
    private String email;

    /**
     * 手机号
     */
    @Size(max = 11, message = "手机号长度不能超过11")
    private String phone;

    /**
     * 性别（0男 1女 2未知）
     */
    private String gender;

    /**
     * 状态（0正常 1停用）
     */
    private String status;

    /**
     * 角色ID数组
     */
    private Long[] roleIds;

    /**
     * 岗位ID数组
     */
    private Long[] postIds;

    /**
     * 备注
     */
    private String remark;
}
