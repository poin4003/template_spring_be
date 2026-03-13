package com.app.features.ops.service;

import com.app.features.ops.service.schema.MqConsumerSetup;

public interface MqListenerService {
     
    void registerMqConsumer(MqConsumerSetup input);

}
