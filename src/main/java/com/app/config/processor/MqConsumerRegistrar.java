package com.app.config.processor;

import java.util.Objects;

import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.beans.factory.config.BeanFactoryPostProcessor;
import org.springframework.beans.factory.config.ConfigurableListableBeanFactory;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.lang.NonNull;
import org.springframework.stereotype.Component;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import com.app.core.annotation.RegisterMqConsumer;
import com.app.core.constant.AppConstants;
import com.app.core.mq.consumer.MqConsumerMeta;
import com.app.core.mq.consumer.MqConsumerRegistry;

@Component
public class MqConsumerRegistrar implements BeanFactoryPostProcessor {

    @Override
    public void postProcessBeanFactory(
        @NonNull ConfigurableListableBeanFactory factory
    ) {

        MqConsumerRegistry registry =
            Objects.requireNonNull(
                factory.getBean(MqConsumerRegistry.class),
                "MqConsumerRegistry must not be null"
            );

        ClassPathScanningCandidateComponentProvider scanner =
            new ClassPathScanningCandidateComponentProvider(false);

        scanner.addIncludeFilter(
            new AnnotationTypeFilter(RegisterMqConsumer.class)
        );

        for (BeanDefinition bd :
            scanner.findCandidateComponents(AppConstants.BASE_PACKAGE)) {

            try {
                String className =
                    Objects.requireNonNull(
                        bd.getBeanClassName(),
                        "Bean class name must not be null"
                    );

                Class<?> clazz = Class.forName(className);

                RegisterMqConsumer ann =
                    Objects.requireNonNull(
                        clazz.getAnnotation(RegisterMqConsumer.class),
                        "RegisterMqConsumer annotation must not be null"
                    );

                String simpleName = clazz.getSimpleName();
                String beanName = Character.toLowerCase(simpleName.charAt(0)) + simpleName.substring(1);

                String listenerId =
                    Objects.requireNonNull(
                        beanName,
                        "ListenerId must not be null"
                    );

                String topic =
                    Objects.requireNonNull(
                        ann.topic(),
                        "MQ topic must not be null"
                    );

                String group =
                    Objects.requireNonNull(
                        ann.group(),
                        "Consumer group must not be null"
                    );

                MqConsumerMeta meta =
                    Objects.requireNonNull(
                        MqConsumerMeta.builder()
                            .listenerId(listenerId)
                            .topic(topic)
                            .consumerGroup(group)
                            .dlq(ann.dlq())
                            .consumerClass(clazz)
                            .build(),
                        "MqConsumerMeta must not be null"
                    );

                registry.register(meta);

            } catch (ClassNotFoundException e) {
                throw new IllegalStateException(
                    "Failed to load MQ consumer class", e
                );
            }
        }
    }
}
