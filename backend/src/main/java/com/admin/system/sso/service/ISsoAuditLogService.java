package com.admin.system.sso.service;

import com.admin.system.sso.entity.SysSsoLoginLog;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

import java.util.Date;

public interface ISsoAuditLogService {

    /** 记一条审计日志（异常吞掉，永不影响主流程）。 */
    void record(SysSsoLoginLog log);

    /** 便捷方法：authorize 成功 */
    void recordAuthorize(Long providerId, String providerCode, String mode, Long userId);

    /** 便捷方法：authorize 失败 */
    void recordAuthorizeFail(String providerCode, String errorMessage);

    /** 便捷方法：callback 成功 */
    void recordCallback(Long providerId, String providerCode, String mode, Long userId, String externalUserId);

    /** 便捷方法：callback 失败 */
    void recordCallbackFail(String providerCode, String mode, String errorMessage);

    /** 便捷方法：unbind */
    void recordUnbind(Long providerId, String providerCode, Long userId, String externalUserId, boolean success, String errorMessage);

    /** 分页查询（admin 端）。 */
    IPage<SysSsoLoginLog> listPage(Page<SysSsoLoginLog> page,
                                   Long providerId, String action, String status,
                                   Long userId, Date from, Date to);
}
