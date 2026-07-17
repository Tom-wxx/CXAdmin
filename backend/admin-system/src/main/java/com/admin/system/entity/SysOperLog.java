package com.admin.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 操作日志记录 sys_oper_log
 *
 * @author Admin
 */
@Data
@TableName("sys_oper_log")
public class SysOperLog implements Serializable {

    private static final long serialVersionUID = 1L;

    @TableId(value = "oper_id", type = IdType.AUTO)
    private Long operId;

    private String title;

    /**
     * 业务类型（0其它 1新增 2修改 3删除）
     */
    private Integer businessType;

    private String method;

    private String requestMethod;

    /**
     * 操作类别（0其它 1后台用户 2手机端用户）
     */
    private Integer operatorType;

    private String operName;

    private String deptName;

    private String operUrl;

    private String operIp;

    private String operLocation;

    private String operParam;

    private String jsonResult;

    /**
     * 操作状态（0正常 1异常）
     */
    private Integer status;

    private String errorMsg;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date operTime;

}
