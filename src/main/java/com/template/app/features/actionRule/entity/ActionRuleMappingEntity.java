package com.template.app.features.actionRule.entity;

import java.io.Serializable;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;

import lombok.Data;

@Data
@TableName("action_rule_mappings")
public class ActionRuleMappingEntity implements Serializable {
    
    @TableField("rule_id")
    private UUID ruleId;

    @TableField("error_id")
    private UUID errorId;
}
