package com.template.app.features.action.entity;

import java.io.Serializable;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("action_mappings")
public class ActionMappingEntity implements Serializable {
    
    @TableField("rule_id")
    private UUID ruleId;

    @TableField("error_id")
    private UUID errorId;
}
