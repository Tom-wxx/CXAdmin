package com.admin.system.monitor;

import java.io.Serializable;

/**
 * JVM信息
 *
 * @author Admin
 */
public class JvmInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * JVM总内存(MB)
     */
    private double total;

    /**
     * JVM最大内存(MB)
     */
    private double max;

    /**
     * JVM已使用内存(MB)
     */
    private double used;

    /**
     * JVM空闲内存(MB)
     */
    private double free;

    /**
     * JVM内存使用率
     */
    private double usage;

    /**
     * JDK版本
     */
    private String version;

    /**
     * JDK路径
     */
    private String home;

    /**
     * JVM运行时长(ms)
     */
    private long runTime;

    /**
     * JVM启动时间
     */
    private String startTime;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
    }

    public double getMax() {
        return max;
    }

    public void setMax(double max) {
        this.max = max;
    }

    public double getUsed() {
        return used;
    }

    public void setUsed(double used) {
        this.used = used;
    }

    public double getFree() {
        return free;
    }

    public void setFree(double free) {
        this.free = free;
    }

    public double getUsage() {
        return usage;
    }

    public void setUsage(double usage) {
        this.usage = usage;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getHome() {
        return home;
    }

    public void setHome(String home) {
        this.home = home;
    }

    public long getRunTime() {
        return runTime;
    }

    public void setRunTime(long runTime) {
        this.runTime = runTime;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }
}
