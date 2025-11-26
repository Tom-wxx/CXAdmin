package com.admin.system.monitor;

import java.io.Serializable;

/**
 * 内存信息
 *
 * @author Admin
 */
public class MemoryInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总内存(GB)
     */
    private double total;

    /**
     * 已使用内存(GB)
     */
    private double used;

    /**
     * 空闲内存(GB)
     */
    private double free;

    /**
     * 使用率
     */
    private double usage;

    public double getTotal() {
        return total;
    }

    public void setTotal(double total) {
        this.total = total;
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
}
