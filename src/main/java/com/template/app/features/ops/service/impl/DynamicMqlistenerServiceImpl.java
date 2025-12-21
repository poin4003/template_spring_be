package com.template.app.features.ops.service.impl;

import java.lang.reflect.Method;
import java.util.Objects;

import org.springframework.context.ApplicationContext;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.config.KafkaListenerEndpointRegistry;
import org.springframework.kafka.config.MethodKafkaListenerEndpoint;
import org.springframework.stereotype.Service;

import com.template.app.features.ops.service.DynamicMqListenerService;
import com.template.app.features.ops.service.schema.command.MqConsumerRegistrationCmd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@RequiredArgsConstructor
public class DynamicMqlistenerServiceImpl implements DynamicMqListenerService {
    
    private final ApplicationContext applicationContext;
    private final KafkaListenerEndpointRegistry kafkaRegistry;

    private final ConcurrentKafkaListenerContainerFactory<String, Object> kafkaContainerFactory;

    @Override
    public void registerMqConsumer(MqConsumerRegistrationCmd cmd) {
        if (cmd == null) return;

        log.info("--> [WORKER] Has begin to register to kafka consumer: {}", cmd.getListenerId());

        try {
            String listenerId = Objects.requireNonNull(cmd.getListenerId(), "ListenerID is required");
            String groupId = Objects.requireNonNull(cmd.getGroupId(), "GroupId is required");
            String sourceName = Objects.requireNonNull(cmd.getSourceName(), "Topic is required");
            String targetBean = Objects.requireNonNull(cmd.getTargetBean(), "TargetBean is required");
            String targetMethod = Objects.requireNonNull(cmd.getTargetMethod(), "TargetMethod is required");

            Object bean = applicationContext.getBean(targetBean);

            MethodKafkaListenerEndpoint<String, Object> endpoint = new MethodKafkaListenerEndpoint<>();

            endpoint.setId(listenerId);
            endpoint.setGroupId(groupId);
            endpoint.setTopics(sourceName);
            endpoint.setBean(bean);

            if (cmd.getConcurrency() != null) {
                endpoint.setConcurrency(Objects.requireNonNull(cmd.getConcurrency()));
            }

            endpoint.setBean(bean);

            Method method = Objects.requireNonNull(
                findTargetMethod(bean, targetMethod),
                "Method result required"
            );
            endpoint.setMethod(method);

            kafkaRegistry.registerListenerContainer(
                endpoint, 
                Objects.requireNonNull(kafkaContainerFactory, "ContainerFactory is required"),
                true
            );

            log.info("--> [SUCCESS] Consumer started: Topic='{}', Bean='{}'", cmd.getSourceName(), cmd.getTargetBean());

        } catch (Exception e) {
            log.error("--> [FAILED] error to register kafka listener id {}: {}", cmd.getListenerId(), e.getMessage());
            throw new RuntimeException(e);
        }
    }

    private Method findTargetMethod(Object bean, String methodName) {
        for (Method m : bean.getClass().getMethods()) {
            if (m.getName().equals(methodName)) {
                return m;
            }
        }
        throw new IllegalArgumentException(
            String.format("Not found method: '%s' in bean '%s'", methodName, bean.getClass().getName())
        );
    }
}
