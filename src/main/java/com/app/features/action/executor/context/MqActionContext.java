package com.app.features.action.executor.context;

import org.apache.kafka.clients.consumer.ConsumerRecord;

import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.experimental.SuperBuilder;

@Data
@SuperBuilder
@EqualsAndHashCode(callSuper = true)
public class MqActionContext extends BaseActionContext {
    
    private ConsumerRecord<?, ?> mqRecord;

    private String originalTopic;

    private String exceptionClass;

    private String exceptionMessage;
}
