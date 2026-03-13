package com.app.features.ops.dlq;

import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.UUID;

import org.apache.kafka.clients.consumer.ConsumerRecord;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.kafka.support.KafkaHeaders;
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
import com.app.features.ops.entity.OpsConfigEntity;
import com.app.features.ops.enums.OpsTypeEnum;
import com.app.features.ops.repository.OpsConfigRepository;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class DlqDispatcher {

    private final ActionService actionService;
    private final ActionExecutorFactory actionExecutorFactory;

    private final OpsConfigRepository opsConfigRepo;
    private final SystemErrorDefinitionRepository errorRepo;

    @KafkaListener(topicPattern = ".*\\.DLQ", groupId = "${spring.kafka.consumer.group-id:dlq-dispatcher-group}")
    public void onDeadLetterMessage(ConsumerRecord<?, ?> record) {
        String originalTopic = "UNKNOWN";
        try {
            log.info("DLQ Activated | Topic: {}", record.topic());

            originalTopic = getHeaderString(record, KafkaHeaders.DLT_ORIGINAL_TOPIC);
            String exceptionFqcn = getHeaderString(record, KafkaHeaders.DLT_EXCEPTION_FQCN);
            String exceptionMsg = getHeaderString(record, KafkaHeaders.DLT_EXCEPTION_MESSAGE);

            if (!StringUtils.hasText(originalTopic) || "UNKNOWN".equals(originalTopic)) {
                log.warn("   -> Missing original topic header. Skipping.");
                return;
            }

            UUID targetId = lookupTargetIdBySourceName(originalTopic);
            if (targetId == null) {
                log.warn("   -> Unknown Topic [{}]. No endpoint configuration found.", originalTopic);
                return;
            }

            Integer errorCode = lookupErrorCodeByExceptionClass(exceptionFqcn);

            MatchedActionResult match = actionService.findBestMatch(targetId, errorCode,
                    ErrorCategoryEnum.INFRASTRUCTURE);

            if (match == null) {
                log.info("   -> No specific rule found. Default behavior.");
                return;
            }

            log.info("   -> MATCHED RULE: [{}] - Action Type: [{}]", match.getName(), match.getActionType());

            MqActionContext context = MqActionContext.builder()
                    .triggerSource("DLQ_LISTENER")
                    .mqRecord(record)
                    .originalTopic(originalTopic)
                    .exceptionClass(exceptionFqcn)
                    .exceptionMessage(exceptionMsg)
                    .build();

            actionExecutorFactory.executeAction(match.getActionConfig(), context);

        } catch (Exception e) {
            log.error("CRITICAL ERROR in DLQ Dispatcher for topic [{}].", originalTopic, e);
        }
    }

    private String getHeaderString(ConsumerRecord<?, ?> record, String key) {
        if (record.headers().lastHeader(key) != null) {
            return new String(record.headers().lastHeader(key).value(), StandardCharsets.UTF_8);
        }
        return "UNKNOW";
    }

    private UUID lookupTargetIdBySourceName(String sourceName) {
        return opsConfigRepo.findFirstByNameAndType(sourceName, OpsTypeEnum.MQ_BROKER_LISTENER)
                .map(OpsConfigEntity::getId)
                .orElse(null);
    }

    private Integer lookupErrorCodeByExceptionClass(String exceptionClass) {
        if (!StringUtils.hasText(exceptionClass)) {
            return ResultCode.ERROR.code();
        }

        // TODO: Nên dùng @Cacheable ở service layer cho hàm này để không query DB liên
        // tục
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
