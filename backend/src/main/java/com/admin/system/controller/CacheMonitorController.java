package com.admin.system.controller;

import com.admin.system.common.Result;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.connection.DataType;
import org.springframework.data.redis.connection.RedisConnection;
import org.springframework.data.redis.core.Cursor;
import org.springframework.data.redis.core.RedisCallback;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.core.ScanOptions;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.nio.charset.StandardCharsets;
import java.util.*;
import java.util.concurrent.TimeUnit;

/**
 * 缓存监控控制器
 *
 * @author Admin
 */
@Api(tags = "缓存监控")
@RestController
@RequestMapping("/monitor/cache")
@RequiredArgsConstructor
public class CacheMonitorController {

    private final RedisTemplate<String, Object> redisTemplate;

    /**
     * 获取缓存列表（按键前缀分类）
     */
    @ApiOperation("获取缓存列表")
    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/list")
    public Result<List<Map<String, Object>>> getCacheList() {
        List<Map<String, Object>> cacheList = new ArrayList<>();

        // 获取所有键
        Set<String> keys = redisTemplate.keys("*");
        if (keys == null || keys.isEmpty()) {
            return Result.success(cacheList);
        }

        // 按前缀分组统计
        Map<String, Integer> prefixCountMap = new HashMap<>();
        for (String key : keys) {
            String prefix = getKeyPrefix(key);
            prefixCountMap.put(prefix, prefixCountMap.getOrDefault(prefix, 0) + 1);
        }

        // 转换为列表
        for (Map.Entry<String, Integer> entry : prefixCountMap.entrySet()) {
            Map<String, Object> cache = new HashMap<>();
            cache.put("cacheName", entry.getKey());
            cache.put("cacheKey", entry.getKey());
            cache.put("keyCount", entry.getValue());
            cache.put("remark", getCacheRemark(entry.getKey()));
            cacheList.add(cache);
        }

        // 按键数量降序排序
        cacheList.sort((a, b) -> {
            Integer countA = (Integer) a.get("keyCount");
            Integer countB = (Integer) b.get("keyCount");
            return countB.compareTo(countA);
        });

        return Result.success(cacheList);
    }

    /**
     * 获取键名列表
     */
    @ApiOperation("获取键名列表")
    @PreAuthorize("@ss.hasPermi('monitor:cache:list')")
    @GetMapping("/keys")
    public Result<List<Map<String, Object>>> getKeyList(
            @ApiParam("缓存名称（键前缀）") @RequestParam(required = false) String cacheName,
            @ApiParam("搜索关键字") @RequestParam(required = false) String keyword
    ) {
        List<Map<String, Object>> keyList = new ArrayList<>();

        // 构建匹配模式
        String pattern;
        if (keyword != null && !keyword.isEmpty()) {
            pattern = "*" + keyword + "*";
        } else if (cacheName != null && !cacheName.isEmpty()) {
            pattern = cacheName + "*";
        } else {
            pattern = "*";
        }

        // 使用 SCAN 命令获取键（避免 KEYS * 阻塞）
        Set<String> keys = redisTemplate.execute((RedisCallback<Set<String>>) connection -> {
            Set<String> keySet = new HashSet<>();
            Cursor<byte[]> cursor = connection.scan(
                    ScanOptions.scanOptions().match(pattern).count(1000).build()
            );

            while (cursor.hasNext()) {
                keySet.add(new String(cursor.next(), StandardCharsets.UTF_8));
            }
            cursor.close();
            return keySet;
        });

        if (keys == null || keys.isEmpty()) {
            return Result.success(keyList);
        }

        // 构建键列表信息
        for (String key : keys) {
            Map<String, Object> keyInfo = new HashMap<>();
            keyInfo.put("keyName", key);
            keyInfo.put("keyType", getKeyType(key));
            keyInfo.put("ttl", getKeyTTL(key));
            keyInfo.put("size", getKeySize(key));
            keyList.add(keyInfo);
        }

        // 按键名排序
        keyList.sort((a, b) -> {
            String keyA = (String) a.get("keyName");
            String keyB = (String) b.get("keyName");
            return keyA.compareTo(keyB);
        });

        return Result.success(keyList);
    }

    /**
     * 获取缓存内容
     */
    @ApiOperation("获取缓存内容")
    @PreAuthorize("@ss.hasPermi('monitor:cache:query')")
    @GetMapping("/value")
    public Result<Map<String, Object>> getCacheValue(
            @ApiParam("键名") @RequestParam String key
    ) {
        Map<String, Object> result = new HashMap<>();

        // 检查键是否存在
        Boolean exists = redisTemplate.hasKey(key);
        if (exists == null || !exists) {
            return Result.fail("键不存在：" + key);
        }

        // 获取键类型
        DataType dataType = redisTemplate.type(key);
        String type = dataType != null ? dataType.code() : "none";

        result.put("keyName", key);
        result.put("keyType", type);
        result.put("ttl", getKeyTTL(key));
        result.put("size", getKeySize(key));

        // 根据类型获取值
        Object value = null;
        try {
            switch (type.toLowerCase()) {
                case "string":
                    value = redisTemplate.opsForValue().get(key);
                    break;
                case "list":
                    value = redisTemplate.opsForList().range(key, 0, -1);
                    break;
                case "set":
                    value = redisTemplate.opsForSet().members(key);
                    break;
                case "zset":
                    value = redisTemplate.opsForZSet().rangeWithScores(key, 0, -1);
                    break;
                case "hash":
                    value = redisTemplate.opsForHash().entries(key);
                    break;
                default:
                    value = "不支持的数据类型：" + type;
            }
        } catch (Exception e) {
            value = "获取值失败：" + e.getMessage();
        }

        result.put("value", value);

        return Result.success(result);
    }

