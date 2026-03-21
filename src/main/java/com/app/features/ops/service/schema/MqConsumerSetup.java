package com.app.features.ops.service.schema;

import java.util.Map;
import java.util.UUID;

import lombok.Data;

@Data
public class MqConsumerSetup {

    private UUID endpointId;

    private String sourceName;

    private String consumerGroup;

    private Integer parallelism;

    private String handlerKey;

    private Map<String, Object> transportConfig;

    private Boolean enableDlq;

    private String dlqName;
}
