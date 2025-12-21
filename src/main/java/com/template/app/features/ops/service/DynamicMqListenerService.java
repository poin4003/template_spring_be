package com.template.app.features.ops.service;

import com.template.app.features.ops.service.schema.command.MqConsumerRegistrationCmd;

public interface DynamicMqListenerService {
    
    void registerMqConsumer(MqConsumerRegistrationCmd cmd);

}
