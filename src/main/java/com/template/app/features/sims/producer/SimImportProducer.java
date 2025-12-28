package com.template.app.features.sims.producer;

import java.util.UUID;

import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.stereotype.Component;
import org.springframework.kafka.support.mapping.AbstractJavaTypeMapper;

import com.template.app.features.sims.service.schema.command.SimCmd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimImportProducer {

    private static final String TOPIC_NAME = "import-sim-topic";
    private final KafkaTemplate<String, SimCmd> kafkaTemplate;

    public void sendSimToImportQueue(SimCmd cmd) {

        log.info("Sending SIM with phone number {} to kafka topic {}",
                cmd.getSimPhoneNumber(), TOPIC_NAME);

        String simKey = cmd.getSimPhoneNumber() + "-" + UUID.randomUUID();

        Message<SimCmd> message = MessageBuilder
                .withPayload(cmd)
                .setHeader(KafkaHeaders.TOPIC, TOPIC_NAME)
                .setHeader(KafkaHeaders.KEY, simKey)
                .setHeader(
                    AbstractJavaTypeMapper.DEFAULT_CLASSID_FIELD_NAME,
                    SimCmd.class.getSimpleName()
                )
                .build();

        kafkaTemplate.send(message);
    } 
}
