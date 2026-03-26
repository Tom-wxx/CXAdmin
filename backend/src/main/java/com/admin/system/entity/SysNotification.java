package com.admin.system.entity;

import com.admin.system.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;
import javax.validation.constraints.Size;
import java.util.Date;

/**
 * 站内通知实体 sys_notification
 *
 * @author Admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notification")
public class SysNotification extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 通知ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 通知标题
     */
    @NotBlank(message = "通知标题不能为空")
    @Size(max = 200, message = "通知标题长度不能超过200个字符")
    private String title;

    /**
     * 通知内容
     */
    private String content;

    /**
     * 通知类型（system:系统通知, todo:待办提醒, approval:审批消息, announce:公告）
     */
    @NotBlank(message = "通知类型不能为空")
    private String type;

    /**
     * 优先级（normal:普通, important:重要, urgent:紧急）
     */
    private String priority;

    /**
     * 状态（unread:未读, read:已读）
     */
    private String status;

    /**
     * 接收用户ID
     */
    @NotNull(message = "接收用户ID不能为空")
    private Long userId;

    /**
     * 发送者ID
     */
    private Long senderId;

    /**
     * 发送者名称
     */
    private String senderName;

    /**
     * 关联链接
     */
    @Size(max = 500, message = "关联链接长度不能超过500个字符")
    private String linkUrl;

    /**
     * 链接类型
     */
    private String linkType;

    /**
     * 阅读时间
     */
    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

}
