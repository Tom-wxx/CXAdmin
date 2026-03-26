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
 * 通知模板实体 sys_notification_template
 *
 * @author Admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_notification_template")
public class SysNotificationTemplate extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 模板ID
     */
    @TableId(value = "id", type = IdType.AUTO)
    private Long id;

    /**
     * 模板编码
     */
    @NotBlank(message = "模板编码不能为空")
    @Size(max = 100, message = "模板编码长度不能超过100个字符")
    private String templateCode;

    /**
     * 模板名称
     */
    @NotBlank(message = "模板名称不能为空")
    @Size(max = 200, message = "模板名称长度不能超过200个字符")
    private String templateName;

    /**
     * 通知标题模板
     */
    @NotBlank(message = "标题模板不能为空")
    @Size(max = 200, message = "标题模板长度不能超过200个字符")
    private String title;

    /**
     * 通知内容模板
     */
    @NotBlank(message = "内容模板不能为空")
    private String content;

    /**
     * 通知类型
     */
    @NotBlank(message = "通知类型不能为空")
    private String type;

    /**
     * 优先级
     */
    private String priority;

    /**
     * 状态（0停用 1启用）
     */
    private String status;

}
