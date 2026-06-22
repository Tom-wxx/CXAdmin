package com.admin.system.monitor;

import java.io.Serializable;

/**
 * 系统信息
 *
 * @author Admin
 */
public class SysInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 操作系统名称
     */
    private String osName;

    /**
     * 操作系统架构
     */
    private String osArch;

    /**
     * 服务器IP
     */
    private String serverIp;

    /**
     * 项目路径
     */
    private String projectPath;

    public String getOsName() {
        return osName;
    }

    public void setOsName(String osName) {
        this.osName = osName;
    }

    public String getOsArch() {
        return osArch;
    }

    public void setOsArch(String osArch) {
        this.osArch = osArch;
    }

    public String getServerIp() {
        return serverIp;
    }

    public void setServerIp(String serverIp) {
        this.serverIp = serverIp;
    }

    public String getProjectPath() {
        return projectPath;
    }

    public void setProjectPath(String projectPath) {
        this.projectPath = projectPath;
    }
}
