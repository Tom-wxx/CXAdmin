package com.admin.system.entity;

import com.admin.system.common.BaseEntity;
import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import lombok.EqualsAndHashCode;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

/**
 * 消息模板对象 sys_message
 *
 * @author Admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message")
public class SysMessage extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 消息ID
     */
    @TableId(value = "message_id", type = IdType.AUTO)
    private Long messageId;

    /**
     * 消息名称
     */
    @NotBlank(message = "消息名称不能为空")
    @Size(max = 100, message = "消息名称长度不能超过100个字符")
    private String messageName;

    /**
     * 消息编码（唯一标识）
     */
    @NotBlank(message = "消息编码不能为空")
    @Size(max = 100, message = "消息编码长度不能超过100个字符")
    private String messageCode;

    /**
     * 消息类型（1邮件 2短信 3站内信 4微信）
     */
    @NotBlank(message = "消息类型不能为空")
    private String messageType;

    /**
     * 消息主题/标题
     */
    @Size(max = 200, message = "消息主题长度不能超过200个字符")
    private String subject;

    /**
     * 消息内容模板（支持变量占位符）
     */
    @NotBlank(message = "消息内容不能为空")
    private String content;

    /**
     * 可用变量说明（JSON格式）
     */
    @Size(max = 500, message = "变量说明长度不能超过500个字符")
    private String variables;

    /**
     * 状态（0正常 1停用）
     */
    private String status;
}
