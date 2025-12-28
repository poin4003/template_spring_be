package com.template.app.config.kafka;

import org.apache.kafka.common.errors.SerializationException;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.messaging.converter.MappingJackson2MessageConverter;
import org.springframework.messaging.handler.annotation.support.DefaultMessageHandlerMethodFactory;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.util.backoff.FixedBackOff;

import com.fasterxml.jackson.databind.exc.InvalidFormatException;
import io.jsonwebtoken.io.DeserializationException;

import lombok.extern.slf4j.Slf4j;

@Configuration 
@Slf4j
public class MyKafkaConfig {

    @Bean
    public DefaultErrorHandler commonErrorHandler() {
        FixedBackOff fixedBackOff = new FixedBackOff(1000L, 3);

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
            SerializationException.class,
            DeserializationException.class,
            IllegalStateException.class,
            InvalidFormatException.class
        );
 
        return errorHandler;
    }

    @Bean
    public MessageHandlerMethodFactory kafkaHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory = new DefaultMessageHandlerMethodFactory();

        factory.setMessageConverter(new MappingJackson2MessageConverter());
        return factory;
    }
}
