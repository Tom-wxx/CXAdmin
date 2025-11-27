package com.admin.system.service.impl;

import com.admin.system.service.IServerMonitorService;
import com.alibaba.druid.pool.DruidDataSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;
import oshi.SystemInfo;
import oshi.hardware.CentralProcessor;
import oshi.hardware.GlobalMemory;
import oshi.hardware.HardwareAbstractionLayer;
import oshi.software.os.FileSystem;
import oshi.software.os.OSFileStore;
import oshi.software.os.OperatingSystem;
import oshi.util.Util;

import javax.sql.DataSource;
import java.lang.management.ManagementFactory;
import java.lang.management.RuntimeMXBean;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.*;

/**
 * 服务器监控服务实现
 *
 * @author Admin
 */
@Service
public class ServerMonitorServiceImpl implements IServerMonitorService {

    @Autowired
    private RedisTemplate<String, Object> redisTemplate;

    @Autowired
    private DataSource dataSource;

    private static final DecimalFormat df = new DecimalFormat("0.00");

    @Override
    public Map<String, Object> getServerInfo() {
        Map<String, Object> result = new HashMap<>();

        // 服务器信息
        result.put("server", getServerData());

        // Redis信息
        result.put("redis", getRedisInfo());

        // 数据库连接池信息
        result.put("database", getDatabaseInfo());

        return result;
    }

    /**
     * 获取服务器数据
     */
    private Map<String, Object> getServerData() {
        Map<String, Object> serverData = new HashMap<>();

        SystemInfo si = new SystemInfo();
        HardwareAbstractionLayer hal = si.getHardware();
        OperatingSystem os = si.getOperatingSystem();

        // CPU信息
        serverData.put("cpu", getCpuInfo(hal.getProcessor()));

        // 内存信息
        serverData.put("mem", getMemoryInfo(hal.getMemory()));

        // JVM信息
        serverData.put("jvm", getJvmInfo());

        // 系统信息
        serverData.put("sys", getSysInfo(os));

        // 磁盘信息
        serverData.put("diskList", getDiskInfo(os.getFileSystem()));

        return serverData;
    }

    /**
     * 获取CPU信息
     */
    private Map<String, Object> getCpuInfo(CentralProcessor processor) {
        Map<String, Object> cpuInfo = new HashMap<>();

        // 获取CPU使用率
        long[] prevTicks = processor.getSystemCpuLoadTicks();
        Util.sleep(1000);
        long[] ticks = processor.getSystemCpuLoadTicks();

        long nice = ticks[CentralProcessor.TickType.NICE.getIndex()] - prevTicks[CentralProcessor.TickType.NICE.getIndex()];
        long irq = ticks[CentralProcessor.TickType.IRQ.getIndex()] - prevTicks[CentralProcessor.TickType.IRQ.getIndex()];
        long softirq = ticks[CentralProcessor.TickType.SOFTIRQ.getIndex()] - prevTicks[CentralProcessor.TickType.SOFTIRQ.getIndex()];
        long steal = ticks[CentralProcessor.TickType.STEAL.getIndex()] - prevTicks[CentralProcessor.TickType.STEAL.getIndex()];
        long cSys = ticks[CentralProcessor.TickType.SYSTEM.getIndex()] - prevTicks[CentralProcessor.TickType.SYSTEM.getIndex()];
        long user = ticks[CentralProcessor.TickType.USER.getIndex()] - prevTicks[CentralProcessor.TickType.USER.getIndex()];
        long iowait = ticks[CentralProcessor.TickType.IOWAIT.getIndex()] - prevTicks[CentralProcessor.TickType.IOWAIT.getIndex()];
        long idle = ticks[CentralProcessor.TickType.IDLE.getIndex()] - prevTicks[CentralProcessor.TickType.IDLE.getIndex()];

        long totalCpu = user + nice + cSys + idle + iowait + irq + softirq + steal;

        cpuInfo.put("coreNum", processor.getLogicalProcessorCount());
        cpuInfo.put("sys", df.format(cSys * 100.0 / totalCpu));
        cpuInfo.put("user", df.format(user * 100.0 / totalCpu));
        cpuInfo.put("wait", df.format(iowait * 100.0 / totalCpu));
        cpuInfo.put("free", df.format(idle * 100.0 / totalCpu));
        cpuInfo.put("used", df.format((totalCpu - idle) * 100.0 / totalCpu));

        return cpuInfo;
    }

