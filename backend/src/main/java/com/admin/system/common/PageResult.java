package com.admin.system.common;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 分页结果
 *
 * @author Admin
 */
@Data
public class PageResult<T> implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 总记录数
     */
    private Long total;

    /**
     * 列表数据
     */
    private List<T> rows;

    /**
     * 消息状态码
     */
    private Integer code;

    /**
     * 消息内容
     */
    private String message;

    public PageResult() {
    }

    public PageResult(List<T> rows, Long total) {
        this.rows = rows;
        this.total = total;
    }

}
