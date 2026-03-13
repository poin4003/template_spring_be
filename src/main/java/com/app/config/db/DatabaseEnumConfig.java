package com.app.config.db;

import java.util.Map;

import org.springframework.beans.factory.annotation.AnnotatedBeanDefinition;
import org.springframework.boot.autoconfigure.orm.jpa.HibernatePropertiesCustomizer;
import org.springframework.context.annotation.ClassPathScanningCandidateComponentProvider;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.type.filter.AnnotationTypeFilter;
import org.hibernate.boot.model.TypeContributions;
import org.hibernate.boot.model.TypeContributor;
import org.hibernate.service.ServiceRegistry;
import org.springframework.lang.NonNull;

import com.app.core.annotation.DatabaseEnum;
import com.app.core.constant.AppConstants;
import com.app.core.db.BaseEnumJavaType;
import com.app.base.BaseEnum;

@Configuration
public class DatabaseEnumConfig implements HibernatePropertiesCustomizer {
    @Override
    public void customize(Map<String, Object> hibernateProperties) {
        hibernateProperties.put("hibernate.type_contributors", new TypeContributor() {
            @Override
            public void contribute(TypeContributions typeContributions, ServiceRegistry serviceRegistry) {

                ClassPathScanningCandidateComponentProvider scanner = new ClassPathScanningCandidateComponentProvider(
                        false) {
                    @Override
                    protected boolean isCandidateComponent(@NonNull AnnotatedBeanDefinition beanDefinition) {
                        return beanDefinition.getMetadata().isIndependent();
                    }
                };

                scanner.addIncludeFilter(new AnnotationTypeFilter(DatabaseEnum.class));

                for (var bd : scanner.findCandidateComponents(AppConstants.BASE_PACKAGE)) {
                    try {
                        Class<?> clazz = Class.forName(bd.getBeanClassName());
                        if (BaseEnum.class.isAssignableFrom(clazz) && clazz.isEnum()) {
                            registerEnum(typeContributions, clazz);
                        }
                    } catch (ClassNotFoundException e) {
                        throw new RuntimeException("Failed to scan enum classes for JPA", e);
                    }
                }
            }
        });
    }

    @SuppressWarnings("unchecked")
    private <T extends Enum<T> & BaseEnum> void registerEnum(TypeContributions typeContributions, Class<?> clazz) {
        typeContributions.contributeJavaType(new BaseEnumJavaType<>((Class<T>) clazz));
    }
}
