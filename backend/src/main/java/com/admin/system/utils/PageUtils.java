package com.admin.system.utils;

import com.admin.system.common.PageResult;
import com.baomidou.mybatisplus.core.metadata.IPage;

/**
 * 分页工具类
 *
 * @author Admin
 */
public class PageUtils {

    /**
     * 将 MyBatis Plus 分页结果转换为统一的 PageResult
     *
     * @param page MyBatis Plus 分页对象
     * @param <T>  实体类型
     * @return PageResult
     */
    public static <T> PageResult<T> buildPageResult(IPage<T> page) {
        return new PageResult<>(page.getRecords(), page.getTotal());
    }
}
