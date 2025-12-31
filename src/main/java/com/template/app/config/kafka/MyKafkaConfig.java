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

@Configuration
public class MyKafkaConfig {

    @Bean
    public DefaultErrorHandler staticConsumerErrorHandler() {
        FixedBackOff backOff = new FixedBackOff(1000L, 3);

        DefaultErrorHandler handler =
            new DefaultErrorHandler(backOff);

        handler.addNotRetryableExceptions(
            SerializationException.class,
            DeserializationException.class,
            InvalidFormatException.class
        );

        return handler;
    }

    @Bean
    public MessageHandlerMethodFactory kafkaHandlerMethodFactory() {
        DefaultMessageHandlerMethodFactory factory =
            new DefaultMessageHandlerMethodFactory();

        factory.setMessageConverter(
            new MappingJackson2MessageConverter()
        );
        return factory;
    }
}
