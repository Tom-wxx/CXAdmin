package com.admin.system.dto;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import lombok.Data;

/**
 * 分页查询参数
 *
 * @author Admin
 */
@Data
public class PageQuery {

    /**
     * 当前页码
     */
    private Integer pageNum = 1;

    /**
     * 每页显示记录数
     */
    private Integer pageSize = 10;

    /**
     * 排序列
     */
    private String orderByColumn;

    /**
     * 排序的方向desc或者asc
     */
    private String isAsc = "asc";

    /**
     * 构建 MyBatis Plus 分页对象
     *
     * @param <T> 实体类型
     * @return Page对象
     */
    public <T> Page<T> build() {
        Integer pageNum = this.pageNum != null ? this.pageNum : 1;
        Integer pageSize = this.pageSize != null ? this.pageSize : 10;

        // 防止分页参数过大
        if (pageSize > 100) {
            pageSize = 100;
        }

        return new Page<>(pageNum, pageSize);
    }

    /**
     * 获取当前页码（兼容方法）
     */
    public Long getCurrent() {
        return pageNum != null ? pageNum.longValue() : 1L;
    }

    /**
     * 获取每页大小（兼容方法）
     */
    public Long getSize() {
        return pageSize != null ? pageSize.longValue() : 10L;
    }
}