    /**
     * 获取内存信息
     */
    private Map<String, Object> getMemoryInfo(GlobalMemory memory) {
        Map<String, Object> memInfo = new HashMap<>();

        long totalMemory = memory.getTotal();
        long availableMemory = memory.getAvailable();
        long usedMemory = totalMemory - availableMemory;

        memInfo.put("total", df.format(totalMemory / 1024.0 / 1024.0 / 1024.0));
        memInfo.put("usedMemory", df.format(usedMemory / 1024.0 / 1024.0 / 1024.0));
        memInfo.put("free", df.format(availableMemory / 1024.0 / 1024.0 / 1024.0));
        memInfo.put("used", df.format(usedMemory * 100.0 / totalMemory));

        return memInfo;
    }

    /**
     * 获取JVM信息
     */
    private Map<String, Object> getJvmInfo() {
        Map<String, Object> jvmInfo = new HashMap<>();

        Runtime runtime = Runtime.getRuntime();
        long totalMemory = runtime.totalMemory();
        long freeMemory = runtime.freeMemory();
        long usedMemory = totalMemory - freeMemory;
        long maxMemory = runtime.maxMemory();

        jvmInfo.put("total", df.format(totalMemory / 1024.0 / 1024.0));
        jvmInfo.put("max", df.format(maxMemory / 1024.0 / 1024.0));
        jvmInfo.put("usedMemory", df.format(usedMemory / 1024.0 / 1024.0));
        jvmInfo.put("free", df.format(freeMemory / 1024.0 / 1024.0));
        jvmInfo.put("used", df.format(usedMemory * 100.0 / totalMemory));
        jvmInfo.put("version", System.getProperty("java.version"));
        jvmInfo.put("home", System.getProperty("java.home"));

        // JVM运行时间
        RuntimeMXBean runtimeMXBean = ManagementFactory.getRuntimeMXBean();
        long startTime = runtimeMXBean.getStartTime();
        long runTime = System.currentTimeMillis() - startTime;

        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        jvmInfo.put("startTime", sdf.format(new Date(startTime)));
        jvmInfo.put("runTime", formatRunTime(runTime));

        return jvmInfo;
    }

    /**
     * 获取系统信息
     */
    private Map<String, Object> getSysInfo(OperatingSystem os) {
        Map<String, Object> sysInfo = new HashMap<>();

        Properties props = System.getProperties();
        sysInfo.put("osName", props.getProperty("os.name"));
        sysInfo.put("osArch", props.getProperty("os.arch"));
        sysInfo.put("osVersion", props.getProperty("os.version"));
        sysInfo.put("computerName", os.getNetworkParams().getHostName());
        sysInfo.put("computerIp", os.getNetworkParams().getIpv4DefaultGateway());
        sysInfo.put("userDir", props.getProperty("user.dir"));

        return sysInfo;
    }

    /**
     * 获取磁盘信息
     */
    private List<Map<String, Object>> getDiskInfo(FileSystem fileSystem) {
        List<Map<String, Object>> diskList = new ArrayList<>();

        List<OSFileStore> fileStores = fileSystem.getFileStores();
        for (OSFileStore fs : fileStores) {
            long free = fs.getUsableSpace();
            long total = fs.getTotalSpace();
            long used = total - free;

            if (total == 0) {
                continue;
            }

            Map<String, Object> diskInfo = new HashMap<>();
            diskInfo.put("dirName", fs.getMount());
            diskInfo.put("sysTypeName", fs.getType());
            diskInfo.put("typeName", fs.getName());
            diskInfo.put("total", df.format(total / 1024.0 / 1024.0 / 1024.0));
            diskInfo.put("free", df.format(free / 1024.0 / 1024.0 / 1024.0));
            diskInfo.put("used", df.format(used * 100.0 / total));

            diskList.add(diskInfo);
        }

        return diskList;
    }

