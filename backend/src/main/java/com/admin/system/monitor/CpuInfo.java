package com.admin.system.monitor;

import java.io.Serializable;

/**
 * CPU信息
 *
 * @author Admin
 */
public class CpuInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 核心数
     */
    private int coreNum;

    /**
     * CPU系统使用率
     */
    private double sysUsage;

    /**
     * CPU用户使用率
     */
    private double userUsage;

    /**
     * CPU当前等待率
     */
    private double waitUsage;

    /**
     * CPU当前空闲率
     */
    private double freeUsage;

    public int getCoreNum() {
        return coreNum;
    }

    public void setCoreNum(int coreNum) {
        this.coreNum = coreNum;
    }

    public double getSysUsage() {
        return sysUsage;
    }

    public void setSysUsage(double sysUsage) {
        this.sysUsage = sysUsage;
    }

    public double getUserUsage() {
        return userUsage;
    }

    public void setUserUsage(double userUsage) {
        this.userUsage = userUsage;
    }

    public double getWaitUsage() {
        return waitUsage;
    }

    public void setWaitUsage(double waitUsage) {
        this.waitUsage = waitUsage;
    }

    public double getFreeUsage() {
        return freeUsage;
    }

    public void setFreeUsage(double freeUsage) {
        this.freeUsage = freeUsage;
    }
}
