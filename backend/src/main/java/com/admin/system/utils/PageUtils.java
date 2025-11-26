package com.admin.system.utils;

import com.admin.system.common.PageResult;
import com.admin.system.dto.PageQuery;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;

/**
 * 分页工具类
 *
 * @author Admin
 */
public class PageUtils {

    /**
     * 根据 PageQuery 创建 MyBatis Plus 分页对象
     *
     * @param pageQuery 分页查询参数
     * @param <T>       实体类型
     * @return Page对象
     */
    public static <T> Page<T> buildPage(PageQuery pageQuery) {
        if (pageQuery == null) {
            return new Page<>(1, 10);
        }

        Integer pageNum = pageQuery.getPageNum() != null ? pageQuery.getPageNum() : 1;
        Integer pageSize = pageQuery.getPageSize() != null ? pageQuery.getPageSize() : 10;

        // 防止分页参数过大
        if (pageSize > 100) {
            pageSize = 100;
        }

        return new Page<>(pageNum, pageSize);
    }

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

    /**
     * 一步到位：根据 PageQuery 创建分页对象并转换结果
     * 这是最常用的方法
     *
     * @param pageQuery 分页查询参数
     * @param page      MyBatis Plus 分页结果
     * @param <T>       实体类型
     * @return PageResult
     */
    public static <T> PageResult<T> getPageResult(PageQuery pageQuery, IPage<T> page) {
        return buildPageResult(page);
    }
}
