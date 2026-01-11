package com.template.app.features.ops.service.impl;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

import org.apache.kafka.common.TopicPartition;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.listener.ConsumerRecordRecoverer;
import org.springframework.kafka.listener.DeadLetterPublishingRecoverer;
import org.springframework.kafka.listener.DefaultErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;
import org.springframework.messaging.handler.annotation.support.MessageHandlerMethodFactory;
import org.springframework.stereotype.Service;
import org.springframework.util.backoff.BackOff;
import org.springframework.util.backoff.ExponentialBackOff;
import org.springframework.util.backoff.FixedBackOff;

import com.template.app.config.settings.AppProperties;
import com.template.app.core.exception.category.AppSecurityException;
import com.template.app.core.exception.category.BusinessException;
import com.template.app.core.exception.category.UnknownException;
import com.template.app.core.exception.category.ValidationException;
import com.template.app.core.mq.handler.MessageHandler;
import com.template.app.core.mq.handler.MessageHandlerRegistry;
import com.template.app.core.mq.type.LogicalTypeIdMapper;
import com.template.app.core.mq.type.PayloadTypeRegistry;
import com.template.app.features.ops.service.DynamicMqListenerService;
import com.template.app.features.ops.service.schema.command.MqConsumerRegistrationCmd;

import jakarta.annotation.Nonnull;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DynamicMqlistenerServiceImpl implements DynamicMqListenerService {
    
    private final KafkaListenerEndpointRegistry kafkaRegistry;
    private final MessageHandlerRegistry handlerRegistry;
    private final KafkaProperties kafkaProperties;
    private final MessageHandlerMethodFactory handlerMethodFactory;
    private final PayloadTypeRegistry payloadTypeRegistry;
    private final KafkaTemplate<String, Object> kafkaTemplate; 
    private final AppProperties appProperties;

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
        endpoint.setMessageHandlerMethodFactory(
            Objects.requireNonNull(handlerMethodFactory, "handlerMethodfactory must be not null")
        );

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

    @Nonnull
    private ConcurrentKafkaListenerContainerFactory<String, Object>
    buildContainerFactory(MqConsumerRegistrationCmd cmd) {

        ConcurrentKafkaListenerContainerFactory<String, Object> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        factory.setConsumerFactory(
            Objects.requireNonNull(createConsumerFactory(), "ConsumerFactory must not be null")
        );

        factory.setCommonErrorHandler(
            Objects.requireNonNull(createErrorHandler(cmd), "ErrorHandler must not be null")
        );

        return factory;
    }

    private ConsumerFactory<String, Object> createConsumerFactory() {
        Map<String, Object> props = new HashMap<>(kafkaProperties.buildConsumerProperties());

        LogicalTypeIdMapper typeMapper = new LogicalTypeIdMapper(payloadTypeRegistry);

        JsonDeserializer<Object> jsonDeserializer = new JsonDeserializer<>();
        jsonDeserializer.addTrustedPackages("*");
        jsonDeserializer.setUseTypeHeaders(true);
        jsonDeserializer.setTypeMapper(typeMapper);

        ErrorHandlingDeserializer<Object> valueDeserializer =
            new ErrorHandlingDeserializer<>(jsonDeserializer);

        return new DefaultKafkaConsumerFactory<>(
            props,
            new StringDeserializer(),
            valueDeserializer
        );
    }

    private DefaultErrorHandler createErrorHandler(MqConsumerRegistrationCmd cmd) {
        Integer retryMaxAttempt = appProperties.getMq().getRetry().getDefaultMaxAttempts();
        Long retryBackoffMs = appProperties.getMq().getRetry().getDefaultBackoffMs();
        Double multiplier = appProperties.getMq().getRetry().getDefaultMultiplier();

        BackOff backOff = resolveBackoffPolicy(retryBackoffMs, retryMaxAttempt, multiplier); 
        ConsumerRecordRecoverer recoverer = createRecoverer(cmd);
        DefaultErrorHandler errorHandler = new DefaultErrorHandler(
            recoverer, Objects.requireNonNull(backOff, "Backoff must not be null")
        );

        errorHandler.addNotRetryableExceptions(
            BusinessException.class,
            ValidationException.class,
            AppSecurityException.class,
            UnknownException.class,
            IllegalArgumentException.class, 
            NullPointerException.class
        );
        
        return errorHandler;
    }

    private BackOff resolveBackoffPolicy(Long initialInterval, int maxAttempts, double multiplier) {
        if (multiplier <= 1.0) {
            return new FixedBackOff(initialInterval, maxAttempts);
        }

        ExponentialBackOff expBackOff = new ExponentialBackOff();
        expBackOff.setInitialInterval(initialInterval);
        expBackOff.setMultiplier(multiplier);
        
        long maxElapsedTime = calculateMaxElapsedTime(initialInterval, multiplier, maxAttempts);
        expBackOff.setMaxElapsedTime(maxElapsedTime);

        return expBackOff;
    }

    private ConsumerRecordRecoverer createRecoverer(MqConsumerRegistrationCmd cmd) {
        boolean dlqEnabled = Boolean.TRUE.equals(cmd.getEnableDlq());

        if (!dlqEnabled) {
            return (record, ex) -> log.error(
                "[Recoverer] Consumer [{}] exhauted retries. Message discarded. Error: {}",
                cmd.getEndpointId(), ex.getMessage()
            );
        }

        String dlqName = (cmd.getDlqName() != null && !cmd.getDlqName().isEmpty())
            ? cmd.getDlqName()
            : cmd.getSourceName() + ".DLQ";

        log.info("Consumer [{}] DLQ enabled -> Target: {}", cmd.getEndpointId());

        return new DeadLetterPublishingRecoverer(
            Objects.requireNonNull(kafkaTemplate, "kafkaTemplate must not be null"),
            (record, ex) -> new TopicPartition(dlqName, -1)
        );
    }

    private long calculateMaxElapsedTime(Long initialInterval, double multiplier, int maxAttempts) {
        long totalTime = 0;
        long currentInterval = initialInterval;

        for (int i = 0; i < maxAttempts; i++) {
            totalTime += currentInterval;
            currentInterval = (long) (currentInterval * multiplier);
        }

        return totalTime + 1000L;
    }
}
