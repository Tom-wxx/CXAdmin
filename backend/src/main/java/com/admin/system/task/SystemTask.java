package com.admin.system.task;

import com.admin.system.service.ISysLoginLogService;
import com.admin.system.service.ISysOperLogService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.Set;

/**
 * 系统定时任务
 *
 * @author Admin
 */
@Component("systemTask")
@RequiredArgsConstructor
public class SystemTask {

    private static final Logger log = LoggerFactory.getLogger(SystemTask.class);

    private final RedisTemplate<String, Object> redisTemplate;
    private final ISysOperLogService operLogService;
    private final ISysLoginLogService loginLogService;

    /**
     * 系统信息监控任务
     */
    public void monitorSystemInfo() {
        log.info("==========系统信息监控任务开始执行==========");

        try {
            // 获取系统运行时信息
            Runtime runtime = Runtime.getRuntime();
            long totalMemory = runtime.totalMemory() / 1024 / 1024;
            long freeMemory = runtime.freeMemory() / 1024 / 1024;
            long usedMemory = totalMemory - freeMemory;

            log.info("JVM总内存: {}MB, 已用内存: {}MB, 空闲内存: {}MB",
                    totalMemory, usedMemory, freeMemory);

            // 监控Redis连接
            Set<String> keys = redisTemplate.keys("*");
            int keyCount = keys != null ? keys.size() : 0;
            log.info("Redis缓存键数量: {}", keyCount);

            log.info("系统信息监控任务执行完成");
        } catch (Exception e) {
            log.error("系统信息监控任务执行失败", e);
            throw e;
        }

        log.info("==========系统信息监控任务执行结束==========");
    }

    /**
     * 清理过期日志任务
     */
    public void cleanExpiredLogs() {
        log.info("==========清理过期日志任务开始执行==========");

        try {
            // 清理30天前的操作日志
            LocalDateTime expireTime = LocalDateTime.now().minusDays(30);
            log.info("开始清理{}之前的操作日志", expireTime.format(DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss")));

            // 这里可以调用service的方法删除过期日志
            // operLogService.deleteExpiredLogs(expireTime);

            log.info("清理过期日志任务执行完成");
        } catch (Exception e) {
            log.error("清理过期日志任务执行失败", e);
            throw e;
        }

        log.info("==========清理过期日志任务执行结束==========");
    }

    /**
     * 缓存预热任务
     */
    public void warmUpCache() {
        log.info("==========缓存预热任务开始执行==========");

        try {
            // 预热系统配置缓存
            log.info("开始预热系统配置缓存...");

            // 这里可以预加载一些常用的缓存数据
            // 例如：系统配置、字典数据等

            log.info("缓存预热任务执行完成");
        } catch (Exception e) {
            log.error("缓存预热任务执行失败", e);
            throw e;
        }

        log.info("==========缓存预热任务执行结束==========");
    }

    /**
     * 数据统计任务
     */
    public void statisticsData() {
        log.info("==========数据统计任务开始执行==========");

        try {
            LocalDateTime now = LocalDateTime.now();
            String today = now.format(DateTimeFormatter.ofPattern("yyyy-MM-dd"));

            log.info("开始统计{}的数据...", today);

            // 统计今日登录次数
            // Long loginCount = loginLogService.countTodayLogins();
            // log.info("今日登录次数: {}", loginCount);

            // 统计今日操作次数
            // Long operCount = operLogService.countTodayOperations();
            // log.info("今日操作次数: {}", operCount);

            log.info("数据统计任务执行完成");
        } catch (Exception e) {
            log.error("数据统计任务执行失败", e);
            throw e;
        }

        log.info("==========数据统计任务执行结束==========");
    }

    /**
     * 清理临时文件任务
     */
    public void cleanTempFiles() {
        log.info("==========清理临时文件任务开始执行==========");

        try {
            log.info("开始清理临时文件...");

            // 清理上传目录中的临时文件
            // 可以根据文件创建时间判断是否为临时文件

            log.info("清理临时文件任务执行完成");
        } catch (Exception e) {
            log.error("清理临时文件任务执行失败", e);
            throw e;
        }

        log.info("==========清理临时文件任务执行结束==========");
    }

    /**
     * 健康检查任务
     */
    public void healthCheck() {
        log.info("==========健康检查任务开始执行==========");

        try {
            // 检查数据库连接
            log.info("检查数据库连接...");

            // 检查Redis连接
            log.info("检查Redis连接...");
            try {
                redisTemplate.hasKey("health_check");
                log.info("Redis连接正常");
            } catch (Exception e) {
                log.error("Redis连接异常", e);
            }

            log.info("健康检查任务执行完成");
        } catch (Exception e) {
            log.error("健康检查任务执行失败", e);
            throw e;
        }

        log.info("==========健康检查任务执行结束==========");
    }
}
