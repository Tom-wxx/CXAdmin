package com.admin.system.common;

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

    /**
     * 成功
     */
    SUCCESS(200, "操作成功"),

    /**
     * 失败
     */
    FAIL(500, "操作失败"),

    /**
     * 参数错误
     */
    PARAM_ERROR(400, "参数错误"),

    /**
     * 未认证
     */
    UNAUTHORIZED(401, "未认证"),

    /**
     * 未授权
     */
    FORBIDDEN(403, "未授权"),

    /**
     * 资源不存在
     */
    NOT_FOUND(404, "资源不存在"),

    /**
     * 用户名或密码错误
     */
    USERNAME_OR_PASSWORD_ERROR(4001, "用户名或密码错误"),

    /**
     * 用户已存在
     */
    USER_EXIST(4002, "用户已存在"),

    /**
     * 用户不存在
     */
    USER_NOT_EXIST(4003, "用户不存在"),

    /**
     * 用户已被禁用
     */
    USER_DISABLED(4004, "用户已被禁用"),

    /**
     * 验证码错误
     */
    CAPTCHA_ERROR(4005, "验证码错误"),

    /**
     * Token无效
     */
    TOKEN_INVALID(4011, "Token无效"),

    /**
     * Token已过期
     */
    TOKEN_EXPIRED(4012, "Token已过期"),

    /**
     * 数据已存在
     */
    DATA_EXIST(4101, "数据已存在"),

    /**
     * 数据不存在
     */
    DATA_NOT_EXIST(4102, "数据不存在");

    /**
     * 响应码
     */
    private final Integer code;

    /**
     * 响应消息
     */
    private final String message;

}
