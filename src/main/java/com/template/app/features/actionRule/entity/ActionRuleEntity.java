package com.template.app.features.actionRule.entity;

import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.template.app.base.BaseEntity;
import com.template.app.features.actionRule.enums.ActionRuleStatusEnum;
import com.template.app.features.actionRule.enums.RuleActionTypeEnum;
import com.template.app.features.actionRule.enums.RuleTargetTypeEnum;
import com.template.app.features.actionRule.vo.ActionConfig;
import com.template.app.features.error.enums.ErrorCategoryEnum;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName(value = "action_rules")
public class ActionRuleEntity extends BaseEntity {
    
    @TableId(value = "rule_id", type = IdType.ASSIGN_UUID)
    private UUID ruleId;

    @TableField("rule_name")
    private String ruleName;

    @TableField("target_type")
    private RuleTargetTypeEnum targetType;

    @TableField("target_id")
    private UUID targetId;

    @TableField("match_category")
    private ErrorCategoryEnum matchCategory;

    @TableField("match_error_code")
    private Integer matchErrorCode;

    @TableField("action_type")
    private RuleActionTypeEnum actionType;

    @TableField(value = "action_config", typeHandler = JacksonTypeHandler.class)
    private ActionConfig actionConfig;

    @TableField("priority")
    private Integer priority;

    @TableField("status")
    private ActionRuleStatusEnum status;
}
