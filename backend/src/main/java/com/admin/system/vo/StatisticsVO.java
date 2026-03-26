package com.admin.system.vo;

import lombok.Data;

import java.io.Serializable;

/**
 * 统计数据 VO
 *
 * @author Admin
 */
@Data
public class StatisticsVO implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日期
     */
    private String date;

    /**
     * 数值
     */
    private Long value;

    /**
     * 名称
     */
    private String name;

    /**
     * 百分比
     */
    private Double percentage;

    public StatisticsVO() {
    }

    public StatisticsVO(String dateOrName, Long value, boolean isDate) {
        if (isDate) {
            this.date = dateOrName;
        } else {
            this.name = dateOrName;
        }
        this.value = value;
    }

}
