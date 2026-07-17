package com.admin.system.entity;

import com.admin.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.fasterxml.jackson.annotation.JsonFormat;
import lombok.Data;
import lombok.EqualsAndHashCode;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
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

    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    @NotBlank(message = "通知标题不能为空")
    @Size(max = 200, message = "通知标题长度不能超过200个字符")
    private String title;

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

    @NotNull(message = "接收用户ID不能为空")
    private Long userId;

    private Long senderId;

    private String senderName;

    @Size(max = 500, message = "关联链接长度不能超过500个字符")
    private String linkUrl;

    private String linkType;

    @JsonFormat(pattern = "yyyy-MM-dd HH:mm:ss")
    private Date readTime;

}
