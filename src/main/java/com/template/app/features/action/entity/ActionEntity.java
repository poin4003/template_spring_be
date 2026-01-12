package com.template.app.features.action.entity;

import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.template.app.base.BaseEntity;
import com.template.app.features.action.enums.ActionStatusEnum;
import com.template.app.features.action.enums.ActionTypeEnum;
import com.template.app.features.action.enums.TargetTypeEnum;
import com.template.app.features.action.vo.BaseActionConfig;
import com.template.app.handler.JsonTypeHanlder;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "actions")
public class ActionEntity extends BaseEntity {
    
    @TableId(value = "action_id", type = IdType.ASSIGN_UUID)
    private UUID actionId;

    @TableField("action_name")
    private String actionName;

    @TableField("target_type")
    private TargetTypeEnum targetType;

    @TableField("target_id")
    private UUID targetId;

    @TableField("action_type")
    private ActionTypeEnum actionType;

    @TableField(value = "action_config", typeHandler = JsonTypeHanlder.class)
    private BaseActionConfig actionConfig;

    @TableField("priority")
    private Integer priority;

    @TableField("status")
    private ActionStatusEnum status;
}
