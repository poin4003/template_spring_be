package com.app.features.sims.producer;

import java.util.UUID;

import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.KafkaHeaders;
import org.springframework.kafka.support.mapping.AbstractJavaTypeMapper;
import org.springframework.messaging.Message;
import org.springframework.messaging.support.MessageBuilder;
import org.springframework.stereotype.Component;

import com.app.features.sims.cqrs.command.CreateSimCmd;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component
@RequiredArgsConstructor
public class SimImportProducer {

    private static final String TOPIC_NAME = "import-sim-topic";
    private final KafkaTemplate<String, CreateSimCmd> kafkaTemplate;

    public void sendSimToImportQueue(CreateSimCmd cmd) {

        log.info("Sending SIM with phone number {} to kafka topic {}",
                cmd.phoneNumber(), TOPIC_NAME);

        String simKey = cmd.phoneNumber() + "-" + UUID.randomUUID();

        Message<CreateSimCmd> message = MessageBuilder
                .withPayload(cmd)
                .setHeader(KafkaHeaders.TOPIC, TOPIC_NAME)
                .setHeader(KafkaHeaders.KEY, simKey)
                .setHeader(
                    AbstractJavaTypeMapper.DEFAULT_CLASSID_FIELD_NAME,
                    CreateSimCmd.class.getSimpleName()
                )
                .build();

        kafkaTemplate.send(message);
    } 
}
