package com.template.app.features.action.processor;

import java.util.Objects;
import java.util.Set;

import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.beans.factory.config.BeanDefinition;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;

import org.springframework.util.ClassUtils;

import com.fasterxml.jackson.databind.jsontype.NamedType;
import com.fasterxml.jackson.databind.module.SimpleModule;
import com.template.app.core.constant.AppConstants;
import com.template.app.features.action.annotation.ActionConf;
import com.template.app.features.action.vo.BaseActionConfig;

import lombok.extern.slf4j.Slf4j;

@Slf4j
@Configuration
public class ActionTypeRegistrar {
    
    private static final String BASE_ACTION_PACKAGE = AppConstants.BASE_PACKAGE + "features.action.processor";

    @Bean
    public Jackson2ObjectMapperBuilderCustomizer registerActionSubtypes() {
        return builder -> {
            SimpleModule module = new SimpleModule("AutoActionTypeModule");

            ClassPathScanningCandidateComponentProvider scanner =
                new ClassPathScanningCandidateComponentProvider(false);

            scanner.addIncludeFilter(new AnnotationTypeFilter(ActionConf.class));

            Set<BeanDefinition> candidates = scanner.findCandidateComponents(BASE_ACTION_PACKAGE);

            for (BeanDefinition bd : candidates) {
                try {
                    Class<?> clazz = ClassUtils.forName(
                        Objects.requireNonNull(bd.getBeanClassName(), "ActionSubType must be not null"),
                        ClassUtils.getDefaultClassLoader()
                    );

                    if (BaseActionConfig.class.isAssignableFrom(clazz)) {
                        ActionConf ann = clazz.getAnnotation(ActionConf.class);

                        String typeName = ann.name().isEmpty() ? ann.value().name() : ann.name();

                        module.registerSubtypes(new NamedType(clazz, typeName));
                    }
                } catch (ClassNotFoundException e) {
                    throw new IllegalStateException("Failed to scan Action Config", e);
                }
            }

            builder.modules(module);
        };
    }
}
