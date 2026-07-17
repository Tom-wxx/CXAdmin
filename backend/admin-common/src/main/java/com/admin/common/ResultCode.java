package com.admin.common;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 响应码枚举
 *
 * @author Admin
 */
@Getter
@AllArgsConstructor
public enum ResultCode {

    SUCCESS(200, "操作成功"),

    FAIL(500, "操作失败"),

    PARAM_ERROR(400, "参数错误"),

    UNAUTHORIZED(401, "未认证"),

    FORBIDDEN(403, "未授权"),

    NOT_FOUND(404, "资源不存在"),

    USERNAME_OR_PASSWORD_ERROR(4001, "用户名或密码错误"),

    USER_EXIST(4002, "用户已存在"),

    USER_NOT_EXIST(4003, "用户不存在"),

    USER_DISABLED(4004, "用户已被禁用"),

    CAPTCHA_ERROR(4005, "验证码错误"),

    TOKEN_INVALID(4011, "Token无效"),

    TOKEN_EXPIRED(4012, "Token已过期"),

    DATA_EXIST(4101, "数据已存在"),

    DATA_NOT_EXIST(4102, "数据不存在");

    private final Integer code;

    private final String message;

}
