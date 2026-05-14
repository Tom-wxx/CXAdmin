package com.admin.system.sso.service.impl;

import com.admin.system.sso.entity.SysSsoLoginLog;
import com.admin.system.sso.mapper.SysSsoLoginLogMapper;
import com.admin.system.sso.service.ISsoAuditLogService;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.web.context.request.RequestContextHolder;
import org.springframework.web.context.request.ServletRequestAttributes;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Slf4j
@Service
@RequiredArgsConstructor
public class SsoAuditLogServiceImpl implements ISsoAuditLogService {

    private final SysSsoLoginLogMapper mapper;

    @Override
    public void record(SysSsoLoginLog l) {
        try {
            if (l.getCreateTime() == null) l.setCreateTime(new Date());
            fillRequestMeta(l);
            mapper.insert(l);
        } catch (Exception e) {
            // 审计日志失败永远不能影响主流程
            log.warn("Record SSO audit log failed: {}", e.getMessage());
        }
    }

    private void fillRequestMeta(SysSsoLoginLog l) {
        try {
            ServletRequestAttributes attrs = (ServletRequestAttributes) RequestContextHolder.getRequestAttributes();
            if (attrs == null) return;
            HttpServletRequest req = attrs.getRequest();
            if (l.getIp() == null)        l.setIp(extractIp(req));
            if (l.getUserAgent() == null) l.setUserAgent(safe(req.getHeader("User-Agent"), 500));
        } catch (Exception ignored) {}
    }

    private String extractIp(HttpServletRequest req) {
        String[] headers = {"X-Forwarded-For", "X-Real-IP", "Proxy-Client-IP", "WL-Proxy-Client-IP"};
        for (String h : headers) {
            String v = req.getHeader(h);
            if (v != null && !v.isEmpty() && !"unknown".equalsIgnoreCase(v)) {
                int comma = v.indexOf(',');
                return safe(comma > 0 ? v.substring(0, comma).trim() : v, 64);
            }
        }
        return safe(req.getRemoteAddr(), 64);
    }

    private String safe(String s, int max) {
        if (s == null) return null;
        return s.length() > max ? s.substring(0, max) : s;
    }

    @Override
    public void recordAuthorize(Long providerId, String providerCode, String mode, Long userId) {
        SysSsoLoginLog l = new SysSsoLoginLog();
        l.setProviderId(providerId);
        l.setProviderCode(providerCode);
        l.setMode(mode);
        l.setUserId(userId);
        l.setAction(SysSsoLoginLog.ACTION_AUTHORIZE);
        l.setStatus(SysSsoLoginLog.STATUS_SUCCESS);
        record(l);
    }

    @Override
    public void recordAuthorizeFail(String providerCode, String errorMessage) {
        SysSsoLoginLog l = new SysSsoLoginLog();
        l.setProviderCode(providerCode);
        l.setAction(SysSsoLoginLog.ACTION_AUTHORIZE);
        l.setStatus(SysSsoLoginLog.STATUS_FAIL);
        l.setErrorMessage(safe(errorMessage, 500));
        record(l);
    }

    @Override
    public void recordCallback(Long providerId, String providerCode, String mode, Long userId, String externalUserId) {
        SysSsoLoginLog l = new SysSsoLoginLog();
        l.setProviderId(providerId);
        l.setProviderCode(providerCode);
        l.setMode(mode);
        l.setUserId(userId);
        l.setExternalUserId(externalUserId);
        l.setAction(SysSsoLoginLog.ACTION_CALLBACK);
        l.setStatus(SysSsoLoginLog.STATUS_SUCCESS);
        record(l);
    }

    @Override
    public void recordCallbackFail(String providerCode, String mode, String errorMessage) {
        SysSsoLoginLog l = new SysSsoLoginLog();
        l.setProviderCode(providerCode);
        l.setMode(mode);
        l.setAction(SysSsoLoginLog.ACTION_CALLBACK);
        l.setStatus(SysSsoLoginLog.STATUS_FAIL);
        l.setErrorMessage(safe(errorMessage, 500));
        record(l);
    }

    @Override
    public void recordUnbind(Long providerId, String providerCode, Long userId, String externalUserId, boolean success, String errorMessage) {
        SysSsoLoginLog l = new SysSsoLoginLog();
        l.setProviderId(providerId);
        l.setProviderCode(providerCode);
        l.setUserId(userId);
        l.setExternalUserId(externalUserId);
        l.setAction(SysSsoLoginLog.ACTION_UNBIND);
        l.setStatus(success ? SysSsoLoginLog.STATUS_SUCCESS : SysSsoLoginLog.STATUS_FAIL);
        if (!success) l.setErrorMessage(safe(errorMessage, 500));
        record(l);
    }

    @Override
    public IPage<SysSsoLoginLog> listPage(Page<SysSsoLoginLog> page,
                                          Long providerId, String action, String status,
                                          Long userId, Date from, Date to) {
        LambdaQueryWrapper<SysSsoLoginLog> qw = new LambdaQueryWrapper<>();
        if (providerId != null)                          qw.eq(SysSsoLoginLog::getProviderId, providerId);
        if (action != null && !action.isEmpty())         qw.eq(SysSsoLoginLog::getAction, action);
        if (status != null && !status.isEmpty())         qw.eq(SysSsoLoginLog::getStatus, status);
        if (userId != null)                              qw.eq(SysSsoLoginLog::getUserId, userId);
        if (from != null)                                qw.ge(SysSsoLoginLog::getCreateTime, from);
        if (to != null)                                  qw.le(SysSsoLoginLog::getCreateTime, to);
        qw.orderByDesc(SysSsoLoginLog::getId);
        return mapper.selectPage(page, qw);
    }
}
