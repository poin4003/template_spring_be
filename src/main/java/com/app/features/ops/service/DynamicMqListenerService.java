package com.app.features.ops.service;

import com.app.features.ops.service.schema.command.MqConsumerRegistrationCmd;

public interface DynamicMqListenerService {
     
    void registerMqConsumer(MqConsumerRegistrationCmd cmd);

}
