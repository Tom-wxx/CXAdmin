package com.admin.system.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;

import java.io.Serializable;
import java.util.Date;

/**
 * 消息发送日志对象 sys_message_log
 *
 * @author Admin
 */
@Data
@TableName("sys_message_log")
public class SysMessageLog implements Serializable {

    private static final long serialVersionUID = 1L;

    /**
     * 日志ID
     */
    @TableId(value = "log_id", type = IdType.AUTO)
    private Long logId;

    /**
     * 消息模板ID
     */
    private Long messageId;

    /**
     * 消息类型（1邮件 2短信 3站内信 4微信）
     */
    private String messageType;

    /**
     * 接收者（邮箱/手机号/用户ID）
     */
    private String receiver;

    /**
     * 消息主题
     */
    private String subject;

    /**
     * 实际发送内容
     */
    private String content;

    /**
     * 发送状态（0成功 1失败 2发送中）
     */
    private String sendStatus;

    /**
     * 发送时间
     */
    private Date sendTime;

    /**
     * 错误信息
     */
    private String errorMsg;

    /**
     * 重试次数
     */
    private Integer retryCount;

    /**
     * 发送人
     */
    private Long sendBy;

    /**
     * 创建时间
     */
    private Date createTime;
}
