package com.admin.system.monitor;

import java.io.Serializable;
import java.util.List;

/**
 * 服务器信息
 *
 * @author Admin
 */
public class ServerInfo implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * CPU信息
     */
    private CpuInfo cpu;

    /**
     * 内存信息
     */
    private MemoryInfo memory;

    /**
     * JVM信息
     */
    private JvmInfo jvm;

    /**
     * 系统信息
     */
    private SysInfo sys;

    /**
     * 磁盘信息
     */
    private List<DiskInfo> disks;

    public CpuInfo getCpu() {
        return cpu;
    }

    public void setCpu(CpuInfo cpu) {
        this.cpu = cpu;
    }

    public MemoryInfo getMemory() {
        return memory;
    }

    public void setMemory(MemoryInfo memory) {
        this.memory = memory;
    }

    public JvmInfo getJvm() {
        return jvm;
    }

    public void setJvm(JvmInfo jvm) {
        this.jvm = jvm;
    }

    public SysInfo getSys() {
        return sys;
    }

    public void setSys(SysInfo sys) {
        this.sys = sys;
    }

    public List<DiskInfo> getDisks() {
        return disks;
    }

    public void setDisks(List<DiskInfo> disks) {
        this.disks = disks;
    }
}
