package com.template.app.core.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target(ElementType.TYPE)
@Retention(RetentionPolicy.RUNTIME)
public @interface RegisterMqConsumer {
    
    String topic();

    String group();

    boolean dlq() default false;

    /**
     * optional: custom dlq topic
     */
    String dlqTopic() default "";
}
