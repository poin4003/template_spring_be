package com.template.app.features.ops.service.schema.command;

import java.util.Map;
import java.util.UUID;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MqConsumerRegistrationCmd {

    private UUID endpointId;

    private String sourceName;

    private String consumerGroup;

    private Integer parallelism;

    private String handlerKey;

    private Map<String, Object> transportConfig;

    private Boolean enableDlq;

    private String dlqName;
}
