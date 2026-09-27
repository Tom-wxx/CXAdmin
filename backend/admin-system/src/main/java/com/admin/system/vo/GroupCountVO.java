package com.admin.system.vo;

import lombok.Data;

/**
 * 分组计数结果（GROUP BY 聚合查询的一行：分组键 + 数量）
 *
 * @author Admin
 */
@Data
public class GroupCountVO {

    /**
     * 分组键（日期 yyyy-MM-dd、部门ID、角色ID、业务类型等，统一按字符串返回）
     */
    private String groupKey;

    /**
     * 数量
     */
    private Long total;
}
