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
@TableName("cronjob_configs")
public class CronJobConfigEntity extends BaseEntity {

    @TableId(value = "service_endpoint_config_id", type = IdType.ASSIGN_UUID)
    private UUID endpointConfigId;

    @TableField("job_name")
    private String cronjobName;

    @TableField("cron_expression")
    private String cronExpression;

    @TableField("job_type")
    private String jobType;

    @TableField("lock_at_most_for")
    private String lockAtMostFor;

    @TableField("lock_at_least_for")
    private String lockAtLeastFor;
}
