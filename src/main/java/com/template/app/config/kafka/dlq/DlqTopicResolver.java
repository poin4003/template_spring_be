package com.template.app.config.kafka.dlq;

public interface DlqTopicResolver {

    String resolve(String topic, String group);

}
