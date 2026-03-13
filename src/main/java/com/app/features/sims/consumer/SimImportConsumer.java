package com.app.features.sims.consumer;

import org.springframework.stereotype.Component;

import com.app.core.exception.ExceptionFactory;
import com.app.core.mq.handler.MessageHandler;
import com.app.features.sims.cqrs.command.CreateSimCmd;

import an.awesome.pipelinr.Pipeline;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;

@Slf4j
@Component("simImportConsumer")
@RequiredArgsConstructor
public class SimImportConsumer implements MessageHandler<CreateSimCmd> {

    private final Pipeline pipeline;

    @Override
    public void handle(CreateSimCmd cmd) {
        if (cmd == null) {
            log.error("Received null request due to deserialization error. Skipping");
            return;
        }

        if ("9999999999".equals(cmd.phoneNumber())) {
            log.warn(">>> SIMULATION: Triggering Artificial Error for DLQ Test! <<<");
            throw ExceptionFactory.importSimError();
        }

        log.info("Received created Sim: {}", cmd);

        try {
            pipeline.send(cmd);
        } catch (Exception e) {
            log.error("Error processing SIM import for phone number {}: {}", cmd.phoneNumber(), e.getMessage());
            throw e;
        }
    }
}
