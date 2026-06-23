package com.admin.system.sso.service.impl;

import com.admin.system.sso.entity.SysSsoLoginLog;
import com.admin.system.sso.mapper.SysSsoLoginLogMapper;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Component;

/**
 * SSO 审计日志异步写入器
 *
 * <p>独立成 Bean 是为了让 {@code @Async} 代理生效（同类自调用不会走代理）。
 * 请求相关的元数据（IP / User-Agent）必须在请求线程内由 {@code SsoAuditLogServiceImpl}
 * 采集完毕后再调用本写入器，本类只负责落库。</p>
 *
 * @author Admin
 */
@Slf4j
@Component
@RequiredArgsConstructor
public class SsoAuditLogWriter {

    private final SysSsoLoginLogMapper mapper;

    @Async("taskExecutor")
    public void persist(SysSsoLoginLog l) {
        try {
            mapper.insert(l);
        } catch (Exception e) {
            // 审计日志失败永远不能影响主流程
            log.warn("Record SSO audit log failed: {}", e.getMessage());
        }
    }
}
