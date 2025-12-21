package com.template.app.features.ops.service.schema.command;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class MqConsumerRegistrationCmd {

    private String listenerId;

    private String sourceName;

    private String groupId;

    private Integer concurrency;

    private String targetBean;

    private String targetMethod;

}
