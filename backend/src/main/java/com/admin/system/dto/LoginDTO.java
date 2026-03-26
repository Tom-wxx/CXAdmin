package com.admin.system.dto;

import lombok.Data;

import javax.validation.constraints.NotBlank;

/**
 * 登录对象
 *
 * @author Admin
 */
@Data
public class LoginDTO {

    /**
     * 用户名
     */
    @NotBlank(message = "用户名不能为空")
    private String username;

    /**
     * 密码
     */
    @NotBlank(message = "密码不能为空")
    private String password;

    /**
     * 验证码
     */
    @NotBlank(message = "验证码不能为空")
    private String code;

    /**
     * 唯一标识
     */
    @NotBlank(message = "验证码标识不能为空")
    private String uuid;

}