    /**
     * 删除缓存
     */
    @ApiOperation("删除缓存")
    @PreAuthorize("@ss.hasPermi('monitor:cache:remove')")
    @DeleteMapping("/{key}")
    public Result<Void> deleteCache(@PathVariable String key) {
        Boolean result = redisTemplate.delete(key);
        if (result != null && result) {
            return Result.success("删除成功");
        }
        return Result.fail("删除失败");
    }

    /**
     * 批量删除缓存
     */
    @ApiOperation("批量删除缓存")
    @PreAuthorize("@ss.hasPermi('monitor:cache:remove')")
    @DeleteMapping("/batch")
    public Result<Map<String, Object>> batchDeleteCache(@RequestBody List<String> keys) {
        if (keys == null || keys.isEmpty()) {
            return Result.fail("请选择要删除的缓存");
        }

        Long count = redisTemplate.delete(keys);
        Map<String, Object> result = new HashMap<>();
        result.put("total", keys.size());
        result.put("success", count != null ? count : 0);

        return Result.success("批量删除完成", result);
    }

    /**
     * 清空所有缓存
     */
    @ApiOperation("清空所有缓存")
    @PreAuthorize("@ss.hasPermi('monitor:cache:clear')")
    @DeleteMapping("/clear")
    public Result<Void> clearAllCache() {
        Set<String> keys = redisTemplate.keys("*");
        if (keys != null && !keys.isEmpty()) {
            redisTemplate.delete(keys);
        }
        return Result.success("清空成功");
    }

    /**
     * 获取键前缀
     */
    private String getKeyPrefix(String key) {
        if (key == null || key.isEmpty()) {
            return "未分类";
        }

        int index = key.indexOf(":");
        if (index > 0) {
            return key.substring(0, index);
        }

        // 如果没有冒号，返回第一个下划线之前的部分
        index = key.indexOf("_");
        if (index > 0) {
            return key.substring(0, index);
        }

        return "未分类";
    }

    /**
     * 获取缓存备注
     */
    private String getCacheRemark(String prefix) {
        Map<String, String> remarkMap = new HashMap<>();
        remarkMap.put("login_tokens", "登录令牌");
        remarkMap.put("captcha", "验证码");
        remarkMap.put("user", "用户信息");
        remarkMap.put("role", "角色信息");
        remarkMap.put("menu", "菜单信息");
        remarkMap.put("dict", "字典数据");
        remarkMap.put("config", "系统配置");
        remarkMap.put("未分类", "其他缓存");

        return remarkMap.getOrDefault(prefix, "缓存数据");
    }

    /**
     * 获取键类型
     */
    private String getKeyType(String key) {
        DataType dataType = redisTemplate.type(key);
        return dataType != null ? dataType.code() : "none";
    }

    /**
     * 获取键过期时间
     */
    private String getKeyTTL(String key) {
        Long ttl = redisTemplate.getExpire(key, TimeUnit.SECONDS);
        if (ttl == null || ttl < 0) {
            return "永久";
        }

        long days = ttl / 86400;
        long hours = (ttl % 86400) / 3600;
        long minutes = (ttl % 3600) / 60;
        long seconds = ttl % 60;

        if (days > 0) {
            return days + "天" + hours + "小时";
        } else if (hours > 0) {
            return hours + "小时" + minutes + "分钟";
        } else if (minutes > 0) {
            return minutes + "分钟" + seconds + "秒";
        } else {
            return seconds + "秒";
        }
    }

    /**
     * 获取键大小（估算）
     */
    private String getKeySize(String key) {
        try {
            // 获取键的类型
            DataType dataType = redisTemplate.type(key);
            if (dataType == null) {
                return "-";
            }

            Long size = 0L;
            switch (dataType) {
                case STRING:
                    // String类型：获取字符串长度
                    size = redisTemplate.execute((RedisCallback<Long>) connection ->
                        connection.strLen(key.getBytes(StandardCharsets.UTF_8))
                    );
                    break;
                case LIST:
                    // List类型：获取列表长度
                    size = redisTemplate.opsForList().size(key);
                    break;
                case SET:
                    // Set类型：获取集合大小
                    size = redisTemplate.opsForSet().size(key);
                    break;
                case ZSET:
                    // ZSet类型：获取有序集合大小
                    size = redisTemplate.opsForZSet().size(key);
                    break;
                case HASH:
                    // Hash类型：获取哈希表大小
                    size = redisTemplate.opsForHash().size(key);
                    break;
                default:
                    return "-";
            }

            if (size == null || size == 0) {
                return "-";
            }

            // 这里返回的是元素数量，不是字节数
            return size + " 项";
        } catch (Exception e) {
            return "-";
        }
    }
}
