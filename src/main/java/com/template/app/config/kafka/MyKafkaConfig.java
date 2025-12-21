package com.template.app.config.kafka;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.listener.ListenerExecutionFailedException;
import org.springframework.util.backoff.FixedBackOff;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.jsonwebtoken.io.DeserializationException;

import lombok.extern.slf4j.Slf4j;

@Configuration 
@Slf4j
public class MyKafkaConfig {

    @Bean
    public DefaultErrorHandler commonErrorHandler() {
        FixedBackOff fixedBackOff = new FixedBackOff(0L, 3);

        DefaultErrorHandler errorHandler = new DefaultErrorHandler((consumerRecord, exception) -> {
            try {
                if (consumerRecord == null) {
                    log.error("Kafka error occurred (no consumer record): {}", exception.getMessage(), exception);
                } else {
                    String topic = consumerRecord.topic();
                    long offset = consumerRecord.offset();
                    log.error("Failed to process record. Topic={}, Offset={}, Error={}", topic, offset, exception.getMessage(), exception);
                }
            } catch (Exception e) {
                log.error("Unexpected error while handling Kafka exception: {}", e.getMessage(), e);
            }
        }, fixedBackOff);

        errorHandler.addNotRetryableExceptions(
            InvalidFormatException.class,
            DeserializationException.class,
            ListenerExecutionFailedException.class
        );

        return errorHandler;
    }
}
