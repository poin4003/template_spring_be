package com.template.app.features.ops.entity;

import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.template.app.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("system_error_messages")
public class SystemErrorMessageEntity extends BaseEntity {
    
    @TableId(value = "message_id", type = IdType.ASSIGN_UUID)
    private UUID messageId;

    @TableField("error_defination_id")
    private UUID errorDefinationId; 

    @TableField("language_code")
    private String languageCode;

    @TableField("message_content")
    private String messageContent;
}
