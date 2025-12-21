package com.template.app.features.sims.config;

import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.autoconfigure.kafka.KafkaProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.DefaultErrorHandler;

import com.template.app.features.sims.service.schema.command.SimCmd;

@Configuration
@EnableKafka
@RequiredArgsConstructor
public class SimKafkaConfig {
    
    private final KafkaProperties kafkaProperties;
    private final DefaultErrorHandler commonErrorHandler;

    @Bean
    public ConsumerFactory<String, SimCmd> simConsumerFactory() { 
        Map<String, Object> configs = kafkaProperties.buildConsumerProperties();
        if (configs == null) throw new NullPointerException("Kafka Consumer Properties Map cannot be null.");
        return new DefaultKafkaConsumerFactory<>(configs);
    }

    @Bean
    @SuppressWarnings("null")
    public ConcurrentKafkaListenerContainerFactory<String, SimCmd>
        simKafkaListenerContainerFactory(ConsumerFactory<String, SimCmd> simConsumerFactory) {
        
        ConcurrentKafkaListenerContainerFactory<String, SimCmd> factory =
            new ConcurrentKafkaListenerContainerFactory<>();

        if (simConsumerFactory == null) {
            throw new NullPointerException("simConsumerFactory must be injected by Spring.");
        }
    
        factory.setConsumerFactory(simConsumerFactory);
         
        factory.setCommonErrorHandler(commonErrorHandler);

        return factory;
    }
}
