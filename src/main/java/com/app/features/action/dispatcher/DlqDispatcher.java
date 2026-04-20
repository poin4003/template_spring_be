package com.app.features.action.dispatcher;

import java.nio.charset.StandardCharsets;
import java.util.List;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;

import com.app.core.response.ResultCode;
import com.app.features.action.executor.ActionExecutorFactory;
import com.app.features.action.executor.context.MqActionContext;
import com.app.features.action.service.ActionService;
import com.app.features.action.service.schema.result.MatchedActionResult;
import com.app.features.error.entity.SystemErrorDefinitionEntity;
import com.app.features.error.enums.ErrorCategoryEnum;
import com.app.features.error.repository.SystemErrorDefinitionRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DlqDispatcher {

    private final ActionService actionService;
    private final ActionExecutorFactory actionExecutorFactory;
    private final SystemErrorDefinitionRepository errorRepo;

    @KafkaListener(topicPattern = ".*\\.DLQ", groupId = "dlq-dispatcher-group")
    public void dispatch(ConsumerRecord<?, ?> record) {

        String originalTopic = getHeaderString(record, "x-original-topic");
        String exceptionClass = getHeaderString(record, "x-dlq-exception-class");

        log.info("Catch message for topic: {}", originalTopic);

        try {
            Integer errorCode = lookupErrorCodeByExceptionClass(exceptionClass);

            MatchedActionResult match = actionService.findBestMatch(originalTopic, errorCode,
                    ErrorCategoryEnum.INFRASTRUCTURE);

            if (match == null || match.getActionConfig() == null) {
                log.warn("Not found action rule for Topic: {} with error: {}", originalTopic, errorCode);
                return;
            }

            MqActionContext context = MqActionContext.builder()
                    .mqRecord(record) 
                    .originalTopic(originalTopic)
                    .exceptionClass(exceptionClass)
                    .exceptionMessage(getHeaderString(record, "x-dlq-reason"))
                    .triggerSource("KAFKA_DLQ") 
                    .build();

            actionExecutorFactory.executeAction(match.getActionConfig(), context);

        } catch (Exception e) {
            log.error("SERIOUS ERROR WHEN DISPATCH DLQ for topic [{}].", originalTopic, e);
        }
    }

    private String getHeaderString(ConsumerRecord<?, ?> record, String key) {
        if (record.headers().lastHeader(key) != null) {
            return new String(record.headers().lastHeader(key).value(), StandardCharsets.UTF_8);
        }
        return "UNKNOWN";
    }

    private Integer lookupErrorCodeByExceptionClass(String exceptionClass) {
        if (!StringUtils.hasText(exceptionClass)) {
            return ResultCode.ERROR.code();
        }

        List<SystemErrorDefinitionEntity> errorDefinitions = errorRepo.findAll();

        return errorDefinitions.stream()
                .filter(def -> def.getExceptionClassName() != null
                        && def.getExceptionClassName().getJava() != null
                        && exceptionClass.equals(def.getExceptionClassName().getJava().getClassName()))
                .map(SystemErrorDefinitionEntity::getCode)
                .findFirst()
                .orElse(ResultCode.ERROR.code());
    }
}
