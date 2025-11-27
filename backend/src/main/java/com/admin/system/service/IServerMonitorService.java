package com.admin.system.service;

import java.util.Map;

/**
 * 服务器监控服务接口
 *
 * @author Admin
 */
public interface IServerMonitorService {

    /**
     * 获取服务器监控信息
     *
     * @return 服务器监控信息
     */
    Map<String, Object> getServerInfo();
}
