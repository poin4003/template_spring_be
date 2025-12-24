package com.template.app.features.ops.service.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.stereotype.Service;

import com.template.app.core.messaging.MessageHandler;
import com.template.app.core.messaging.MessageHandlerRegistry;
import com.template.app.features.ops.service.DynamicMqListenerService;
import com.template.app.features.ops.service.schema.command.MqConsumerRegistrationCmd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DynamicMqlistenerServiceImpl implements DynamicMqListenerService {
    
    private final KafkaListenerEndpointRegistry kafkaRegistry;
    private final MessageHandlerRegistry handlerRegistry;
    private final KafkaProperties kafkaProperties;
    private final DefaultErrorHandler errorHandler;
    private final MessageHandlerMethodFactory handlerMethodFactory;

    @Override
    public void registerMqConsumer(MqConsumerRegistrationCmd cmd) {
        if (cmd == null) {
            log.warn("Skip registerMqConsumer: cmd is null");
            return;
        }

        Objects.requireNonNull(cmd.getEndpointId(), "endpointId is required");
        Objects.requireNonNull(cmd.getHandlerKey(), "handlerKey is required");
        Objects.requireNonNull(cmd.getSourceName(), "sourceName is required");
        Objects.requireNonNull(cmd.getConsumerGroup(), "consumerGroup is required");

        String listenerId = "dynamic-mq-" + cmd.getEndpointId();

        MessageHandler<?> handler = Objects.requireNonNull(
            handlerRegistry.get(cmd.getHandlerKey()),
            "MessageHandler not found for key: " + cmd.getHandlerKey()
        );

        Method handlerMethod = resolveHandlerMethod(handler);

        MethodKafkaListenerEndpoint<String, Object> endpoint =
            new MethodKafkaListenerEndpoint<>();

        endpoint.setId(listenerId);
        endpoint.setGroupId(cmd.getConsumerGroup());
        endpoint.setTopics(cmd.getSourceName());
        endpoint.setBean(handler);
        endpoint.setMethod(
            Objects.requireNonNull(handlerMethod, "handlerMethod must not be null")
        );
        endpoint.setMessageHandlerMethodFactory(handlerMethodFactory);

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            Objects.requireNonNull(
                buildContainerFactory(cmd),
                "KafkaListenerContainerFactory must not be null"
            );

        kafkaRegistry.registerListenerContainer(endpoint, factory, true);

        log.info(
            "Kafka consumer [{}] started | topic={} | handler={}",
            listenerId,
            cmd.getSourceName(),
            cmd.getHandlerKey()
        );
    }

    private Method resolveHandlerMethod(Object handler) {
        Objects.requireNonNull(handler, "handler must not be null");

        for (Method m : handler.getClass().getMethods()) {
            if ("handle".equals(m.getName()) && m.getParameterCount() == 1) {
                return m;
            }
        }

        throw new IllegalStateException(
            "MessageHandler must define method: handle(T payload)"
        );
    }

    private ConcurrentKafkaListenerContainerFactory<String, Object> buildContainerFactory(MqConsumerRegistrationCmd cmd) {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());

        if (cmd.getTransportConfig() != null) {
            props.putAll(cmd.getTransportConfig());
        }

        JsonDeserializer<Object> valueDeserializer = new JsonDeserializer<>();
        valueDeserializer.addTrustedPackages("*");
        valueDeserializer.setUseTypeHeaders(false);
        valueDeserializer.setRemoveTypeHeaders(false);

        ConsumerFactory<String, Object> consumerFactory =
            new DefaultKafkaConsumerFactory<>(
                props,
                new StringDeserializer(),
                valueDeserializer
            );

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(consumerFactory);
        factory.setCommonErrorHandler(errorHandler);

        Integer parallelism = cmd.getParallelism();
        if (parallelism != null) {
            factory.setConcurrency(parallelism);
        }

        return factory;
    }
}