    /**
     * 获取Redis信息
     */
    private Map<String, Object> getRedisInfo() {
        Map<String, Object> redisInfo = new HashMap<>();

        try {
            RedisConnectionFactory connectionFactory = redisTemplate.getConnectionFactory();
            if (connectionFactory != null) {
                Properties info = connectionFactory.getConnection().info();

                redisInfo.put("version", info.getProperty("redis_version", "-"));
                redisInfo.put("uptime", info.getProperty("uptime_in_days", "0"));
                redisInfo.put("connectedClients", info.getProperty("connected_clients", "0"));
                redisInfo.put("usedMemoryHuman", info.getProperty("used_memory_human", "0M"));
                redisInfo.put("usedMemoryPeakHuman", info.getProperty("used_memory_peak_human", "0M"));
                redisInfo.put("memFragmentationRatio", info.getProperty("mem_fragmentation_ratio", "0"));
                redisInfo.put("totalCommandsProcessed", info.getProperty("total_commands_processed", "0"));

                // 计算内存使用率
                String maxMemory = info.getProperty("maxmemory", "0");
                String usedMemory = info.getProperty("used_memory", "0");
                long maxMem = Long.parseLong(maxMemory);
                long usedMem = Long.parseLong(usedMemory);
                if (maxMem > 0) {
                    redisInfo.put("memoryUsage", df.format(usedMem * 100.0 / maxMem));
                } else {
                    redisInfo.put("memoryUsage", "0");
                }

                // 获取key总数
                Long dbSize = redisTemplate.getConnectionFactory().getConnection().dbSize();
                redisInfo.put("dbSize", dbSize != null ? dbSize : 0);

                // 网络流量
                redisInfo.put("totalNetInputBytes", formatBytes(Long.parseLong(info.getProperty("total_net_input_bytes", "0"))));
                redisInfo.put("totalNetOutputBytes", formatBytes(Long.parseLong(info.getProperty("total_net_output_bytes", "0"))));
            }
        } catch (Exception e) {
            redisInfo.put("error", "获取Redis信息失败: " + e.getMessage());
        }

        return redisInfo;
    }

    /**
     * 获取数据库连接池信息
     */
    private Map<String, Object> getDatabaseInfo() {
        Map<String, Object> dbInfo = new HashMap<>();

        try {
            if (dataSource instanceof DruidDataSource) {
                DruidDataSource druidDataSource = (DruidDataSource) dataSource;

                dbInfo.put("poolingCount", druidDataSource.getPoolingCount());
                dbInfo.put("activeCount", druidDataSource.getActiveCount());
                dbInfo.put("maxActive", druidDataSource.getMaxActive());
                dbInfo.put("initialSize", druidDataSource.getInitialSize());
                dbInfo.put("minIdle", druidDataSource.getMinIdle());
                dbInfo.put("maxWait", druidDataSource.getMaxWait());
                dbInfo.put("connectCount", druidDataSource.getConnectCount());
                dbInfo.put("closeCount", druidDataSource.getCloseCount());
                dbInfo.put("waitThreadCount", druidDataSource.getWaitThreadCount());
            }
        } catch (Exception e) {
            dbInfo.put("error", "获取数据库连接池信息失败: " + e.getMessage());
        }

        return dbInfo;
    }

    /**
     * 格式化运行时间
     */
    private String formatRunTime(long ms) {
        long seconds = ms / 1000;
        long minutes = seconds / 60;
        long hours = minutes / 60;
        long days = hours / 24;

        if (days > 0) {
            return days + "天" + (hours % 24) + "小时" + (minutes % 60) + "分钟";
        } else if (hours > 0) {
            return hours + "小时" + (minutes % 60) + "分钟";
        } else if (minutes > 0) {
            return minutes + "分钟" + (seconds % 60) + "秒";
        } else {
            return seconds + "秒";
        }
    }

    /**
     * 格式化字节大小
     */
    private String formatBytes(long bytes) {
        if (bytes < 1024) {
            return bytes + "B";
        } else if (bytes < 1024 * 1024) {
            return df.format(bytes / 1024.0) + "KB";
        } else if (bytes < 1024 * 1024 * 1024) {
            return df.format(bytes / 1024.0 / 1024.0) + "MB";
        } else {
            return df.format(bytes / 1024.0 / 1024.0 / 1024.0) + "GB";
        }
    }
}
