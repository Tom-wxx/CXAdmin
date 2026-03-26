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
 * 消息配置对象 sys_message_config
 *
 * @author Admin
 */
@Data
@EqualsAndHashCode(callSuper = true)
@TableName("sys_message_config")
public class SysMessageConfig extends BaseEntity {

    private static final long serialVersionUID = 1L;

    /**
     * 配置ID
     */
    @TableId(value = "config_id", type = IdType.AUTO)
    private Long configId;

    /**
     * 消息类型（1邮件 2短信 3站内信 4微信）
     */
    @NotBlank(message = "消息类型不能为空")
    private String messageType;

    /**
     * 配置名称
     */
    @NotBlank(message = "配置名称不能为空")
    @Size(max = 100, message = "配置名称长度不能超过100个字符")
    private String configName;

    /**
     * 配置数据（JSON格式）
     */
    @NotBlank(message = "配置数据不能为空")
    private String configData;

    /**
     * 是否默认配置（0否 1是）
     */
    private String isDefault;

    /**
     * 状态（0正常 1停用）
     */
    private String status;
}
