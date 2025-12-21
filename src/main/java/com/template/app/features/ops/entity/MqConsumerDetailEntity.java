package com.template.app.features.ops.entity;

import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.annotation.TableId;
import com.template.app.base.BaseEntity;

import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
@TableName("mq_consumer_details")
public class MqConsumerDetailEntity extends BaseEntity { 
    /**
     * PK also FK for ServiceEndpointConfigEntity
     */
    @TableId(value="service_endpoint_config_id", type=IdType.ASSIGN_UUID)
    private UUID configId;

    /**
     * Source name:
     * Topic name or queue name
     */
    @TableField("source_name")
    private String sourceName;

    /**
     * Group consumer
     */
    @TableField("group_id")
    private String groupId;

    /**
     * Parallel threads
     */
    @TableField("concurrency")
    private Integer concurrency;

    /**
     * Function in bean to get message
     */
    @TableField("target_bean")
    private String targetBean;

    /**
     * Function name to handle message
     */
    @TableField("target_method")
    private String targetMethod;
}
