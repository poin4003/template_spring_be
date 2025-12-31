package com.template.app.config.kafka;

import java.util.Objects;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.util.backoff.FixedBackOff;

import com.template.app.core.messaging.dlq.DlqMetadata;
import com.template.app.core.messaging.dlq.DlqPublisher;

@Configuration
public class KafkaErrorHandlerConfig {

    @Bean
    public DefaultErrorHandler dynamicConsumerErrorHandler(DlqPublisher dlqPublisher) {
        return new DefaultErrorHandler(
            (record, ex) -> {
                if (record == null) {
                    return;
                }

                Object payload = record.value();
                if (payload == null) {
                    return;
                }

                dlqPublisher.publish(
                    payload,
                    Objects.requireNonNull(
                        record.headers(), 
                        "Dlq headers must not be null"
                    ),
                    Objects.requireNonNull(
                        DlqMetadata.builder()
                            .originalTopic(Objects.requireNonNull(record.topic(), "topic"))
                            .consumerGroup("dynamic")
                            .exception(Objects.requireNonNull(ex, "exception"))
                            .build(),
                        "DlqMetadata must not be null"
                    )
                );         
            },
            new FixedBackOff(0L, 0)
        );
    }
}
