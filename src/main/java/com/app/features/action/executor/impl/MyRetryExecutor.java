package com.app.features.action.executor.impl;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.stereotype.Component;

import com.app.features.action.executor.ActionExecutor;
import com.app.features.action.executor.context.BaseActionContext;
import com.app.features.action.executor.context.MqActionContext;
import com.app.features.action.vo.config.MqRetryActionConfig;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Component
@RequiredArgsConstructor
@Slf4j
public class MyRetryExecutor implements ActionExecutor<MqRetryActionConfig> {

    private final KafkaTemplate<String, Object> kafkaTemplate;

    @Override
    public Class<MqRetryActionConfig> getSupportedConfigClass() {
        return MqRetryActionConfig.class;
    }

    @Override
    public void execute(MqRetryActionConfig config, BaseActionContext context) {
        if (!(context instanceof MqActionContext)) {
            log.error("Invalid Context! MqRetryExecutor requires MqActionContext but got: {}",
                context.getClass().getSimpleName()
            );
            return;
        }

        MqActionContext mqContext = (MqActionContext) context;

        String originalSourceName = mqContext.getOriginalTopic();
        Object payload = mqContext.getMqRecord().value();

        if (originalSourceName == null) {
            log.error("Origin topic missing in MQ Context");
            return;
        }

        log.info(">>> RETRY ACTION: Re-publishing message to sourceName [{}] (MaxAttempts config: {})",
            originalSourceName, config.getMaxAttemps()
        );

        kafkaTemplate.send(originalSourceName, payload);
    }    
}
