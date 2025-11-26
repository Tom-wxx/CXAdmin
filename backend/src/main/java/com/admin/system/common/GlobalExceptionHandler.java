package com.admin.system.common;

import com.admin.system.common.exception.ServiceException;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.InternalAuthenticationServiceException;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.validation.BindException;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常处理器
 *
 * @author Admin
 */
@Slf4j
@RestControllerAdvice
public class GlobalExceptionHandler {

    /**
     * 认证失败异常
     */
    @ExceptionHandler(BadCredentialsException.class)
    public Result<Void> handleBadCredentialsException(BadCredentialsException e) {
        log.error("认证失败：{}", e.getMessage());
        return Result.fail(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
    }

    /**
     * 用户不存在异常
     */
    @ExceptionHandler(UsernameNotFoundException.class)
    public Result<Void> handleUsernameNotFoundException(UsernameNotFoundException e) {
        log.error("用户不存在：{}", e.getMessage());
        return Result.fail(ResultCode.UNAUTHORIZED.getCode(), "用户名或密码错误");
    }

    /**
     * 内部认证服务异常
     */
    @ExceptionHandler(InternalAuthenticationServiceException.class)
    public Result<Void> handleInternalAuthenticationServiceException(InternalAuthenticationServiceException e) {
        log.error("认证服务异常：{}", e.getMessage(), e);
        Throwable cause = e.getCause();
        if (cause instanceof RuntimeException) {
            return Result.fail(cause.getMessage());
        }
        return Result.fail("认证失败，请稍后重试");
    }

    /**
     * 权限校验异常
     */
    @ExceptionHandler(AccessDeniedException.class)
    public Result<Void> handleAccessDeniedException(AccessDeniedException e) {
        log.error("权限校验异常：{}", e.getMessage());
        return Result.fail(ResultCode.FORBIDDEN);
    }

    /**
     * 参数绑定异常
     */
    @ExceptionHandler(BindException.class)
    public Result<Void> handleBindException(BindException e) {
        log.error("参数绑定异常：{}", e.getMessage());
        String message = e.getAllErrors().get(0).getDefaultMessage();
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 参数校验异常
     */
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public Result<Void> handleMethodArgumentNotValidException(MethodArgumentNotValidException e) {
        log.error("参数校验异常：{}", e.getMessage());
        String message = e.getBindingResult().getFieldError().getDefaultMessage();
        return Result.fail(ResultCode.PARAM_ERROR.getCode(), message);
    }

    /**
     * 业务异常
     */
    @ExceptionHandler(ServiceException.class)
    public Result<Void> handleServiceException(ServiceException e) {
        log.error("业务异常：{}", e.getMessage());
        Integer code = e.getCode();
        if (code != null) {
            return Result.fail(code, e.getMessage());
        }
        return Result.fail(e.getMessage());
    }

    /**
     * 运行时异常
     */
    @ExceptionHandler(RuntimeException.class)
    public Result<Void> handleRuntimeException(RuntimeException e) {
        log.error("运行时异常：{}", e.getMessage());
        return Result.fail(e.getMessage());
    }

    /**
     * 系统异常
     */
    @ExceptionHandler(Exception.class)
    public Result<Void> handleException(Exception e) {
        log.error("系统异常：", e);
        return Result.fail("系统异常，请联系管理员");
    }

}
