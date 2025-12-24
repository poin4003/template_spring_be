package com.template.app.features.ops.entity;

import java.util.Map;
import java.util.UUID;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableName;
import com.baomidou.mybatisplus.extension.handlers.JacksonTypeHandler;
import com.baomidou.mybatisplus.annotation.TableId;
import com.template.app.base.BaseEntity;
import com.template.app.features.ops.enums.MqAckStrategyEnum;

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
    private UUID endpointConfigId;

    /**
     * Source name:
     * Topic / Queue / Stream / Channel
     */
    @TableField("source_name")
    private String sourceName;

    /**
     * Group consumer:
     * GroupId / ConsumerTag / Queue group
     */
    @TableField("consumer_group")
    private String consumerGroup;

    /**
     * Parallel threads:
     * concurrency / concurrent consumers
     */
    @TableField("parallelism")
    private Integer parallelism;

    /**
     * Handler identifier (semantic key, resolved by registry)
     */
    @TableField("handler_key")
    private String handlerKey;

    /**
     * Method name
     */
    @TableField("handler_method")
    private String handlerMethod;

    /**
     * Mq ack stategy
     * AUTO / MANUAL
     */
    @TableField("ack_strategy")
    private MqAckStrategyEnum ackStrategy;

    /**
     * Retry mode
     * ENABLE / DISABLE retry
     */
    @TableField("retry_enabled")
    private Boolean retryEnabled;

    /**
     * Mq config
     * Transport-specific override config (optional)
     */
    @TableField(typeHandler = JacksonTypeHandler.class)
    private Map<String, Object> transportConfig;
}
